package kr.swyp.backend.authentication.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import kr.swyp.backend.authentication.dto.SocialLoginDto.ApplePublicKeyResponse;
import kr.swyp.backend.authentication.dto.SocialLoginDto.ApplePublicKeyResponse.ApplePublicKey;
import kr.swyp.backend.authentication.dto.SocialLoginDto.AppleTokenResponse;
import kr.swyp.backend.authentication.dto.SocialLoginDto.AppleTokenVerificationResult;
import kr.swyp.backend.authentication.dto.SocialLoginDto.AppleUserInfo;
import kr.swyp.backend.authentication.dto.SocialLoginDto.JwtHeader;
import kr.swyp.backend.authentication.service.AppleClientService;
import kr.swyp.backend.common.config.AppleProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleLoginUtil {

    private static final String APPLE_ISS = "https://appleid.apple.com";

    private final AppleClientService appleClientService;
    private final AppleProperties appleProperties;
    private final ObjectMapper objectMapper;

    public AppleTokenVerificationResult verifyAppleToken(String identityToken) {
        try {
            // JWT 헤더 파싱
            String encodedHeader = identityToken.split("\\.")[0];
            String decodedHeader = new String(Base64.getUrlDecoder().decode(encodedHeader));
            JwtHeader header = objectMapper.readValue(decodedHeader, JwtHeader.class);

            // Apple의 공개키 가져오기
            ApplePublicKeyResponse keyResponse = appleClientService.getAuthKey();

            // 적절한 공개키 찾기
            ApplePublicKey publicKey = keyResponse.getKeys().stream()
                    .filter(key -> key.getKid().equals(header.getKid()) && key.getAlg()
                            .equals(header.getAlg()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Matching public key not found"));

            // 공개키로 RSA 키 생성
            RSAPublicKey rsaPublicKey = getPublicKey(publicKey);

            // 토큰 검증 (JJWT 0.12.6 API 사용)
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(rsaPublicKey)
                    .requireIssuer(APPLE_ISS)
                    .requireAudience(appleProperties.getClientId())
                    .build()
                    .parseSignedClaims(identityToken);

            Claims claims = jws.getPayload();

            return AppleTokenVerificationResult.builder()
                    .valid(true)
                    .subject(claims.getSubject())
                    .email(claims.get("email", String.class))
                    .build();

        } catch (Exception e) {
            log.error("Apple 토큰 검증 실패: {}", e.getMessage(), e);
            return AppleTokenVerificationResult.builder()
                    .valid(false)
                    .build();
        }
    }

    public AppleUserInfo getUserInfoFromAuthCode(String authorizationCode) {
        try {

            String clientSecret = generateClientSecret();
            AppleTokenResponse tokenResponse = appleClientService.getTokenResponse(
                    appleProperties.getClientId(), clientSecret, authorizationCode,
                    "authorization_code");

            // ID 토큰에서 정보 추출
            String idToken = tokenResponse.getIdToken();
            String[] chunks = idToken.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));

            JsonNode payloadJson = objectMapper.readTree(payload);
            String email = payloadJson.has("email") ? payloadJson.get("email").asText() : null;
            boolean emailVerified =
                    payloadJson.has("email_verified") && payloadJson.get("email_verified")
                            .asBoolean();

            return AppleUserInfo.builder()
                    .email(email)
                    .emailVerified(emailVerified)
                    .build();

        } catch (Exception e) {
            log.error("Apple 사용자 정보 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("애플에서 유저 정보를 받아올 수 없습니다: " + e.getMessage(), e);
        }
    }

    private RSAPublicKey getPublicKey(ApplePublicKey publicKey) throws Exception {
        byte[] n = Base64.getUrlDecoder().decode(publicKey.getN());
        byte[] e = Base64.getUrlDecoder().decode(publicKey.getE());

        BigInteger modulus = new BigInteger(1, n);
        BigInteger exponent = new BigInteger(1, e);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }

    private String generateClientSecret() {
        try {
            // 현재 시간 및 만료 시간 (20일)
            Date now = new Date();
            Date expiration = new Date(now.getTime() + (20 * 24 * 60 * 60 * 1000L));

            PrivateKey privateKey = getPrivateKey();
            if (privateKey == null) {
                throw new RuntimeException("Apple 프라이빗 키를 로드할 수 없습니다");
            }
            return Jwts.builder()
                    .subject(appleProperties.getClientId())
                    .issuer(appleProperties.getTeamId())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + (30 * 24 * 60 * 60
                            * 1000L))) // 30일
                    .audience()
                    .add("https://appleid.apple.com")
                    .and()
                    .header()
                    .keyId(appleProperties.getKeyId())
                    .and()
                    .signWith(getPrivateKey(), SIG.ES256)
                    .compact();

        } catch (Exception e) {
            log.error("Apple 클라이언트 시크릿 생성 실패: {}", e.getMessage(), e);
            throw new RuntimeException("클라이언트 Secret을 생성하는 데에 실패하였습니다: " + e.getMessage(), e);
        }
    }

    public PrivateKey getPrivateKey() throws IOException {
        ClassPathResource resource = new ClassPathResource(appleProperties.getKeyPath());
        String privateKey = new String(Files.readAllBytes(Paths.get(resource.getURI())));
        Reader pemReader = new StringReader(privateKey);
        PEMParser pemParser = new PEMParser(pemReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        return converter.getPrivateKey(object);
    }
}
package kr.swyp.backend.authentication.service;

import kr.swyp.backend.authentication.client.AppleClient;
import kr.swyp.backend.authentication.dto.SocialLoginDto.ApplePublicKeyResponse;
import kr.swyp.backend.authentication.dto.SocialLoginDto.AppleTokenRequest;
import kr.swyp.backend.authentication.dto.SocialLoginDto.AppleTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppleClientServiceImpl implements AppleClientService {

    private final AppleClient appleClient;

    @Override
    public ApplePublicKeyResponse getAuthKey() {
        return appleClient.getAuthKey();
    }

    @Override
    public AppleTokenResponse getTokenResponse(String clientId, String clientSecret, String code,
            String grantType) {
        return appleClient.getTokenResponse(AppleTokenRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(code)
                .grantType(grantType)
                .build());
    }
}

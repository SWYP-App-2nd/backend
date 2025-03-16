package kr.swyp.backend.authentication.service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import kr.swyp.backend.authentication.domain.RefreshToken;
import kr.swyp.backend.authentication.dto.MemberInfo;
import kr.swyp.backend.authentication.dto.TokenDto.RefreshTokenInfoResponse;
import kr.swyp.backend.authentication.repository.RefreshTokenRepository;
import kr.swyp.backend.member.domain.Member;
import kr.swyp.backend.member.enums.RoleType;
import kr.swyp.backend.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final int refreshTokenValidityInDays;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
            MemberRepository memberRepository,
            @Value("${swyp.jwt.refresh-token-validity-in-days}")
            int refreshTokenValidityInDays) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberRepository = memberRepository;
        this.refreshTokenValidityInDays = refreshTokenValidityInDays;
    }
    
    @Override
    public MemberInfo getMemberInfoByRefreshTokenString(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenString)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 토큰입니다."));

        String username = getUsername(refreshToken.getMemberId(), refreshToken.getRoleType());

        return MemberInfo.builder()
                .memberId(refreshToken.getMemberId())
                .username(username)
                .roleType(refreshToken.getRoleType())
                .build();
    }

    @Override
    public void removeRefreshToken(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenString)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 토큰입니다."));

        refreshTokenRepository.delete(refreshToken);
    }

    @Override
    public RefreshTokenInfoResponse renew(UUID memberId, RoleType roleType) {
        // 새로운 토큰 생성.
        RefreshToken newToken = generateRefreshToken(memberId, roleType);

        // 새로운 토큰 저장.
        refreshTokenRepository.save(newToken);

        return RefreshTokenInfoResponse.builder()
                .refreshToken(newToken.getRefreshToken())
                .expiresAt(newToken.getExpiresAt())
                .build();
    }

    private String getUsername(UUID memberId, RoleType roleType) {
        if (roleType == RoleType.USER || roleType == RoleType.ADMIN
                || roleType == RoleType.SUPER_ADMIN) {
            return memberRepository.findByMemberIdAndIsActiveIsTrue(memberId)
                    .map(Member::getUsername)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        } else {
            throw new IllegalArgumentException("올바르지 않은 회원 타입입니다.");
        }
    }

    private RefreshToken generateRefreshToken(UUID memberId, RoleType roleType) {
        return RefreshToken.builder()
                .memberId(memberId)
                .roleType(roleType)
                .refreshToken(generateRefreshTokenString())
                .expiresAt(LocalDateTime.now().plusDays(refreshTokenValidityInDays))
                .timeToLive(refreshTokenValidityInDays)
                .build();
    }

    private String generateRefreshTokenString() {
        return new Random().ints(128, '0', 'z' + 1)
                .filter(Character::isLetterOrDigit)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}

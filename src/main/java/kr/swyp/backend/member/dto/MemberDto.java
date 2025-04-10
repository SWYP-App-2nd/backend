package kr.swyp.backend.member.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import kr.swyp.backend.member.domain.Member;
import kr.swyp.backend.member.domain.MemberNotificationSetting;
import kr.swyp.backend.member.domain.MemberSocialLoginInfo;
import kr.swyp.backend.member.enums.SocialLoginProviderType;
import lombok.Builder;
import lombok.Getter;

public class MemberDto {

    @Getter
    @Builder
    public static class MemberInfoResponse {

        private UUID memberId;
        private String username;
        private String nickname;
        private String imageUrl;
        private Integer averageRate;
        private Boolean isActive;
        private LocalDateTime marketingAgreedAt;
        private Boolean enablePush; // 푸시 수신 여부
        private SocialLoginProviderType providerType; // 소셜 로그인 제공자

        public static MemberInfoResponse fromEntity(Member member,
                MemberSocialLoginInfo socialLoginInfo,
                MemberNotificationSetting notificationSetting) {
            return MemberInfoResponse.builder()
                    .memberId(member.getMemberId())
                    .username(member.getUsername())
                    .nickname(member.getNickname())
                    .imageUrl(member.getImageUrl())
                    .averageRate(0) // 평균 챙김률은 초기에 0으로 설정
                    .isActive(member.getIsActive())
                    .marketingAgreedAt(member.getMarketingAgreedAt())
                    .enablePush(notificationSetting.getEnablePush())
                    .providerType(socialLoginInfo.getProviderType())
                    .build();
        }
    }
}


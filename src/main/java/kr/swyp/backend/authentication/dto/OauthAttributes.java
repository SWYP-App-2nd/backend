package kr.swyp.backend.authentication.dto;

import java.util.Map;
import kr.swyp.backend.member.enums.Oauth2ProviderType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OauthAttributes {

    private String nameAttributeKey;
    private Oauth2UserInfo oauth2UserInfo;

    @Builder
    public OauthAttributes(String nameAttributeKey, Oauth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    public static OauthAttributes from(Oauth2ProviderType providerType, String nameAttributeKey,
            Map<String, Object> attributes) {
        if (providerType == Oauth2ProviderType.KAKAO) {
            return ofKakao(nameAttributeKey, attributes);
        } else {
            throw new IllegalArgumentException("소셜 로그인 서비스 제공자가 올바르지 않습니다.");
        }
    }

    private static OauthAttributes ofKakao(String nameAttributeKey,
            Map<String, Object> attributes) {
        return OauthAttributes.builder()
                .nameAttributeKey(nameAttributeKey)
                .oauth2UserInfo(new KakaoOauth2UserInfo(attributes))
                .build();
    }

}

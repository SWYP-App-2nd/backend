package kr.swyp.backend.authentication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.swyp.backend.authentication.dto.MemberInfo;
import kr.swyp.backend.member.enums.SocialLoginProviderType;

public interface SocialLoginService {

    /**
     * 소셜 로그인 제공자가 카카오 일 때만 사용하는 메소드. 확장성을 위해서 providerType을 파라미터로 받는다.
     *
     * @param accessKey    소셜 로그인 제공자가 제공하는 accessKey
     * @param providerType 소셜 로그인 제공자 타입
     * @return 회원 정보
     */
    MemberInfo getMemberInfoByAccessKeyAndProviderType(String accessKey,
            SocialLoginProviderType providerType) throws JsonProcessingException;

    /**
     * 소셜 로그인 제공자가 애플 일 때 사용하는 메소드. 확장성을 위해서 providerType을 파라미터로 받는다.
     *
     * @param identityToken     소셜 로그인 제공자가 제공하는 identityToken
     * @param authorizationCode 소셜 로그인 제공자가 제공하는 authorizationCode
     * @param providerType      소셜 로그인 제공자 타입
     * @return 회원 정보
     */
    MemberInfo getMemberInfoByIdentityTokenAndAuthorizationCodeAndProviderType(String identityToken,
            String authorizationCode, SocialLoginProviderType providerType);
}

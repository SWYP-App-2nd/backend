package kr.swyp.backend.authentication.service;

import kr.swyp.backend.authentication.dto.SocialLoginDto.ApplePublicKeyResponse;
import kr.swyp.backend.authentication.dto.SocialLoginDto.AppleTokenResponse;

public interface AppleClientService {

    ApplePublicKeyResponse getAuthKey();

    AppleTokenResponse getTokenResponse(String clientId, String clientSecret, String code,
            String grantType);
}

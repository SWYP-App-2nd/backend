package kr.swyp.backend.authentication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.swyp.backend.authentication.dto.SocialLoginDto.KakaoSocialLoginResponse;

public interface KakaoClientService {

    KakaoSocialLoginResponse getAccessTokenInfo(String accessToken) throws JsonProcessingException;
}

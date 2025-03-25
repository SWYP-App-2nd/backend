package kr.swyp.backend.authentication.client;

import kr.swyp.backend.authentication.dto.SocialLoginDto.KakaoSocialLoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakao-client", url = "https://kapi.kakao.com")
public interface KakaoClient {

    @GetMapping(value = "/v2/user/me",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8")
    KakaoSocialLoginResponse getAccessTokenInfo(@RequestHeader("Authorization") String accessToken,
            @RequestParam("property_keys") String propertyKeys);
}

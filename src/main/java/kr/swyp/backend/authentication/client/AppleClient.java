package kr.swyp.backend.authentication.client;

import kr.swyp.backend.authentication.dto.SocialLoginDto.ApplePublicKeyResponse;
import kr.swyp.backend.authentication.dto.SocialLoginDto.AppleTokenRequest;
import kr.swyp.backend.authentication.dto.SocialLoginDto.AppleTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "apple-client", url = "https://appleid.apple.com")
public interface AppleClient {

    @GetMapping("/auth/keys")
    ApplePublicKeyResponse getAuthKey();

    @PostMapping(value = "/auth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    AppleTokenResponse getTokenResponse(@RequestBody AppleTokenRequest request);
}

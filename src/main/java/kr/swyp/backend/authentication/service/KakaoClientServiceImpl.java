package kr.swyp.backend.authentication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import kr.swyp.backend.authentication.client.KakaoClient;
import kr.swyp.backend.authentication.dto.SocialLoginDto.KakaoSocialLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoClientServiceImpl implements KakaoClientService {

    private final KakaoClient kakaoOauthClient;
    private final ObjectMapper objectMapper;

    @Override
    public KakaoSocialLoginResponse getAccessTokenInfo(String accessToken)
            throws JsonProcessingException {
        List<String> kakaoPropertyKeyList = List.of("kakao_account.profile");
        return kakaoOauthClient.getAccessTokenInfo("Bearer " + accessToken,
                objectMapper.writeValueAsString(kakaoPropertyKeyList));
    }
}

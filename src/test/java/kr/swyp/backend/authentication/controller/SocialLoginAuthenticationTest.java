package kr.swyp.backend.authentication.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.swyp.backend.authentication.dto.SocialLoginDto.KakaoSocialLoginResponse;
import kr.swyp.backend.authentication.dto.SocialLoginDto.KakaoSocialLoginResponse.KakaoAccount;
import kr.swyp.backend.authentication.dto.SocialLoginDto.KakaoSocialLoginResponse.KakaoAccount.Profile;
import kr.swyp.backend.authentication.dto.SocialLoginDto.SocialLoginRequest;
import kr.swyp.backend.authentication.service.KakaoClientServiceImpl;
import kr.swyp.backend.common.desciptor.ErrorDescriptor;
import kr.swyp.backend.member.enums.SocialLoginProviderType;
import kr.swyp.backend.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
public class SocialLoginAuthenticationTest {

    private final FieldDescriptor[] authenticationResponseDescriptor = {
            fieldWithPath("accessToken").description("엑세스 토큰"),
            fieldWithPath("refreshTokenInfo").description("갱신 토큰 정보"),
            fieldWithPath("refreshTokenInfo.token").description("갱신 토큰"),
            fieldWithPath("refreshTokenInfo.expiresAt").description("갱신 토큰 만료 시각"),
    };

    private final String url = "/auth/social";

    @MockitoBean
    private KakaoClientServiceImpl kakaoClientService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() throws JsonProcessingException {
        when(kakaoClientService.getAccessTokenInfo(Mockito.anyString())).thenReturn(
                KakaoSocialLoginResponse.builder()
                        .id(1L)
                        .kakaoAccount(KakaoAccount.builder()
                                .profile(Profile.builder()
                                        .nickname("test")
                                        .build())
                                .build())
                        .build());
    }

    @Test
    @DisplayName("카카오 로그인이 성공해야 한다.")
    void 카카오_로그인이_성공해야_한다() throws Exception {
        // given
        String accessToken = "test";

        var request = SocialLoginRequest.builder()
                .accessToken(accessToken)
                .providerType(SocialLoginProviderType.KAKAO)
                .build();

        // when
        ResultActions result = this.mockMvc.perform(
                post(url).content(
                                this.objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();

        // docs
        result.andDo(document("소셜 로그인 성공",
                "소셜 로그인 AccessToken과 Provider로 토큰을 발급한다.",
                "소셜 로그인 AccessToken과 Provider로 토큰을 발급",
                false,
                false,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("accessToken").description("소셜 로그인 Access Token"),
                        fieldWithPath("providerType").description("소셜 로그인 제공자 타입")
                ),
                responseFields(authenticationResponseDescriptor)));
    }


    @Test
    @DisplayName("지원하지 않는 소셜 로그인 제공자 타입이면 실패해야 한다.")
    void 지원하지_않는_소셜_로그인_제공자_타입이면_실패해야_한다() throws Exception {
        // given
        String accessToken = "test";

        // when
        ResultActions result = this.mockMvc.perform(
                post(url).content(
                                "{\"accessToken\":\"" + accessToken + "\",\"providerType\":\"ETC\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").isString())
                .andReturn();

        // docs
        result.andDo(document("소셜 로그인 실패",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("accessToken").description("소셜 로그인 Access Token"),
                        fieldWithPath("providerType").description("소셜 로그인 제공자 타입")
                ),
                responseFields(ErrorDescriptor.errorResponseFieldDescriptors)));
    }
}

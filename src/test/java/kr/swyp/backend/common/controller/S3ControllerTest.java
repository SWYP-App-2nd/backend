package kr.swyp.backend.common.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import kr.swyp.backend.authentication.provider.TokenProvider;
import kr.swyp.backend.common.dto.FileDto.FileDeleteRequest;
import kr.swyp.backend.common.dto.FileDto.FileUploadRequest;
import kr.swyp.backend.common.service.S3Service;
import kr.swyp.backend.member.dto.MemberDetails;
import kr.swyp.backend.member.enums.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
public class S3ControllerTest {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_VALUE_PREFIX = "Bearer ";

    private final String url = "/s3";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private S3Service s3Service;

    @MockitoBean
    private S3Client s3Client;

    @Test
    @DisplayName("파일 업로드 URL을 생성 할 수 있어야 한다.")
    void 파일_업로드_URL을_생성_할_수_있어야_한다() throws Exception {
        // given
        FileUploadRequest request = FileUploadRequest.builder()
                .fileName("test.jpg")
                .contentType("image/jpeg")
                .fileSize(1024L)
                .category("profile")
                .build();

        // when
        ResultActions result = mockMvc.perform(post(url)
                .header(AUTHORIZATION_HEADER,
                        AUTHORIZATION_VALUE_PREFIX + createAccessToken(UUID.randomUUID()
                        ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.preSignedUrl").isNotEmpty());

        // docs
        result.andDo(document("Object Storage에 파일 업로드",
                "업로드를 할 수 있는 preSignedUrl을 생성한다.",
                "업로드 전용 preSignedUrl 생성",
                false,
                false,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(AUTHORIZATION_HEADER).description("발급받은 JWT")),
                requestFields(
                        fieldWithPath("fileName").description("파일 이름"),
                        fieldWithPath("contentType").description("파일 타입"),
                        fieldWithPath("fileSize").description("파일 크기"),
                        fieldWithPath("category").description("파일 카테고리")
                ),
                responseFields(
                        fieldWithPath("preSignedUrl").description("preSignedUrl")
                )

        ));
    }

    @Test
    @DisplayName("파일 다운로드 URL을 생성 할 수 있어야 한다.")
    void 파일_다운로드_URL을_생성_할_수_있어야_한다() throws Exception {
        // given
        UUID memberId = UUID.randomUUID();
        FileUploadRequest request = FileUploadRequest.builder()
                .fileName("test.jpg")
                .contentType("image/jpeg")
                .fileSize(1024L)
                .category("profile")
                .build();

        s3Service.generatePreSignedUrlForUpload(memberId, request);

        // when
        ResultActions result = mockMvc.perform(get(url)
                .header(AUTHORIZATION_HEADER,
                        AUTHORIZATION_VALUE_PREFIX + createAccessToken(memberId
                        ))
                .param("category", request.getCategory())
                .param("fileName", request.getFileName()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.preSignedUrl").isNotEmpty());

        // docs
        result.andDo(document("Object Storage에 파일 다운로드",
                "다운로드를 할 수 있는 preSignedUrl을 생성한다.",
                "다운로드 전용 preSignedUrl 생성",
                false,
                false,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(AUTHORIZATION_HEADER).description("발급받은 JWT")),
                queryParameters(
                        parameterWithName("category").description("파일 카테고리"),
                        parameterWithName("fileName").description("파일 이름")
                ),
                responseFields(
                        fieldWithPath("preSignedUrl").description("preSignedUrl")
                )
        ));
    }

    @Test
    @DisplayName("파일을 삭제 할 수 있어야 한다.")
    void 파일을_삭제_할_수_있어야_한다() throws Exception {
        // given
        UUID memberId = UUID.randomUUID();
        s3Service.generatePreSignedUrlForUpload(memberId, FileUploadRequest.builder()
                .fileName("test.jpg")
                .contentType("image/jpeg")
                .fileSize(1024L)
                .category("profile")
                .build());

        FileDeleteRequest request = FileDeleteRequest.builder()
                .fileName("test.jpg")
                .category("profile")
                .build();

        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(
                any(DeleteObjectResponse.class));

        // when
        ResultActions result = mockMvc.perform(delete(url)
                .header(AUTHORIZATION_HEADER,
                        AUTHORIZATION_VALUE_PREFIX + createAccessToken(memberId
                        ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isNoContent());

        // docs
        result.andDo(document("Object Storage에 파일 삭제",
                "파일을 삭제한다.",
                "파일 삭제",
                false,
                false,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(AUTHORIZATION_HEADER).description("발급받은 JWT")),
                requestFields(
                        fieldWithPath("fileName").description("파일 이름"),
                        fieldWithPath("category").description("파일 카테고리")
                )
        ));
    }

    private String createAccessToken(UUID memberId) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(RoleType.USER.name()));
        MemberDetails memberDetails = new MemberDetails(memberId, "test", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, "",
                authorities);
        return tokenProvider.generateAccessToken(authentication);
    }
}
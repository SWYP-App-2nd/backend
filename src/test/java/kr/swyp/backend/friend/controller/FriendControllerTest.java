package kr.swyp.backend.friend.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import kr.swyp.backend.authentication.provider.TokenProvider;
import kr.swyp.backend.common.dto.FileDto.FileUploadRequest;
import kr.swyp.backend.friend.domain.FriendContactFrequency;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListRequest;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListRequest.FriendRequest;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListRequest.FriendRequest.FriendAnniversaryCreateRequest;
import kr.swyp.backend.friend.enums.FriendContactWeek;
import kr.swyp.backend.friend.enums.FriendSource;
import kr.swyp.backend.member.dto.MemberDetails;
import kr.swyp.backend.member.enums.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
class FriendControllerTest {

    private final FieldDescriptor[] friendInitRequestDescriptor = {
            fieldWithPath("friendList[].name").description("친구 이름"),
            fieldWithPath("friendList[].source").description("연락처 출처"),
            fieldWithPath("friendList[].contactFrequency.contactWeek").description("연락 주기"),
            fieldWithPath("friendList[].contactFrequency.dayOfWeek").description("연락 요일"),
            fieldWithPath("friendList[].imageUploadRequest").description("파일 업로드 요청"),
            fieldWithPath("friendList[].imageUploadRequest.fileName").description("파일 이름"),
            fieldWithPath("friendList[].imageUploadRequest.contentType").description("파일 타입"),
            fieldWithPath("friendList[].imageUploadRequest.fileSize").description("파일 크기"),
            fieldWithPath("friendList[].imageUploadRequest.category").description("파일 카테고리"),
            fieldWithPath("friendList[].anniversary").description("기념일"),
            fieldWithPath("friendList[].anniversary.title").description("기념일 제목"),
            fieldWithPath("friendList[].anniversary.date").description("기념일 날짜"),
            fieldWithPath("friendList[].phone").description("전화번호"),
    };

    private final FieldDescriptor[] friendInitResponseDescriptor = {
            fieldWithPath("friendList[].friendId").description("친구 ID"),
            fieldWithPath("friendList[].name").description("친구 이름"),
            fieldWithPath("friendList[].source").description("연락처 출처"),
            fieldWithPath("friendList[].contactFrequency").description("연락 주기"),
            fieldWithPath("friendList[].contactFrequency.contactWeek").description("연락 주기"),
            fieldWithPath("friendList[].contactFrequency.dayOfWeek").description("연락 요일"),
            fieldWithPath("friendList[].anniversary.title").description("기념일 제목"),
            fieldWithPath("friendList[].anniversary.date").description("기념일 날짜"),
            fieldWithPath("friendList[].nextContactAt").description("다음 연락 날짜"),
            fieldWithPath("friendList[].preSignedImageUrl").description("프리사인드 이미지 URL"),
            fieldWithPath("friendList[].phone").description("전화번호"),
    };

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_VALUE_PREFIX = "Bearer ";

    private final String url = "/friend";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("친구를 추가할 수 있어야 한다.")
    void 친구를_추가할_수_있어야_한다() throws Exception {
        // given
        FriendCreateListRequest friendCreateListRequest =
                FriendCreateListRequest.builder()
                        .friendList(List.of(FriendRequest.builder()
                                        .name("test")
                                        .source(FriendSource.KAKAO)
                                        .contactFrequency(FriendContactFrequency.builder()
                                                .contactWeek(FriendContactWeek.EVERY_WEEK)
                                                .dayOfWeek(DayOfWeek.MONDAY)
                                                .build())
                                        .imageUploadRequest(FileUploadRequest.builder()
                                                .fileName("test.jpg")
                                                .contentType("image/jpeg")
                                                .fileSize(1024L)
                                                .category("test")
                                                .build())
                                        .anniversary(FriendAnniversaryCreateRequest.builder()
                                                .title("test")
                                                .date(LocalDate.now())
                                                .build())
                                        .phone("01000000000")
                                        .build(),
                                FriendRequest.builder()
                                        .name("test")
                                        .source(FriendSource.APPLE)
                                        .contactFrequency(FriendContactFrequency.builder()
                                                .contactWeek(FriendContactWeek.EVERY_WEEK)
                                                .dayOfWeek(DayOfWeek.MONDAY)
                                                .build())
                                        .imageUploadRequest(FileUploadRequest.builder()
                                                .fileName("test.jpg")
                                                .contentType("image/jpeg")
                                                .fileSize(1024L)
                                                .category("test")
                                                .build())
                                        .anniversary(FriendAnniversaryCreateRequest.builder()
                                                .title("test")
                                                .date(LocalDate.now())
                                                .build())
                                        .phone("01000000000")
                                        .build()
                        )).build();

        // when
        ResultActions result = mockMvc.perform(post(url + "/init")
                .header(AUTHORIZATION_HEADER,
                        AUTHORIZATION_VALUE_PREFIX + createAccessToken(UUID.randomUUID()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(friendCreateListRequest)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.friendList").isNotEmpty())
                .andExpect(jsonPath("$.friendList[0].friendId").isNotEmpty())
                .andExpect(jsonPath("$.friendList[1].friendId").isNotEmpty());

        // docs
        result.andDo(document("친구 초기 설정",
                "친구를 초기 설정한다.",
                "친구 초기 설정",
                false,
                false,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(AUTHORIZATION_HEADER).description("발급받은 JWT")),
                requestFields(friendInitRequestDescriptor),
                responseFields(friendInitResponseDescriptor)
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
package kr.swyp.backend.friend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import kr.swyp.backend.common.dto.FileDto.FileUploadRequest;
import kr.swyp.backend.friend.domain.FriendContactFrequency;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListRequest;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListRequest.FriendRequest;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListRequest.FriendRequest.FriendAnniversaryCreateRequest;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListResponse;
import kr.swyp.backend.friend.enums.FriendContactWeek;
import kr.swyp.backend.friend.enums.FriendRelation;
import kr.swyp.backend.friend.enums.FriendSource;
import kr.swyp.backend.friend.repository.FriendRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class FriendServiceImplTest {

    @Autowired
    private FriendServiceImpl friendService;

    @Autowired
    private FriendRepository friendRepository;

    @Test
    @DisplayName("친구를 추가할 수 있어야 한다.")
    void 친구를_추가할_수_있어야_한다() {
        // given
        UUID memberId = UUID.randomUUID();

        FriendCreateListRequest friendCreateListRequest =
                FriendCreateListRequest.builder()
                        .friendList(List.of(FriendRequest.builder()
                                        .name("test")
                                        .source(FriendSource.KAKAO)
                                        .relation(FriendRelation.FRIEND)
                                        .birthDay(LocalDate.now())
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
                                        .memo("test")
                                        .build(),
                                FriendRequest.builder()
                                        .name("test")
                                        .relation(FriendRelation.FRIEND)
                                        .birthDay(LocalDate.now())
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
                                        .memo("test")
                                        .build()
                        )).build();

        // when
        FriendCreateListResponse response = friendService.init(memberId, friendCreateListRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getFriendList().get(0).getPreSignedImageUrl()).isNotNull();
    }
}
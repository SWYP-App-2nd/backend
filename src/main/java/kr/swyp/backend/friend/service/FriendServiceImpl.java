package kr.swyp.backend.friend.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import kr.swyp.backend.common.domain.File;
import kr.swyp.backend.common.service.S3Service;
import kr.swyp.backend.friend.domain.Friend;
import kr.swyp.backend.friend.domain.FriendAnniversary;
import kr.swyp.backend.friend.domain.FriendDetail;
import kr.swyp.backend.friend.domain.FriendDetail.FriendDetailBuilder;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListRequest;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListRequest.FriendRequest.FriendAnniversaryCreateRequest;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListResponse;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListResponse.FriendResponse;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListResponse.FriendResponse.FriendAnniversaryCreateResponse;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListResponse.FriendResponse.FriendResponseBuilder;
import kr.swyp.backend.friend.repository.FriendAnniversaryRepository;
import kr.swyp.backend.friend.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final S3Service s3Service;
    private final FriendRepository friendRepository;
    private final FriendAnniversaryRepository friendAnniversaryRepository;

    @Override
    @Transactional
    public FriendCreateListResponse init(UUID memberId, FriendCreateListRequest request) {
        AtomicInteger position = new AtomicInteger(0); // 시작 값을 0으로 설정

        List<FriendResponse> friendResponseList = request.getFriendList().stream()
                .map(friendRequest -> {
                    Friend friend = Friend.builder()
                            .memberId(memberId)
                            .name(friendRequest.getName())
                            .friendSource(friendRequest.getSource())
                            .contactFrequency(friendRequest.getContactFrequency())
                            .position(position.getAndIncrement())
                            .nextContactAt(
                                    friendRequest.getNextContactAt(
                                            friendRequest.getContactFrequency()))
                            .build();

                    friendRepository.save(friend);

                    FriendDetailBuilder friendDetailBuilder = FriendDetail.builder()
                            .friend(friend)
                            .phone(friendRequest.getPhone());

                    FriendResponseBuilder friendResponseBuilder = FriendResponse.builder()
                            .friendId(friend.getFriendId())
                            .name(friend.getName())
                            .source(friend.getFriendSource())
                            .contactFrequency(friend.getContactFrequency())
                            .nextContactAt(friend.getNextContactAt());

                    if (friendRequest.getImageUploadRequest() != null) {
                        String preSignedUrl = s3Service.generatePreSignedUrlForUpload(memberId,
                                friendRequest.getImageUploadRequest()).getPreSignedUrl();
                        File file = s3Service.createFile(memberId,
                                friendRequest.getImageUploadRequest());
                        friendDetailBuilder.imageFileId(file.getId());
                        friendResponseBuilder.preSignedImageUrl(preSignedUrl);
                    }

                    friend.addFriendDetail(friendDetailBuilder.build());
                    friendRepository.save(friend);

                    if (friendRequest.getAnniversary() != null) {
                        FriendAnniversary friendAnniversary = friendAnniversaryRepository.save(
                                FriendAnniversaryCreateRequest.toEntity(friend.getFriendId(),
                                        friendRequest.getAnniversary()));
                        friendResponseBuilder.anniversary(
                                FriendAnniversaryCreateResponse.fromEntity(friendAnniversary));
                    }

                    if (friendRequest.getPhone() != null) {
                        friendResponseBuilder.phone(friend.getFriendDetail().getPhone());
                    }
                    return friendResponseBuilder.build();
                }).toList();

        return FriendCreateListResponse.builder()
                .friendList(friendResponseList)
                .build();
    }
}

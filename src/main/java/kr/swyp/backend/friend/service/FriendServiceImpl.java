package kr.swyp.backend.friend.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import kr.swyp.backend.common.domain.File;
import kr.swyp.backend.common.repository.FileRepository;
import kr.swyp.backend.common.service.S3Service;
import kr.swyp.backend.friend.domain.Friend;
import kr.swyp.backend.friend.domain.FriendAnniversary;
import kr.swyp.backend.friend.domain.FriendCheckingLog;
import kr.swyp.backend.friend.domain.FriendDetail;
import kr.swyp.backend.friend.domain.FriendDetail.FriendDetailBuilder;
import kr.swyp.backend.friend.dto.FriendDto.FriendCheckLogResponse;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListRequest;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListRequest.FriendRequest.FriendAnniversaryCreateRequest;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListResponse;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListResponse.FriendResponse;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListResponse.FriendResponse.FriendAnniversaryCreateResponse;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListResponse.FriendResponse.FriendResponseBuilder;
import kr.swyp.backend.friend.dto.FriendDto.FriendListResponse;
import kr.swyp.backend.friend.repository.FriendAnniversaryRepository;
import kr.swyp.backend.friend.repository.FriendCheckingLogRepository;
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
    private final FriendCheckingLogRepository friendCheckingLogRepository;
    private final FileRepository fileRepository;

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

    @Override
    @Transactional
    public void recordCheckAndUpdateRate(UUID memberId, UUID friendId) {
        // Friend 엔티티 조회
        Friend friend = friendRepository.findByFriendIdAndMemberId(friendId, memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 친구를 찾을 수 없습니다."));

        // 체크 로그 생성 및 저장
        FriendCheckingLog log = FriendCheckingLog.of(friend, true);
        friendCheckingLogRepository.save(log);

        // 체크율 계산
        Integer checkCount = friendCheckingLogRepository.countCheckedLogsByFriendId(friendId);
        int alarmTriggerCount = friend.getAlarmTriggerCount();
        int checkRate = 0;

        if (alarmTriggerCount > 0) {
            checkRate = (int) Math.round(((double) checkCount / alarmTriggerCount) * 100);
        }

        // 체크율 업데이트
        friend.updateCheckRate(checkRate);
        friendRepository.save(friend);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendCheckLogResponse> getCheckLogs(UUID memberId, UUID friendId) {

        // Friend 엔티티 조회
        Friend friend = friendRepository.findByFriendIdAndMemberId(friendId, memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 친구를 찾을 수 없습니다."));

        List<FriendCheckingLog> logs = friendCheckingLogRepository.findByFriend_FriendId(
                friend.getFriendId());

        return logs.stream()
                .map(FriendCheckLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateAlarmCheck(UUID memberId, UUID friendId) {
        Friend friend = friendRepository.findByFriendIdAndMemberId(friendId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 친구를 찾을 수 없습니다."));
        friend.updateAlarmTriggerCount();
        friendRepository.save(friend);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendListResponse> getFriendList(UUID memberId) {
        List<Friend> friends = friendRepository.findAllByMemberIdWithDetail(memberId);

        return friends.stream()
                .map(friend -> {
                    String imageUrl = Optional.ofNullable(friend.getFriendDetail())
                            .filter(detail -> detail.getImageFileId() != null)
                            .flatMap(detail -> fileRepository.findById(detail.getImageFileId()))
                            .map(imageFile -> s3Service.generatePreSignedUrlForDownload(
                                    memberId,
                                    imageFile.getCategory(),
                                    imageFile.getFileName()
                            ).getPreSignedUrl())
                            .orElse(null);

                    return FriendListResponse.fromEntity(friend, imageUrl);
                })
                .toList();
    }
}


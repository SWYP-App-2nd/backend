package kr.swyp.backend.friend.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
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
import kr.swyp.backend.friend.dto.FriendDto.FriendDetailResponse;
import kr.swyp.backend.friend.dto.FriendDto.FriendDetailUpdateRequest;
import kr.swyp.backend.friend.dto.FriendDto.FriendDetailUpdateRequest.FriendAnniversaryDetailUpdateRequest;
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
                            .phone(friendRequest.getPhone())
                            .relation(friendRequest.getRelation())
                            .birthday(friendRequest.getBirthDay())
                            .memo(friendRequest.getMemo());

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
                        friendResponseBuilder.fileName(file.getFileName());
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
                    if (friendRequest.getMemo() != null) {
                        friendResponseBuilder.memo(friend.getFriendDetail().getMemo());
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
                .toList();
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
                    File imageFile = Optional.ofNullable(friend.getFriendDetail())
                            .filter(detail -> detail.getImageFileId() != null)
                            .flatMap(detail -> fileRepository.findById(detail.getImageFileId()))
                            .orElse(null);

                    String fileName = imageFile != null ? imageFile.getFileName() : null;
                    String imageUrl = imageFile != null ? s3Service.generatePreSignedUrlForDownload(
                            memberId,
                            imageFile.getCategory(),
                            imageFile.getFileName()
                    ).getPreSignedUrl() : null;

                    return FriendListResponse.fromEntity(friend, imageUrl, fileName);
                })
                .toList();
    }

    @Override
    @Transactional
    public void updateFriendPosition(UUID memberId, UUID friendId, int newPosition) {
        Friend friend = friendRepository.findByFriendIdAndMemberId(friendId, memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 친구를 찾을 수 없습니다."));
        friend.updatePosition(newPosition);
    }

    @Override
    @Transactional(readOnly = true)
    public FriendDetailResponse getFriendDetail(UUID memberId, UUID friendId) {
        Friend friend = friendRepository.findByFriendIdAndMemberId(friendId, memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 친구를 찾을 수 없습니다."));

        File imageFile = Optional.ofNullable(friend.getFriendDetail())
                .filter(detail -> detail.getImageFileId() != null)
                .flatMap(detail -> fileRepository.findById(detail.getImageFileId()))
                .orElse(null);

        String imageUrl = imageFile != null ? s3Service.generatePreSignedUrlForDownload(
                memberId,
                imageFile.getCategory(),
                imageFile.getFileName()
        ).getPreSignedUrl() : null;

        List<FriendAnniversary> friendAnniversaryList = friendAnniversaryRepository
                .findByFriendId(friend.getFriendId());

        return FriendDetailResponse.fromEntity(friend, imageUrl, friend.getFriendDetail(),
                friendAnniversaryList);
    }

    @Override
    @Transactional
    public FriendDetailResponse updateFriend(UUID memberId, UUID friendId,
            FriendDetailUpdateRequest request) {
        Friend friend = friendRepository.findByFriendIdAndMemberId(friendId, memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 친구를 찾을 수 없습니다."));

        List<FriendAnniversary> friendAnniversaryList = friendAnniversaryRepository
                .findByIdIsIn(request.getAnniversaryList().stream()
                        .map(FriendAnniversaryDetailUpdateRequest::getId)
                        .toList());

        // Friend 업데이트
        friend.updateName(request.getName());
        friend.updateFriendContactFrequency(request.getContactFrequency());

        // FriendAnniversaryList 업데이트
        request.getAnniversaryList().forEach(anniversaryRequest -> {
            FriendAnniversary anniversaryToUpdate = friendAnniversaryList.stream()
                    .filter(anniversary -> anniversary.getId().equals(anniversaryRequest.getId()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("해당 기념일을 찾을 수 없습니다."));

            // 기념일 정보 업데이트
            anniversaryToUpdate.updateDate(anniversaryRequest.getDate());
            anniversaryToUpdate.updateTitle(anniversaryRequest.getTitle());
        });

        FriendDetail friendDetail = friend.getFriendDetail();

        // FriendDetail 업데이트
        friendDetail.updateRelation(request.getRelation());
        friendDetail.updateBirthday(request.getBirthday());
        friendDetail.updateMemo(request.getMemo());
        friendDetail.updatePhone(request.getPhone());

        return FriendDetailResponse.fromEntity(friend, null, friendDetail,
                friendAnniversaryList);
    }

    @Override
    @Transactional
    public void deleteFriend(UUID memberId, UUID friendId) {
        friendAnniversaryRepository.deleteByFriendId(friendId);
        friendRepository.deleteById(friendId);
    }
}


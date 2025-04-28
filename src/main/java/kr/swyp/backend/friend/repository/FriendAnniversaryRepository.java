package kr.swyp.backend.friend.repository;

import java.util.List;
import java.util.UUID;
import kr.swyp.backend.friend.domain.FriendAnniversary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendAnniversaryRepository extends JpaRepository<FriendAnniversary, Long> {

    List<FriendAnniversary> findByFriendId(UUID friendId);

    List<FriendAnniversary> findByIdIsIn(List<Long> idList);

    List<FriendAnniversary> findByFriendIdIsIn(List<UUID> idList);

    void deleteByFriendId(UUID friendId);
}

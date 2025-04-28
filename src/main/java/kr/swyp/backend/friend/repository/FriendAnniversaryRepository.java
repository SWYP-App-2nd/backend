package kr.swyp.backend.friend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import kr.swyp.backend.friend.domain.FriendAnniversary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendAnniversaryRepository extends JpaRepository<FriendAnniversary, Long> {

    List<FriendAnniversary> findByFriendId(UUID friendId);

    List<FriendAnniversary> findByIdIsIn(List<Long> idList);

    List<FriendAnniversary> findAllByFriendIdIsInAndCreatedAtBetween(List<UUID> friendIds,
            LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    void deleteByFriendId(UUID friendId);

    @Query("""
            SELECT fa
            FROM FriendAnniversary fa
            WHERE fa.friendId IN
                (SELECT DISTINCT fcl.friend.friendId
                  FROM FriendCheckingLog fcl
                  WHERE fcl.id IN :checkingLogIdList)
            """)
    List<FriendAnniversary> findAllAnniversaryByCheckingLogIdList(
            @Param("checkingLogIdList") List<Long> checkingLogIdList);
}

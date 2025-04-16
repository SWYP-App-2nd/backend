package kr.swyp.backend.friend.repository;

import java.util.List;
import java.util.UUID;
import kr.swyp.backend.friend.domain.FriendCheckingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendCheckingLogRepository extends JpaRepository<FriendCheckingLog, Long> {

    @Query("""
            SELECT COUNT(f)
            FROM FriendCheckingLog f
            WHERE f.friend.friendId = :friendId
            AND f.isChecked = true
            """)
    Integer countCheckedLogsByFriendId(@Param("friendId") UUID friendId);

    List<FriendCheckingLog> findByFriend_FriendId(UUID friendId);

}
package kr.swyp.backend.friend.repository;

import feign.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kr.swyp.backend.friend.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FriendRepository extends JpaRepository<Friend, UUID> {

    @Query("""
            SELECT AVG(f.checkRate)
            FROM Friend f
            WHERE f.memberId = :memberId
            """)
    Double findAverageCheckRateByMemberId(@Param("memberId") UUID memberId);

    Optional<Friend> findByFriendIdAndMemberId(UUID friendId, UUID memberId);

    @Query("""
            SELECT f FROM Friend f
            JOIN FETCH f.friendDetail fd
            WHERE f.memberId = :memberId
            ORDER BY f.position ASC
            """)
    List<Friend> findAllByMemberIdWithDetail(@Param("memberId") UUID memberId);
}


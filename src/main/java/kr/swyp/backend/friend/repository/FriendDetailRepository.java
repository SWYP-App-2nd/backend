package kr.swyp.backend.friend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import kr.swyp.backend.friend.domain.FriendDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendDetailRepository extends JpaRepository<FriendDetail, Long> {

    List<FriendDetail> findAllByFriend_MemberIdAndBirthdayBetween(UUID friendMemberId,
            LocalDate birthdayAfter, LocalDate birthdayBefore);

    @Query("""
            SELECT fd
            FROM FriendDetail fd
            WHERE fd.friend.memberId = :memberId
            AND fd.birthday IS NOT NULL
            AND fd.birthday BETWEEN :birthdayAfter AND :birthdayBefore
            AND EXISTS (
                SELECT 1
                FROM FriendCheckingLog fcl
                WHERE fcl.friend.friendId = fd.friend.friendId
                AND fcl.isChecked = true
            )
            """)
    List<FriendDetail> findBirthdaysWithCheckedLogs(
            @Param("memberId") UUID memberId,
            @Param("birthdayAfter") LocalDate birthdayAfter,
            @Param("birthdayBefore") LocalDate birthdayBefore);

}

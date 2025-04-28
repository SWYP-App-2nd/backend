package kr.swyp.backend.friend.repository;

import java.util.List;
import java.util.UUID;
import kr.swyp.backend.friend.domain.FriendDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendDetailRepository extends JpaRepository<FriendDetail, Long> {

    List<FriendDetail> findByFriend_MemberId(UUID memberId);
}

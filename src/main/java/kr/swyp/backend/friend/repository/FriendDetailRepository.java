package kr.swyp.backend.friend.repository;

import kr.swyp.backend.friend.domain.FriendDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendDetailRepository extends JpaRepository<FriendDetail, Long> {

}

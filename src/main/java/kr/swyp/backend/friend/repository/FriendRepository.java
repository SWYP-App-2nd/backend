package kr.swyp.backend.friend.repository;

import java.util.UUID;
import kr.swyp.backend.friend.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, UUID> {

}

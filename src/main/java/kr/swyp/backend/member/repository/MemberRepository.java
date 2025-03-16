package kr.swyp.backend.member.repository;

import java.util.Optional;
import java.util.UUID;
import kr.swyp.backend.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByUsername(String username);

    Optional<Member> findByMemberIdAndIsActiveIsTrue(UUID memberId);
}

package kr.swyp.backend.member.repository;

import java.util.Optional;
import kr.swyp.backend.member.domain.Member;
import kr.swyp.backend.member.domain.MemberSocialLoginInfo;
import kr.swyp.backend.member.enums.SocialLoginProviderType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberSocialLoginInfoRepository extends
        JpaRepository<MemberSocialLoginInfo, Long> {

    @EntityGraph(attributePaths = {"member"})
    Optional<MemberSocialLoginInfo> findByProviderIdAndProviderType(
            String providerId, SocialLoginProviderType providerType);

    Optional<MemberSocialLoginInfo> findByMember(Member member);

    void deleteByMember(Member member);
}

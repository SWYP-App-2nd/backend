package kr.swyp.backend.member.repository;

import java.util.Optional;
import kr.swyp.backend.member.domain.MemberOauthInfo;
import kr.swyp.backend.member.enums.Oauth2ProviderType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberOauthInfoRepository extends JpaRepository<MemberOauthInfo, Long> {

    @EntityGraph(attributePaths = {"member"})
    Optional<MemberOauthInfo> findByProviderTypeAndProviderId(Oauth2ProviderType providerType,
            String providerId);
}

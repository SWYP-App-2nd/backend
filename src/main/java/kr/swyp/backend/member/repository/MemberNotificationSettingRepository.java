package kr.swyp.backend.member.repository;

import java.util.Optional;
import java.util.UUID;
import kr.swyp.backend.member.domain.MemberNotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberNotificationSettingRepository extends
        JpaRepository<MemberNotificationSetting, Long> {

    Optional<MemberNotificationSetting> findByMemberId(UUID memberId);
}

package kr.swyp.backend.member.repository;

import kr.swyp.backend.member.domain.MemberWithdrawalLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberWithdrawalLogRepository extends JpaRepository<MemberWithdrawalLog, Long> {

}

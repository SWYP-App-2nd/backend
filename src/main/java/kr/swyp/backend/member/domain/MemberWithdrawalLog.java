package kr.swyp.backend.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import kr.swyp.backend.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER_WITHDRAWAL_LOG")
public class MemberWithdrawalLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @Column(name = "MEMBER_ID")
    @Comment("탈퇴한 사용자 ID")
    private UUID memberId;

    @NotNull
    @Column(name = "REASON_TYPE")
    @Comment("선택한 탈퇴 사유")
    private String reasonType;

    @Column(name = "CUSTOM_REASON")
    @Comment("사용자 입력 사유 (선택형이 ETC인 경우)")
    private String customReason;

}
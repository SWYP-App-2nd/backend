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
@AllArgsConstructor
@Table(name = "MEMBER_TERMS_AGREEMENT")
public class MemberTermsAgreement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @Column(name = "MEMBER_ID")
    @Comment("회원 UUID (FK)")
    private UUID memberId;

    @NotNull
    @Column(name = "TERMS_ID")
    @Comment("약관 ID (FK)")
    private Long termsId;

    @NotNull
    @Column(name = "IS_AGREED")
    @Comment("약관 동의 여부")
    private boolean isAgreed;
}

package kr.swyp.backend.friend.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import kr.swyp.backend.common.domain.BaseEntity;
import kr.swyp.backend.friend.enums.FriendSource;
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
@Table(name = "FRIEND")
public class Friend extends BaseEntity {

    @Id
    @Column(name = "FRIEND_ID", columnDefinition = "BINARY(16)")
    private UUID friendId;

    @Column(name = "MEMBER_ID")
    @Comment("회원")
    @NotNull
    private UUID memberId;  // MEMBER 테이블 참조

    @Column(name = "NAME")
    @Comment("친구 이름")
    @NotNull
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "FRIEND_SOURCE")
    @Comment("카카오 친구 or 연락처 친구")
    private FriendSource friendSource;

    @Column(name = "CONTACT_FREQUENCY")
    @Comment("연락 주기")
    @NotNull
    private String contactFrequency;

    @Column(name = "POSITION")
    @Comment("가까운 인연들 노출 순서")
    private Integer position;

    @Column(name = "NEXT_CONTACT_AT")
    @Comment("다음 챙김 예정일")
    private LocalDate nextContactAt;

    @Column(name = "CHECK_RATE")
    @Comment("친구별 챙김 체크율 (0~100%)")
    private Integer checkRate;

    @OneToOne(mappedBy = "friend", cascade = CascadeType.ALL, orphanRemoval = true)
    private FriendDetail friendDetail;

    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendCheckSchedule> checkSchedules = new ArrayList<>();

}

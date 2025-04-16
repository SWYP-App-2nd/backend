package kr.swyp.backend.friend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.swyp.backend.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "FRIEND_CHECKING_LOG")
public class FriendCheckingLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FRIEND_ID")
    @Comment("FRIEND 테이블 외래키")
    private Friend friend;

    @Default
    @Column(name = "IS_CHECKED")
    @Comment("해당 날짜에 사용자가 체크했는지 여부")
    private Boolean isChecked = false;

    public static FriendCheckingLog of(Friend friend, Boolean isChecked) {
        return FriendCheckingLog.builder()
                .friend(friend)
                .isChecked(isChecked)
                .build();
    }
}

package kr.swyp.backend.friend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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
@Table(name = "FRIEND_ANNIVERSARY")
public class FriendAnniversary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @Column(name = "FRIEND_ID")
    @Comment("친구 ID")
    private UUID friendId;

    @NotNull
    @Column(name = "TITLE")
    @Comment("기념일 이름 (예: 결혼기념일, 첫 만남)")
    private String title;

    @NotNull
    @Column(name = "DATE")
    @Comment("기념일 날짜")
    private LocalDate date;
}

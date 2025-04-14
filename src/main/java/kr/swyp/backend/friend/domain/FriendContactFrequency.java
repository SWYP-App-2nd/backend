package kr.swyp.backend.friend.domain;

import java.time.DayOfWeek;
import kr.swyp.backend.friend.enums.FriendContactWeek;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class FriendContactFrequency {

    FriendContactWeek contactWeek;
    DayOfWeek dayOfWeek;
}

package kr.swyp.backend.friend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendContactWeek {
    EVERY_DAY("매일"),
    EVERY_WEEK("매주"),
    EVERY_TWO_WEEK("2주"),
    EVERY_MONTH("매달"),
    EVERY_SIX_MONTH("6개월");

    private String value;
}

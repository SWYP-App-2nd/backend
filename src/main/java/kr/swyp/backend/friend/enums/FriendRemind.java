package kr.swyp.backend.friend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendRemind {
    BIRTHDAY("생일"),
    ANNIVERSARY("기념일"),
    MESSAGE("안부");

    private String value;
}

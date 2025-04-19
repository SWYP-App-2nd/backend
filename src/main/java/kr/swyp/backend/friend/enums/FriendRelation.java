package kr.swyp.backend.friend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendRelation {
    FRIEND("친구"),
    FAMILY("가족"),
    ACQUAINTANCE("지인");

    private String value;
}

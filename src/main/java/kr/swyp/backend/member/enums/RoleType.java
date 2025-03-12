package kr.swyp.backend.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 권한 유형을 정의하는 열거형.
 */
@Getter
@RequiredArgsConstructor
public enum RoleType {

    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자"),
    SUPER_ADMIN("ROLE_SUPER_ADMIN", "최고 관리자");

    private final String key;
    private final String description;

    /**
     * Spring Security에서 사용할 권한 문자열 반환.
     *
     * @return "ROLE_" 접두사가 포함된 권한 문자열
     */
    public String getAuthority() {
        return this.key;
    }
}

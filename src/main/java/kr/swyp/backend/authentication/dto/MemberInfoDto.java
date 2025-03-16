package kr.swyp.backend.authentication.dto;

import java.util.UUID;
import kr.swyp.backend.member.enums.RoleType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberInfoDto {

    private UUID memberId;
    private String username;
    private RoleType roleType;
}

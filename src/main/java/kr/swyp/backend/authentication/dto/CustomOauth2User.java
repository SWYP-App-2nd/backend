package kr.swyp.backend.authentication.dto;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import kr.swyp.backend.member.enums.RoleType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class CustomOauth2User extends DefaultOAuth2User {

    private UUID memberId;
    private String username;
    private RoleType roleType;

    public CustomOauth2User(Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes, String nameAttributeKey, UUID memberId, String username,
            RoleType roleType) {
        super(authorities, attributes, nameAttributeKey);
        this.memberId = memberId;
        this.username = username;
        this.roleType = roleType;
    }
}

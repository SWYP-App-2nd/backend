package kr.swyp.backend.authentication.domain;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import kr.swyp.backend.member.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@AllArgsConstructor
@RedisHash(value = "refreshToken")
public class RefreshToken {

    @Id
    @Indexed
    private String id;

    @Indexed
    private UUID memberId;

    @Indexed
    private String refreshToken;

    @Indexed
    private RoleType roleType;

    @Indexed
    @TimeToLive(unit = TimeUnit.DAYS)
    private Integer timeToLive;

    @Indexed
    private LocalDateTime expiresAt;
}
package kr.swyp.backend.authentication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

public class TokenDto {

    @Getter
    @Builder
    public static class JwtDto {

        private String accessToken;
        private RefreshTokenInfo refreshTokenInfo;

        @Getter
        @Builder
        public static class RefreshTokenInfo {

            private String token;

            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime expiresAt;
        }
    }

    @Getter
    @Builder
    public static class RefreshTokenInfoResponse {

        private String refreshToken;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime expiresAt;
    }
}
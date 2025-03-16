package kr.swyp.backend.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthenticationDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsernamePasswordAuthenticationRequest {

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String username;

        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$",
                message = "비밀번호는 영문, 숫자를 포함하여 8자 이상이어야 합니다.")
        private String password;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenAuthenticationRequest {

        @NotNull(message = "리프레시 토큰은 필수 입니다.")
        private String refreshToken;
    }

}

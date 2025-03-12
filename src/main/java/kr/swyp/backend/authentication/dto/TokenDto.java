package kr.swyp.backend.authentication.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {

    private String accessToken;
}

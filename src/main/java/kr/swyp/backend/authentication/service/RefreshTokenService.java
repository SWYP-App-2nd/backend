package kr.swyp.backend.authentication.service;

import java.util.UUID;
import kr.swyp.backend.authentication.dto.MemberInfo;
import kr.swyp.backend.authentication.dto.TokenDto.RefreshTokenInfoResponse;
import kr.swyp.backend.member.enums.RoleType;

public interface RefreshTokenService {

    /**
     * 갱신 토큰 문자열에 해당하는 사용자 정보를 반환한다.
     *
     * @param refreshTokenString 갱신 토큰 문자열
     * @return 사용자 정보
     */
    MemberInfo getMemberInfoByRefreshTokenString(String refreshTokenString);

    /**
     * 입력받은 갱신 토큰 문자열에 해당하는 데이터를 삭제한다.
     *
     * @param refreshTokenString 삭제할 갱신 토큰 문자열
     */
    void removeRefreshToken(String refreshTokenString);

    /**
     * 사용자의 토큰을 생성하고 생성된 토큰의 정보를 반환한다.
     *
     * @param memberId 사용자 ID
     * @param roleType 사용자 타입
     * @return 생성된 토큰의 정보
     */
    RefreshTokenInfoResponse renew(UUID memberId, RoleType roleType);
}

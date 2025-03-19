package kr.swyp.backend.authentication.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.swyp.backend.authentication.dto.CustomOauth2User;
import kr.swyp.backend.authentication.provider.TokenProvider;
import kr.swyp.backend.member.dto.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@RequiredArgsConstructor
public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        if (authentication.getPrincipal() instanceof CustomOauth2User user) {
            MemberDetails memberDetails = new MemberDetails(user.getMemberId(), user.getUsername(),
                    "", authentication.getAuthorities());
            Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                    memberDetails, authentication.getCredentials(),
                    authentication.getAuthorities());
            tokenProvider.generateToken(response, newAuthentication);

        }
    }
}

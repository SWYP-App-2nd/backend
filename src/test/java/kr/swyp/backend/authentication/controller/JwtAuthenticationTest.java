package kr.swyp.backend.authentication.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import java.util.UUID;
import kr.swyp.backend.authentication.provider.TokenProvider;
import kr.swyp.backend.member.dto.MemberDetails;
import kr.swyp.backend.member.enums.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class JwtAuthenticationTest {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("올바른 JWT으로 인증되어야 한다.")
    void 올바른_JWT으로_인증되어야_한다() throws Exception {
        // given
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(RoleType.USER.getAuthority()));
        MemberDetails memberDetails = new MemberDetails(UUID.randomUUID(), "test", "",
                authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, "",
                authorities);
        String accessToken = tokenProvider.generateAccessToken(authentication);

        // when
        MvcResult mvcResult = mockMvc.perform(
                get("/shouldNotFound").header(AUTHORIZATION_HEADER,
                        String.format("%s%s", TOKEN_PREFIX, accessToken))).andReturn();

        // then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}

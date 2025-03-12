package kr.swyp.backend.authentication.filter;

import static kr.swyp.backend.authentication.exception.AuthException.AuthExceptionCode.CREDENTIAL_NOT_FOUND;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import kr.swyp.backend.authentication.dto.AuthenticationDto.UsernamePasswordAuthenticationRequest;
import kr.swyp.backend.authentication.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class CustomUsernamePasswordAuthenticationFilter extends
        UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    public CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
            ObjectMapper objectMapper) {
        super(authenticationManager);
        setFilterProcessesUrl("/auth/login");
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        UsernamePasswordAuthenticationRequest dto = obtainAuthenticationRequest(request);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UsernamePasswordAuthenticationRequest>> violationSet =
                validator.validate(dto);

        if (!violationSet.isEmpty()) {
            List<String> errorMessageList = violationSet.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();
            throw new AuthenticationServiceException(errorMessageList.get(0));
        }

        String username = dto.getUsername();
        username = username != null ? username.trim() : "";
        String password = dto.getPassword();
        password = password != null ? password : "";

        var authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        setDetails(request, authRequest);
        return getAuthenticationManager().authenticate(authRequest);
    }

    private UsernamePasswordAuthenticationRequest obtainAuthenticationRequest(
            HttpServletRequest request) {
        try {
            return objectMapper.readValue(request.getReader(),
                    UsernamePasswordAuthenticationRequest.class);
        } catch (Exception e) {
            log.error("[로그인 요청 실패] 로그인 요청 파싱에 실패했습니다.", e);
            throw new AuthException(CREDENTIAL_NOT_FOUND, "로그인 요청 파싱에 실패했습니다.",
                    HttpStatus.BAD_REQUEST);
        }
    }
}

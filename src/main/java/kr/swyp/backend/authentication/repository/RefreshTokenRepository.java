package kr.swyp.backend.authentication.repository;

import java.util.Optional;
import java.util.UUID;
import kr.swyp.backend.authentication.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    void deleteAllByMemberId(UUID memberId);
}

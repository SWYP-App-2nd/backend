package kr.swyp.backend.common.repository;

import java.util.Optional;
import java.util.UUID;
import kr.swyp.backend.common.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByFileNameAndCategoryAndMemberId(String fileName, String category,
            UUID memberId);
}

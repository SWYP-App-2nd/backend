package kr.swyp.backend.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import software.amazon.awssdk.annotations.NotNull;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "FILE")
public class File extends BaseEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Comment("사용자 ID")
    @Column(name = "MEMBER_ID")
    private UUID memberId;

    @NotNull
    @Comment("파일명")
    @Column(name = "FILE_NAME")
    private String fileName;

    @NotNull
    @Comment("파일 카테고리")
    @Column(name = "CATEGORY")
    private String category;

    @NotNull
    @Comment("파일 Content Type")
    @Column(name = "CONTENT_TYPE")
    private String contentType;

    @NotNull
    @Comment("파일 크기")
    @Column(name = "FILE_SIZE")
    private Long fileSize;

    @Override
    public String toString() {
        return String.format("%s/%s/%s", category, memberId, fileName);
    }
}

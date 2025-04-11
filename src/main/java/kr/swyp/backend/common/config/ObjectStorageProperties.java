package kr.swyp.backend.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "swyp.ncp.object-storage")
public class ObjectStorageProperties {

    private String endPoint;
    private String regionName;
    private String bucketName;
    private String accessKey;
    private String secretKey;
}

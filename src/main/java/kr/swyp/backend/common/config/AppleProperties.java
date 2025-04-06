package kr.swyp.backend.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "swyp.apple")
public class AppleProperties {

    private String teamId;
    private String clientId;
    private String keyId;
    private String keyPath;
}

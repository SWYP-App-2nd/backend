package kr.swyp.backend.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Java 8 날짜/시간 타입 지원 모듈 등록
        objectMapper.registerModule(new JavaTimeModule());

        // Java 8의 Optional, Stream 등을 위한 모듈 등록
        objectMapper.registerModule(new Jdk8Module());

        // 날짜를 타임스탬프가 아닌 ISO-8601 형식의 문자열로 직렬화
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 알 수 없는 속성이 있을 때 실패하지 않도록 설정
        // objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }
}

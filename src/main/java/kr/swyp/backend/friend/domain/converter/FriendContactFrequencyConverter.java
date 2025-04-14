package kr.swyp.backend.friend.domain.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kr.swyp.backend.friend.domain.FriendContactFrequency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
@RequiredArgsConstructor
public class FriendContactFrequencyConverter implements
        AttributeConverter<FriendContactFrequency, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(FriendContactFrequency attribute) {
        try {
            if (attribute == null) {
                return null;
            }
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            log.error("FriendContactFrequency to Json 변환 중 오류 발생! 원본 데이터: {}, 오류: {}", attribute,
                    e.getMessage());
            return null;
        }
    }

    @Override
    public FriendContactFrequency convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null) {
                return FriendContactFrequency.builder().build();
            }
            return objectMapper.readValue(dbData, FriendContactFrequency.class);
        } catch (Exception e) {
            log.error("String to FriendContactFrequency 변환 중 오류 발생! 원본 데이터: {}, 오류: {}", dbData,
                    e.getMessage());
            return FriendContactFrequency.builder().build();
        }
    }
}

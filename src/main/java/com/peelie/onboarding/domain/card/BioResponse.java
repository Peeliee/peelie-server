package com.peelie.onboarding.domain.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BioResponse {
    @JsonProperty("user_introduction")
    private String userIntroduction;

    public static Map<String, Object> getUserIntroSchema() {
        Map<String, Object> oneLinerSpec = Map.of(
                "type", "string",
                "description", "30자 이내로 만들어진 사용자 한 줄 자기소개 문구"
        );
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "user_introduction", oneLinerSpec // GPT 응답의 필드명과 일치해야 함
                ),
                "required", List.of("user_introduction"),
                "additionalProperties", false
        );
    }


}

package com.peelie.onboarding.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GeneratedCardPayload {
    @JsonProperty(required = true)
    private StageCardPayload stage1;

    @JsonProperty(required = true)
    private StageCardPayload stage2;

    @JsonProperty(required = true)
    private StageCardPayload stage3;

    @Getter
    @NoArgsConstructor
    public static class StageCardPayload {
        @JsonProperty(required = true)
        private String title;
        @JsonProperty(required = true)
        private String subtitle;
        @JsonProperty(required = true)
        private String content;
    }
}

package com.peelie.onboarding.domain.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GeneratedCardPayload {
    @JsonProperty(required = true)
    private StageCardPayload stage1;

    @JsonProperty(required = true)
    private StageCardPayload stage2;

    @JsonProperty(required = true)
    private StageCardPayload stage3;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor()
    public static class StageCardPayload {
        @JsonProperty
        private String title;
        @JsonProperty
        private String subtitle;
        @JsonProperty
        private String content;


        public String toStringContent() {
            return "title: " + this.title + "\n"
                    + "subtitle: " + this.subtitle + "\n"
                    + "content: " + this.content;
        }
    }
}

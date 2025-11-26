package com.peelie.onboarding.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class CardInfo {
    public static class StageInfo {

        @JsonIgnore
        private List<Stage> stages;

        @JsonAnyGetter
        public Map<String, Stage> getAllStageCards() {
            Map<String, Stage> stages = new LinkedHashMap<>();

            for (int i = 0; i < stages.size(); i++) {
                stages.put("stage" + (i + 1), stages.get(i));
            }
            return stages;
        }

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Stage {
        private String title;
        private String subtitle;
        private String content;
    }
}


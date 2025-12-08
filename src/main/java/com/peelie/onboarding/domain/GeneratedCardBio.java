package com.peelie.onboarding.domain;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedCardBio {

    private StageContent stage1;
    private StageContent stage2;
    private StageContent stage3;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StageContent {
        private InitCard card;
        private InitBio bio;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InitCard {
        @JsonPropertyDescription("단계 번호 (반드시 1, 2, 3 중 하나)")
        private Integer stage;

        @JsonPropertyDescription("카드의 제목 (이모지와 텍스트 조합)")
        private String title;

        @JsonPropertyDescription("카드의 소제목")
        private String subTitle;

        @JsonPropertyDescription("카드의 메인 내용")
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InitBio {
        @JsonPropertyDescription("단계 번호 (반드시 1, 2, 3 중 하나)")
        private Integer stage;

        @JsonPropertyDescription("한줄 소개 내용")
        private String bio;
    }
}
package com.peelie.onboarding.domain;


import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor // Jackson 역직렬화를 위해 필요
@AllArgsConstructor
public class GeneratedResponse { // LLM으로 부터 받은 응답 객체

    @JsonPropertyDescription("총 3단계(stage1, stage2, stage3)로 구성된 카드 리스트")
    private List<InitCard> cards;

    @JsonPropertyDescription("총 3단계(stage1, stage2, stage3)로 구성된 카드 리스트")
    private List<InitBio> bios;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InitCard {
        @JsonPropertyDescription("카드의 단계 (1, 2, 3 중 하나)")
        private Integer stage;
        @JsonPropertyDescription("카드의 제목 (이모지와 텍스트 조합, 창의적인 표현)")
        private String title;
        @JsonPropertyDescription("카드의 소제목 (제목을 보조하는 설명)")
        private String subTitle;
        @JsonPropertyDescription("카드의 메인 내용 (자연스러운 줄글 형태)")
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InitBio {
        @JsonPropertyDescription("한줄 소개 단계 (1, 2, 3 중 하나)")
        private Integer stage;
        @JsonPropertyDescription("한줄 소개 내용")
        private String bio;
    }
}

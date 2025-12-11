package com.peelie.onboarding.domain;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.peelie.profile.domain.Card;
import com.peelie.profile.domain.ProfileCommand;
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

    // TODO: 프로필 커맨드를 여기서 참조하는게 맞는지 고민해보고 개선하기.
    public static ProfileCommand.ApplyOnboardingResult toApplyOnboardingResult(GeneratedCardBio generatedBio) {
        return ProfileCommand.ApplyOnboardingResult.builder()
                .stage1Card(toCard(generatedBio.getStage1().getCard()))
                .stage1Bio(generatedBio.getStage1().getBio().getBio())
                .stage2Card(toCard(generatedBio.getStage2().getCard()))
                .stage2Bio(generatedBio.getStage2().getBio().getBio())
                .stage3Card(toCard(generatedBio.getStage3().getCard()))
                .stage3Bio(generatedBio.getStage3().getBio().getBio())
                .build();
    }

    public static Card toCard(GeneratedCardBio.InitCard initCard) {
        return new Card(initCard.getTitle(), initCard.getSubTitle(), initCard.getContent());
    }
}
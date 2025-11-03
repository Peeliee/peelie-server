package com.peelie.onboarding.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class OnboardingInfo {

    @Getter
    public static class Process {
        private final Set<Long> selectedCategoryIds;
        private final List<SubCategoryBlock> answers;

        public Process(OnboardingProcess process) {
            this.selectedCategoryIds = process.getSelectedCategories();

            // 서브카테고리별 그룹핑
            Map<Long, List<OnboardingSubCategoryAnswers>> bySub =
                    process.getSubCategoryAnswers().stream()
                            .collect(Collectors.groupingBy(OnboardingSubCategoryAnswers::getSubCategoryId));

            this.answers = bySub.entrySet().stream()
                    .map(e -> new SubCategoryBlock(
                            e.getKey(),
                            e.getValue().stream()
                                    .map(ans -> toLevelOut(ans.getLevel(), ans.getOptionId(), ans.getTextAnswer()))
                                    .toList()
                    ))
                    .toList();
        }

        private static LevelAnswerOut toLevelOut(String level, Long optionId, String textAnswer) {
            return switch (level) {
                case "L1" -> new LevelAnswerOut(level, optionId, null,    null,    null);
                case "L2" -> new LevelAnswerOut(level, null,    optionId, null,    null);
                case "L3" -> new LevelAnswerOut(level, null,    null,     optionId, null);
                case "L4" -> new LevelAnswerOut(level, null,    null,     null,     textAnswer);
                default   -> new LevelAnswerOut(level, null,    null,     null,     null);
            };
        }
    }

    @Getter
    @AllArgsConstructor
    public static class SubCategoryBlock {
        private final Long subCategoryId;
        private final List<LevelAnswerOut> answers;
    }

    @Getter
    @AllArgsConstructor
    public static class LevelAnswerOut {
        private final String level;      // "L1" ~ "L4"
        private final Long L1AnswerId;   // L1일 때만 값, 그 외 null
        private final Long L2AnswerId;   // L2일 때만 값
        private final Long L3AnswerId;   // L3일 때만 값
        private final String L4Answer;   // L4(TEXT)일 때만 값
    }
}
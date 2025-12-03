package com.peelie.onboarding.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, isGetterVisibility = NONE)
    public static class LevelAnswerOut {
        private final String level;
        @JsonProperty("L1AnswerId")
        private final Long L1AnswerId;
        @JsonProperty("L2AnswerId")
        private final Long L2AnswerId;
        @JsonProperty("L3AnswerId")
        private final Long L3AnswerId;
        @JsonProperty("L4Answer")
        private final String L4Answer;
    }
}
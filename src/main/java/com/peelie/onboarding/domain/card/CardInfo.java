package com.peelie.onboarding.domain.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class CardInfo {
    private final Stage stage1;
    private final Stage stage2;
    private final Stage stage3;

    @Getter
    @AllArgsConstructor
    public static class Stage {
        private final String title;
        private final String subtitle;
        private final String content;
    }
}
//public class CardInfo {
//    private List<Stage> stages;
//    private Map<String, Stage> stageMapCard;
//
//
//    public static CardInfo from(List<Stage> stages) {
//        Map<String, Stage> map = new LinkedHashMap<>();
//        for (int i = 0; i < stages.size(); i++) {
//            map.put("stage" + (i + 1), stages.get(i));
//        }
//
//        return CardInfo.builder()
//                .stages(stages)
//                .stageMapCard(map)
//                .build();
//    }
//
//    @Getter
//    @Builder
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class Stage {
//        @Builder.Default
//        private String title = "";
//        @Builder.Default
//        private String subtitle = "";
//        @Builder.Default
//        private String content = "";
//
//    }
//}
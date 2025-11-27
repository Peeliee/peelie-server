package com.peelie.onboarding.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
// TODO: CardInfo 클래스를 처음 만들었을 때는 Onboarding 도메인 모델 안에 포함하기 애매해서
//       별도의 도메인 패키지에 만들었으나, GPT 응답 response DTO의 Stage와 별도로 매핑하는 것이 불필요해보임
@Getter
@Builder
@AllArgsConstructor
public class CardInfo {
    private List<Stage> stages;
    private Map<String, Stage> stageMapCard;

    public CardInfo(){
        this.stageMapCard = new LinkedHashMap<>();
        for (int i = 0; i < 3; i++) {
            // Stage에 @NoArgsConstructor가 있어야 호출 가능합니다.
            this.stageMapCard.put("stage" + (i + 1), new Stage());
        }
    }

    public static CardInfo from(List<Stage> stages) {
        Map<String, Stage> map = new LinkedHashMap<>();
        for (int i = 0; i < stages.size(); i++) {
            map.put("stage" + (i + 1), stages.get(i));
        }

        return CardInfo.builder()
                .stages(stages)
                .stageMapCard(map)
                .build();
    }

    @Getter
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Stage {
        @Builder.Default
        private String title =  "";
        @Builder.Default
        private String subtitle = "";
        @Builder.Default
        private String content = "";

    }
}



//    @JsonAnyGetter
//    public Map<String, Stage> getAllStageCards() {
//        Map<String, Stage> stages = new LinkedHashMap<>();
//
//        for (int i = 0; i < 3; i++) {
//            stages.put("stage" + (i + 1), stages.get(i));
//        }
//        return stages;
//    }



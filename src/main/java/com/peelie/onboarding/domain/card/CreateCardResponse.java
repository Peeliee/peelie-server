package com.peelie.onboarding.domain.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

//사실상 Response DTO입니다. 즉, 클라이언트에 내려줄 JSON 구조를 표현하기 위한 용도죠.
// TODO: CardInfo 클래스를 처음 만들었을 때는 Onboarding 도메인 모델 안에 포함하기 애매해서
//       별도의 도메인 패키지에 만들었으나, GPT 응답 response DTO의 Stage와 별도로 매핑하는 것이 불필요해보임
@Getter
@Builder
@AllArgsConstructor
public class CreateCardResponse {
    private String status;
    private String reason;
    private final CardInfo data;

    public static final String STATUS_GENERATING = "GENERATING";
    public static final String REASON_GENERATING = "카드를 생성하고 있습니다. 잠시만 기다려주세요.";


}
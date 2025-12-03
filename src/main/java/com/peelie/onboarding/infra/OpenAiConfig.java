package com.peelie.onboarding.infra;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {

    @Value("${openai.api.key}")
    private String apiKey;

    @Bean
    public OpenAIClient openAIClient() {
        return OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }

    public static final String CARD_SYSTEM_PROMPT = """
            유저가 온보딩 질문에 답변한 응답 (L0 ~ L4)을 기반으로 총 3단계로 이루어진 사용자의 정보를 반영한 단계별 정보를 자기 소개 카드 형식으로 총 3개 제공하는 AI입니다.
            [L0 정보란?]
            - L0 질문: Category의 categoryQuestion (예: "콘텐츠 중에 어떤 것을 가장 즐겨보세요?")
            - L0 답변: 사용자가 선택한 SubCategory 이름 (예: "영화")
            
            하나의 카드에는 제목(title), 소제목(subtitle), 내용(content) 필드가 포함되어야 합니다.
            제목, 소제목, 내용은 순수 텍스트 + 이모지만 반환 해야 하며, 마크다운 문법이나 기타 포맷팅 문법은 포함하지 마세요.
            제목과 소제목은 고정 포맷이 아닙니다. 입력값 특성을 반영해 자유롭게 생성해주세요 (예: 나열형, 조합형, 시적 표현 등 가능)
            내용은 세 카테고리 정보를 자연스럽게 이어 하나의 단락으로 표현해주세요.
            
            각 단계별 카드의 구성은 다음과 같습니다:
            - stage1: L0 + L1 정보를 활용하여 사용자의 답변을 조합해 스토리처럼 풀어냄 → 허접한 나열이 아니라 풍성한 소개 느낌
            - stage2: L2 + L3 정보를 활용하여 사용자의 답변을 조합해 스토리처럼 풀어냄 → 허접한 나열이 아니라 풍성한 소개 느낌
            - stage3: L4 정보를 활용하여 사용자의 주관식 답변을 기반으로 가장 개성 있는 결과물이 나와야 합니다 → 유저 원문 뉘앙스를 살리고, 제목도 답변 키워드를 활용해 센스 있게 생성해주세요.
           
            아래는 예시 카드입니다.
            
            stage1: 
            {
                "title": "📽️🎶🐶 액션 영화, 감성적인 음악, 그리고 뽀송한 친구",
                "subtitle": "주말엔 영화관, 이동 중엔 음악, 집에선 강아지와 함께",
                "content": "🎬 평소 콘텐츠 중에서는 영화를 가장 즐겨 보는데, 특히 액션/스릴러 장르의 긴장감을 좋아해서 주말이면 꼭 한 편은 챙겨봅니다. 🎶 이동할 때나 하루를 마무리할 때는 늘 음악을 듣는데, 요즘은 감성적인 곡에 꽂혀 이어폰만 꽂으면 일상이 뮤직비디오처럼 바뀌곤 해요. 🐾 집에 돌아오면 애교 많은 말티즈가 꼬리를 흔들며 맞아주는데, 그 순간 하루 피로가 싹 풀려버리죠"
            }
                        
            stage2:
            {
                "title": "✨ 취향에서 경험으로, 나를 드러내는 순간들",
                "subtitle": "무엇을 보며, 언제 듣고, 어떻게 함께하는지",
                "content": "🎬 영화를 볼 때는 단순히 스토리보다 연출과 영상미에 눈길이 가는 편이에요. 특히 아이맥스에서 영화를 봤을 때 화면과 사운드가 압도적이어서 그 순간이 아직도 생생하게 남아 있습니다. 🎶 음악은 주로 이동할 때 감성적인 곡을 즐겨 듣는데, 어느 콘서트에서는 수많은 관객과 함께 노래를 따라 부르며 전율을 느꼈던 경험도 있어요. 🐾 반려견과는 산책을 가장 자주 나가는데, 단순한 산책이 아니라 제 마음을 단단히 지탱해주는 시간이 되고, 늘 곁에 있어주는 안정감 덕분에 가족 같은 존재로 느껴집니다."
            }     
            
            stage3:
            {
                "title": "다콜쿠🎶🐾",
                "subtitle": "가장 깊은 기억을 꺼내놓다",
                "content": "🎶 콜드플레이 공연에서 눈물이 났던 장면은 그 어떤 말로도 설명하기 힘든 전율이었고요. 🐾 마지막으로 우리 집 말티즈 쿠리는 세상에서 제일 애교 많은 가족이라, 하루하루를 특별하게 만들어줍니다. 다크나이트 아이맥스를 보며 느꼈던 압도적인 순간은 아직도 제 인생 영화 경험으로 남아 있어요."
            }
            
            다음은 사용자가 온보딩 과정에서 선택한 관심사와 답변입니다.
            """;
}


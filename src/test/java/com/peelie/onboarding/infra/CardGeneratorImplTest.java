package com.peelie.onboarding.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.peelie.onboarding.domain.CardInfo;
import com.peelie.onboarding.domain.GeneratedCardPayload;
import com.peelie.onboarding.domain.OnboardingData;
import com.peelie.onboarding.infra.CardGeneratorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardGeneratorImpl 테스트")
class CardGeneratorImplTest {

    @Mock
    private OpenAIClient openAIClient;

    @InjectMocks
    private CardGeneratorImpl cardGenerator;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // ObjectMapper를 리플렉션으로 주입
        try {
            var field = CardGeneratorImpl.class.getDeclaredField("objectMapper");
            field.setAccessible(true);
            field.set(cardGenerator, objectMapper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject ObjectMapper", e);
        }
    }

    @Test
    @DisplayName("정상적으로 카드를 생성한다")
    void generateCard_Success() throws Exception {
        // given
        OnboardingData onboardingData = createTestOnboardingData();
        String mockJsonResponse = createMockJsonResponse();
        Response mockResponse = createMockResponse(mockJsonResponse);

        // Deep stubbing을 사용하여 체이닝된 메서드 호출 모킹
        // openAIClient.responses().create() 체이닝을 모킹
        when(openAIClient.responses()).thenAnswer(invocation -> {
            Object responsesService = mock(Object.class, RETURNS_DEEP_STUBS);
            try {
                // create() 메서드가 호출되면 mockResponse 반환
                Method createMethod = responsesService.getClass().getMethod("create", ResponseCreateParams.class);
                when(createMethod.invoke(eq(responsesService), any(ResponseCreateParams.class)))
                        .thenReturn(mockResponse);
            } catch (Exception e) {
                // Reflection 실패 시 deep stubbing에 의존
            }
            return responsesService;
        });

        // when
        CardInfo result = cardGenerator.generateCard(onboardingData);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStages()).hasSize(3);
        assertThat(result.getStageMapCard()).hasSize(3);

        // Stage 1 검증
        CardInfo.Stage stage1 = result.getStages().get(0);
        assertThat(stage1.getTitle()).isEqualTo("Stage 1 Title");
        assertThat(stage1.getSubtitle()).isEqualTo("Stage 1 Subtitle");
        assertThat(stage1.getContent()).isEqualTo("Stage 1 Content");

        // Stage 2 검증
        CardInfo.Stage stage2 = result.getStages().get(1);
        assertThat(stage2.getTitle()).isEqualTo("Stage 2 Title");
        assertThat(stage2.getSubtitle()).isEqualTo("Stage 2 Subtitle");
        assertThat(stage2.getContent()).isEqualTo("Stage 2 Content");

        // Stage 3 검증
        CardInfo.Stage stage3 = result.getStages().get(2);
        assertThat(stage3.getTitle()).isEqualTo("Stage 3 Title");
        assertThat(stage3.getSubtitle()).isEqualTo("Stage 3 Subtitle");
        assertThat(stage3.getContent()).isEqualTo("Stage 3 Content");

        // stageMapCard 검증
        assertThat(result.getStageMapCard().get("stage1")).isEqualTo(stage1);
        assertThat(result.getStageMapCard().get("stage2")).isEqualTo(stage2);
        assertThat(result.getStageMapCard().get("stage3")).isEqualTo(stage3);
    }

    @Test
    @DisplayName("GPT API가 빈 응답을 반환하면 예외가 발생한다")
    void generateCard_EmptyContent_ThrowsException() {
        // given
        OnboardingData onboardingData = createTestOnboardingData();
        Response mockResponse = createMockResponse("");

        // Deep stubbing으로 체이닝된 호출 모킹
        when(openAIClient.responses()).thenAnswer(invocation -> {
            Object responsesService = mock(Object.class, RETURNS_DEEP_STUBS);
            try {
                Method createMethod = responsesService.getClass().getMethod("create", ResponseCreateParams.class);
                when(createMethod.invoke(eq(responsesService), any(ResponseCreateParams.class)))
                        .thenReturn(mockResponse);
            } catch (Exception e) {
                // Reflection 실패 시
            }
            return responsesService;
        });

        // when & then
        assertThatThrownBy(() -> cardGenerator.generateCard(onboardingData))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Card generation failed");
    }

    @Test
    @DisplayName("GPT API 호출 실패 시 예외가 발생한다")
    void generateCard_ApiCallFails_ThrowsException() {
        // given
        OnboardingData onboardingData = createTestOnboardingData();

        // API 호출 시 예외 발생하도록 모킹
        when(openAIClient.responses()).thenAnswer(invocation -> {
            Object responsesService = mock(Object.class, RETURNS_DEEP_STUBS);
            try {
                Method createMethod = responsesService.getClass().getMethod("create", ResponseCreateParams.class);
                when(createMethod.invoke(eq(responsesService), any(ResponseCreateParams.class)))
                        .thenThrow(new RuntimeException("API 호출 실패"));
            } catch (Exception e) {
                // Reflection 실패 시
            }
            return responsesService;
        });

        // when & then
        assertThatThrownBy(() -> cardGenerator.generateCard(onboardingData))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Card generation failed");
    }

    @Test
    @DisplayName("잘못된 JSON 응답 시 예외가 발생한다")
    void generateCard_InvalidJson_ThrowsException() {
        // given
        OnboardingData onboardingData = createTestOnboardingData();
        String invalidJson = "{ invalid json }";
        Response mockResponse = createMockResponse(invalidJson);

        when(openAIClient.responses()).thenAnswer(invocation -> {
            Object responsesService = mock(Object.class, RETURNS_DEEP_STUBS);
            try {
                Method createMethod = responsesService.getClass().getMethod("create", ResponseCreateParams.class);
                when(createMethod.invoke(eq(responsesService), any(ResponseCreateParams.class)))
                        .thenReturn(mockResponse);
            } catch (Exception e) {
                // Reflection 실패 시
            }
            return responsesService;
        });

        // when & then
        assertThatThrownBy(() -> cardGenerator.generateCard(onboardingData))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Card generation failed");
    }

    @Test
    @DisplayName("null payload 필드가 있어도 빈 문자열로 처리한다")
    void generateCard_NullFields_HandlesGracefully() throws Exception {
        // given
        OnboardingData onboardingData = createTestOnboardingData();
        String jsonWithNulls = """
                {
                  "stage1": {"title": "Title1", "subtitle": null, "content": "Content1"},
                  "stage2": {"title": null, "subtitle": "Subtitle2", "content": null},
                  "stage3": {"title": "Title3", "subtitle": "Subtitle3", "content": "Content3"}
                }
                """;

        Response mockResponse = createMockResponse(jsonWithNulls);
        
        when(openAIClient.responses()).thenAnswer(invocation -> {
            Object responsesService = mock(Object.class, RETURNS_DEEP_STUBS);
            try {
                Method createMethod = responsesService.getClass().getMethod("create", ResponseCreateParams.class);
                when(createMethod.invoke(eq(responsesService), any(ResponseCreateParams.class)))
                        .thenReturn(mockResponse);
            } catch (Exception e) {
                // Reflection 실패 시
            }
            return responsesService;
        });

        // when
        CardInfo result = cardGenerator.generateCard(onboardingData);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStages()).hasSize(3);

        // null 필드는 빈 문자열로 처리됨
        assertThat(result.getStages().get(0).getSubtitle()).isEmpty();
        assertThat(result.getStages().get(1).getTitle()).isEmpty();
        assertThat(result.getStages().get(1).getContent()).isEmpty();
    }

    private OnboardingData createTestOnboardingData() {
        OnboardingData.CategoryAnswer categoryAnswer = OnboardingData.CategoryAnswer.builder()
                .userName("테스트 사용자")
                .categoryName("카테고리1")
                .answers(List.of(
                        OnboardingData.CategoryAnswer.Answer.builder()
                                .level("L1")
                                .question("질문1")
                                .answer("답변1")
                                .build()))
                .build();

        return OnboardingData.builder()
                .stage1(List.of(categoryAnswer))
                .stage2(List.of(categoryAnswer))
                .stage3(List.of(categoryAnswer))
                .build();
    }

    private String createMockJsonResponse() {
        return """
                {
                  "stage1": {
                    "title": "Stage 1 Title",
                    "subtitle": "Stage 1 Subtitle",
                    "content": "Stage 1 Content"
                  },
                  "stage2": {
                    "title": "Stage 2 Title",
                    "subtitle": "Stage 2 Subtitle",
                    "content": "Stage 2 Content"
                  },
                  "stage3": {
                    "title": "Stage 3 Title",
                    "subtitle": "Stage 3 Subtitle",
                    "content": "Stage 3 Content"
                  }
                }
                """;
    }

    private Response createMockResponse(String jsonContent) {
        // Response 객체를 모킹하여 toString()이 JSON 문자열을 반환하도록 설정
        Response mockResponse = mock(Response.class);
        when(mockResponse.toString()).thenReturn(jsonContent);
        return mockResponse;
    }
}

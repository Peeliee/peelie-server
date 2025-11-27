package com.peelie.prompt;

import com.peelie.onboarding.domain.OnboardingProcess;
import com.peelie.onboarding.domain.OnboardingReader;
import com.peelie.onboarding.domain.OnboardingSubCategoryAnswers;
import com.peelie.profile.domain.Profile;
import com.peelie.profile.domain.ProfileReader;
import com.peelie.questionnaire.domain.category.SubCategory;
import com.peelie.questionnaire.domain.category.SubCategoryReader;
import com.peelie.questionnaire.domain.question.Question;
import com.peelie.questionnaire.domain.question.QuestionOption;
import com.peelie.questionnaire.domain.question.QuestionReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserAnswerLoader {

    private final OnboardingReader onboardingReader;
    private final SubCategoryReader subCategoryReader;
    private final QuestionReader questionReader;
    private final ProfileReader profileReader;

    public PromptCommand generatePromptCommand(Long userId) {
        OnboardingProcess onboardingProcess = onboardingReader.findOnboardingProcessByUserId(userId);
        return buildPromptCommand(userId, onboardingProcess);
    }

    // OnboardingProcess의 답변 데이터를 PromptCommand로 변환
    private PromptCommand buildPromptCommand(Long userId, OnboardingProcess onboardingProcess) {
        // 프로필 조회하여 사용자 이름 획득
        Profile profile = profileReader.getProfileByUserId(userId);

        // 사용자가 선택한 모든 서브카테고리의 ID 추출
        Set<Long> subCategoryIds = onboardingProcess.getSubCategoryAnswers().stream()
                .map(OnboardingSubCategoryAnswers::getSubCategoryId)
                .collect(Collectors.toSet());

        // 각 서브카테고리별 답변 정보 구성
        List<PromptCommand.SubCategoryAnswer> subCategoryAnswers = new ArrayList<>();
        for (Long subCategoryId : subCategoryIds) {
            // 해당 서브카테고리의 모든 답변 필터링
            List<OnboardingSubCategoryAnswers> answersForSubCategory =
                    onboardingProcess.getSubCategoryAnswers().stream()
                            .filter(answer -> answer.getSubCategoryId().equals(subCategoryId))
                            .collect(Collectors.toList());

            // 서브카테고리 답변 정보 생성
            PromptCommand.SubCategoryAnswer subCategoryAnswer = buildSubCategoryAnswer(subCategoryId, answersForSubCategory);
            subCategoryAnswers.add(subCategoryAnswer);
        }

        return PromptCommand.builder()
                .userId(userId)
                .userName(profile.getUserName())
                .answers(subCategoryAnswers)
                .build();
    }

    // 서브카테고리 하나에 대한 답변 정보 구성
    private PromptCommand.SubCategoryAnswer buildSubCategoryAnswer(
            Long subCategoryId,
            List<OnboardingSubCategoryAnswers> answers) {

        // 서브카테고리 정보 조회
        SubCategory subCategory = subCategoryReader.getSubCategory(subCategoryId);

        // 레벨별로 정렬 (L1 -> L2 -> L3 -> L4)
        List<OnboardingSubCategoryAnswers> sortedAnswers = answers.stream()
                .sorted(Comparator.comparing(OnboardingSubCategoryAnswers::getLevel))
                .collect(Collectors.toList());

        // 각 답변을 QuestionAnswer로 변환
        List<PromptCommand.QuestionAnswer> questionAnswers = sortedAnswers.stream()
                .map(this::buildQuestionAnswer)
                .collect(Collectors.toList());

        return PromptCommand.SubCategoryAnswer.builder()
                .categoryName(subCategory.getCategory().getName())
                .subCategoryName(subCategory.getName())
                .categoryQuestion(subCategory.getCategory().getCategoryQuestion()) // L0 정보
                .questionAnswers(questionAnswers)
                .build();
    }

    // 개별 질문에 대한 답변 정보 구성
    private PromptCommand.QuestionAnswer buildQuestionAnswer(OnboardingSubCategoryAnswers answer) {
        // 질문 내용 조회 - subCategory의 questions에서 level로 찾기
        SubCategory subCategory = subCategoryReader.getSubCategory(answer.getSubCategoryId());
        Question question = subCategory.getQuestions().stream()
                .filter(q -> q.getLevel().name().equals(answer.getLevel()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("질문을 찾을 수 없습니다. Level: " + answer.getLevel()));

        // 답변 내용 결정
        String answerContent;
        if ("L4".equals(answer.getLevel())) {
            // L4는 텍스트 답변
            answerContent = answer.getTextAnswer();
        } else {
            // L1~L3는 선택지 내용 조회
            QuestionOption option = questionReader.getQuestionOptionById(answer.getOptionId());
            answerContent = option.getContent();
        }

        return PromptCommand.QuestionAnswer.builder()
                .level(answer.getLevel())
                .question(question.getContent())
                .answer(answerContent)
                .build();
    }
}

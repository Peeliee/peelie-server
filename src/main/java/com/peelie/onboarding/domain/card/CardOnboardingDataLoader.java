package com.peelie.onboarding.domain.card;

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
public class CardOnboardingDataLoader {
    private final OnboardingReader onboardingReader;
    private final SubCategoryReader subCategoryReader;
    private final QuestionReader questionReader;
    private final ProfileReader profileReader;

    public CardOnboardingData load(Long userId) {
        OnboardingProcess onboardingProcess = onboardingReader.findOnboardingProcessByUserId(userId);
        return buildCardOnboardingData(userId, onboardingProcess);
    }

    // OnboardingProcess의 답변 데이터를 UserAnswer로 변환
    private CardOnboardingData buildCardOnboardingData(Long userId, OnboardingProcess onboardingProcess) {
        // 프로필 조회하여 사용자 이름 획득
        Profile profile = profileReader.getProfileByUserId(userId);
        String userName = profile.getUserName();

        // 사용자가 선택한 모든 서브카테고리의 ID 추출
        Set<Long> subCategoryIds = onboardingProcess.getSubCategoryAnswers().stream()
                .map(OnboardingSubCategoryAnswers::getSubCategoryId)
                .collect(Collectors.toSet());

        // 각 서브카테고리별 답변 정보 구성
        List<CardOnboardingData.CategoryAnswer> subCategoryAnswers = new ArrayList<>();
        for (Long subCategoryId : subCategoryIds) {
            // 해당 서브카테고리의 모든 답변 필터링
            List<OnboardingSubCategoryAnswers> answersForSubCategory =
                    onboardingProcess.getSubCategoryAnswers().stream()
                            .filter(answer -> answer.getSubCategoryId().equals(subCategoryId))
                            .collect(Collectors.toList());

            // 서브카테고리 답변 정보 생성
            CardOnboardingData.CategoryAnswer categoryAnswer = buildSubCategoryAnswer(userName,subCategoryId, answersForSubCategory);
            subCategoryAnswers.add(categoryAnswer);
        }

        // 원본 CategoryAnswer으 level 기준으로 Stage 나누기
        List<CardOnboardingData.CategoryAnswer> stage1 = new ArrayList<>();
        List<CardOnboardingData.CategoryAnswer> stage2 = new ArrayList<>();
        List<CardOnboardingData.CategoryAnswer> stage3 = new ArrayList<>();

        for (CardOnboardingData.CategoryAnswer categoryAnswer : subCategoryAnswers) {
            List<CardOnboardingData.Answer> allAnswers = categoryAnswer.getAnswers();

            // stage1 : L0, L1
            List<CardOnboardingData.Answer> stage1Answers = allAnswers.stream()
                    .filter(a -> "L0".equals(a.getLevel()) || "L1".equals(a.getLevel()))
                    .collect(Collectors.toList());
            if (!stage1Answers.isEmpty()) {
                stage1.add(CardOnboardingData.CategoryAnswer.builder()
                        .userName(categoryAnswer.getUserName())
                        .categoryName(categoryAnswer.getCategoryName())
                        .categoryQuestion(categoryAnswer.getCategoryQuestion())
                        .answers(stage1Answers)
                        .build());
            }

            // stage2 : L2, L3
            List<CardOnboardingData.Answer> stage2Answers = allAnswers.stream()
                    .filter(a -> "L2".equals(a.getLevel()) || "L3".equals(a.getLevel()))
                    .collect(Collectors.toList());
            if (!stage2Answers.isEmpty()) {
                stage2.add(CardOnboardingData.CategoryAnswer.builder()
                        .userName(categoryAnswer.getUserName())
                        .categoryName(categoryAnswer.getCategoryName())
                        .categoryQuestion(categoryAnswer.getCategoryQuestion())
                        .answers(stage2Answers)
                        .build());
            }

            // stage3 : L4
            List<CardOnboardingData.Answer> stage3Answers = allAnswers.stream()
                    .filter(a -> "L4".equals(a.getLevel()))
                    .collect(Collectors.toList());
            if (!stage3Answers.isEmpty()) {
                stage3.add(CardOnboardingData.CategoryAnswer.builder()
                        .userName(categoryAnswer.getUserName())
                        .categoryName(categoryAnswer.getCategoryName())
                        .categoryQuestion(categoryAnswer.getCategoryQuestion())
                        .answers(stage3Answers)
                        .build());
            }
        }

        return CardOnboardingData.builder()
                .stage1(stage1)
                .stage2(stage2)
                .stage3(stage3)
                .build();
    }

    // 서브카테고리 하나에 대한 답변 정보 구성
    private CardOnboardingData.CategoryAnswer buildSubCategoryAnswer(
            String userName,
            Long subCategoryId,
            List<OnboardingSubCategoryAnswers> answers) {

        // 서브카테고리 정보 조회
        SubCategory subCategory = subCategoryReader.getSubCategory(subCategoryId);

        // 레벨별로 정렬 (L1 -> L2 -> L3 -> L4)
        List<OnboardingSubCategoryAnswers> sortedAnswers = answers.stream()
                .sorted(Comparator.comparing(OnboardingSubCategoryAnswers::getLevel))
                .collect(Collectors.toList());
        // 각 답변을 CardOnboardingData.Answer로 변환
        List<CardOnboardingData.Answer> answerDtos = sortedAnswers.stream()
                .map(this::buildCardAnswer)   // ⬅️ 새로 만든 메서드
                .collect(Collectors.toList());

//    // 각 답변을 QuestionAnswer로 변환
//    List<CardOnboardingData.Answer> questionAnswers = sortedAnswers.stream()
//            .map(this::buildQuestionAnswer)
//            .collect(Collectors.toList());

        return CardOnboardingData.CategoryAnswer.builder()
                .userName(userName)                                           //  userName 채우기
                .categoryName(subCategory.getCategory().getName())            // 상위 카테고리 이름
                .categoryQuestion(subCategory.getCategory().getCategoryQuestion()) // L0 질문
                .answers(answerDtos)
                .build();
    }


    // 개별 질문에 대한 답변 정보 구성
    private CardOnboardingData.Answer buildCardAnswer(OnboardingSubCategoryAnswers answer) {
        // 질문 내용 조회 - subCategory의 questions에서 level로 찾기
        SubCategory subCategory = subCategoryReader.getSubCategory(answer.getSubCategoryId());
        Question question = subCategory.getQuestions().stream()
                .filter(q -> q.getLevel().name().equals(answer.getLevel()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("질문을 찾을 수 없습니다. Level: " + answer.getLevel()));

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

        return CardOnboardingData.Answer.builder()
                .level(answer.getLevel())
                .question(question.getContent())
                .answer(answerContent)
                .build();
    }

}

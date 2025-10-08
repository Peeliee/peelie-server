package com.peelie.onboarding.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peelie.questionnaire.domain.category.CategoryReader;
import com.peelie.questionnaire.domain.QuestionnaireService;
import com.peelie.questionnaire.domain.question.QuestionInfo;
import com.peelie.questionnaire.domain.question.QuestionType;
import com.peelie.profile.domain.ProfileService;
import com.peelie.profile.domain.InteractionStyle;

@Service
@RequiredArgsConstructor
public class OnboardingProcessServiceImpl implements OnboardingProcessService {
    private final OnboardingReader onboardingReader;
    private final OnboardingStore onboardingStore;

    private final CategoryReader categoryReader;
    private final QuestionnaireService questionnaireService;
    private final ProfileService profileService;

    @Override
    @Transactional
    public OnboardingInfo.Process selectCategories(OnboardingCommand.SelectCategories command) {
        // 1. 유저의 온보딩 프로세스를 조회
        OnboardingProcess onboardingProcess = onboardingReader.findOnboardingProcessByUserId(command.getUserId());
        // 2. 도메인에서 status 검증 및 카테고리 선택(매개변수로 카테고리 reader 호출)
        onboardingProcess.validateCategories(command.getCategoryIds());
        // 3. 변경된 상태를 저장
        OnboardingProcess updated = onboardingStore.store(onboardingProcess);
        // 4. 결과 반환
        return new OnboardingInfo.Process(updated);
    }

    @Override
    @Transactional
    public OnboardingInfo.Process submitAnswer(OnboardingCommand.SubmitAnswer command) {
        // 1. 유저의 온보딩 프로세스 조회
        OnboardingProcess process = onboardingReader.findOnboardingProcessByUserId(command.getUserId());

        // 2. 질문 묶음 조회
        List<QuestionInfo> questions = questionnaireService.getQuestionsByIds(command.getCategoryId(), command.getSubCategoryId());

        // 3. 해당 questionId가 실제 존재하는지 확인
        QuestionInfo target = questions.stream()
                .filter(q -> q.getQuestionId().equals(command.getQuestionId()))
                .findFirst()
                .orElseThrow(() -> new BaseException("해당 카테고리/서브카테고리에 questionId가 없습니다.", ErrorCode.VALIDATION_ERROR));

        // 4. 타입에 따라 값 검증 + 정규화
        final String input = command.getValue() == null ? "" : command.getValue().trim();
        String value;

        if (target.getType() == QuestionType.TEXT) {
            if (input.isBlank()) {
                throw new BaseException("서술형 답변이 비어 있습니다.", ErrorCode.VALIDATION_ERROR);
            }
            value = input;
        } else {
            boolean optionExists = target.getOptions().stream()
                    .anyMatch(opt -> String.valueOf(opt.getOptionId()).equals(input));
            if (!optionExists) {
                throw new BaseException("선택형 질문의 옵션이 유효하지 않습니다.", ErrorCode.VALIDATION_ERROR);
            }
            value = input;
        }
        // 5. 도메인에서 status 검증 및 추가
        process.validateAnswers(target.getQuestionId(), value);
        // 6. 저장
        OnboardingProcess updated = onboardingStore.store(process);
        // 7. 결과 반환
        return new OnboardingInfo.Process(updated);
    }

    @Override
    @Transactional
    public OnboardingInfo.Process submitInteractionStyle(OnboardingCommand.SubmitInteraction command) {
        // 1. 온보딩 프로세스 조회
        OnboardingProcess process = onboardingReader.findOnboardingProcessByUserId(command.getUserId());
        // 2. 도메인 검증
        process.validateInteractionStyle(command.getInteractionStyle(), command.getBio());
        // 3. Profile 업데이트 (교류성향 + 한줄소개 저장)
        profileService.updateInteractionStyleAndBio(
                command.getUserId(),
                InteractionStyle.valueOf(command.getInteractionStyle().trim().toUpperCase()),
                command.getBio()
        );
        // 4. 온보딩 상태 저장
        OnboardingProcess updated = onboardingStore.store(process);
        return new OnboardingInfo.Process(updated);
    }

}

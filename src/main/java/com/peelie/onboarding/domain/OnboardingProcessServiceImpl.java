package com.peelie.onboarding.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.profile.domain.InteractionStyle;
import com.peelie.profile.domain.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peelie.questionnaire.domain.QuestionnaireService;
import com.peelie.questionnaire.domain.question.QuestionInfo;
import com.peelie.questionnaire.domain.question.QuestionType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OnboardingProcessServiceImpl implements OnboardingProcessService {
    private final OnboardingReader onboardingReader;
    private final OnboardingStore onboardingStore;

    private final QuestionnaireService questionnaireService;
    private final ProfileService profileService;

    @Override
    @Transactional
    public OnboardingInfo.Process startOnboarding(Long userId) {
        // 1. 이미 온보딩 프로세스가 있으면 예외 or 리턴
        if (onboardingReader.existsByUserId(userId)) {
            throw new BaseException("이미 온보딩이 진행 중입니다.", ErrorCode.VALIDATION_ERROR);
        }
        // 2. 도메인 엔티티에서 초기 상태를 정의
        OnboardingProcess process = OnboardingProcess.start(userId);
        // 3. 저장 후 반환
        onboardingStore.store(process);
        return new OnboardingInfo.Process(process);
    }

    @Override
    @Transactional
    public OnboardingInfo.Process selectCategories(OnboardingCommand.SelectCategories command) {
        // 1. 유저의 온보딩 프로세스를 조회
        OnboardingProcess onboardingProcess = onboardingReader.findOnboardingProcessByUserId(command.getUserId());
        // 2. 카테고리 선택 로직 수행 (매개변수로 프론트에서 선택된 카테고리 id 3개를 command로 전달받아)
        onboardingProcess.setCategories(command.getCategoryIds());
        // 3. 변경된 상태를 저장
        OnboardingProcess updated = onboardingStore.store(onboardingProcess);
        // 4. 결과 반환
        return new OnboardingInfo.Process(updated);
    }


    @Override
    @Transactional
    public OnboardingInfo.Process submitSubCategoryAnswers(OnboardingCommand.SubmitSubCategoryAnswers command) {
        // 1. 유저의 온보딩 프로세스 조회
        OnboardingProcess process = onboardingReader.findOnboardingProcessByUserId(command.getUserId());

        // 2. 질문 묶음 조회
//        List<QuestionInfo> questions = questionnaireService.getQuestionsByIds(command.getCategoryId(), command.getSubCategoryId());


        // 6. 저장
        OnboardingProcess updated = onboardingStore.store(process);
        // 7. 결과 반환
        return new OnboardingInfo.Process(updated);
    }


    @Override
    @Transactional
    public OnboardingInfo.Process submitInteractionStyleBio(OnboardingCommand.SubmitInteractionBio command) {
        // 1. 온보딩 프로세스 조회
        OnboardingProcess process = onboardingReader.findOnboardingProcessByUserId(command.getUserId());
        // 2. 현재 상태 검증 + 교류성향/한줄소개 값 검증 및 온보딩 완료 처리
        process.setInteractionStyle(command.getInteractionStyle(), command.getBio());
        // 3. ProfileService 호출 (다른 도메인)
        profileService.updateInteractionStyle(
                command.getUserId(),
                command.getInteractionStyle()
        );
        profileService.updateBio(
                command.getUserId(),
                command.getBio()
        );
        // 4. 온보딩 상태 저장
        onboardingStore.store(process);
        // 5. 결과 반환
        return new OnboardingInfo.Process(process);
    }
}

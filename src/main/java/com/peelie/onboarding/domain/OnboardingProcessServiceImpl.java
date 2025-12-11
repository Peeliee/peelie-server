package com.peelie.onboarding.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.profile.domain.ProfileService;
import com.peelie.prompt.PromptGenerator;
import com.peelie.questionnaire.domain.QuestionnaireService;
import com.peelie.questionnaire.domain.category.SubCategory;
import com.peelie.questionnaire.domain.category.SubCategoryReader;
import com.peelie.questionnaire.domain.question.QuestionInfo;
import com.peelie.questionnaire.domain.question.QuestionOptionInfo;
import com.peelie.questionnaire.domain.question.QuestionType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
public class OnboardingProcessServiceImpl implements OnboardingProcessService {
    private final OnboardingReader onboardingReader;
    private final OnboardingStore onboardingStore;
    private final QuestionnaireService questionnaireService;
    private final SubCategoryReader subCategoryReader;
    private final ProfileService profileService;

    private final PromptGenerator promptGenerator;
    private final CardBioGenerator cardBioGenerator;

    @Override
    @Transactional
    public OnboardingInfo.Process selectCategories(OnboardingCommand.SelectCategories command) {
        // 1. 유저의 온보딩 프로세스를 조회하여 있으면 찾기, 없으면 도메인 메서드 start 호출
        OnboardingProcess onboardingProcess;
        if (onboardingReader.existsByUserId(command.getUserId())) {
            onboardingProcess = onboardingReader.findOnboardingProcessByUserId(command.getUserId());
        } else {
            onboardingProcess = OnboardingProcess.start(command.getUserId());
            onboardingStore.store(onboardingProcess);
        }
        // 2. 카테고리 선택 로직 수행
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
        OnboardingProcess onboardingProcess = onboardingReader.findOnboardingProcessByUserId(command.getUserId());
        // 2. 서브카테고리 존재 검증
        SubCategory sub = subCategoryReader.getSubCategoryByIds(command.getCategoryId(), command.getSubCategoryId());
        // 3. 질문 묶음 조회
        List<QuestionInfo> questions = questionnaireService.getQuestionsByIds(command.getCategoryId(), command.getSubCategoryId());

        // 4. level, 선택형(optionId), 서술형을 검증 및 답변 생성
        List<OnboardingSubCategoryAnswers> newAnswers = new ArrayList<>();
        for (OnboardingCommand.SubmitSubCategoryAnswers.LevelAnswerCommand a : command.getAnswers()) {
            // LevelAnswerCommand.level(문자열)이 DB에 실제 존재하는 QuestionInfo.level(enum)과 일치하는지 검증
            QuestionInfo q = null;
            for (QuestionInfo cand : questions) {
                if (cand.getLevel().name().equals(a.getLevel())) {
                    q = cand;
                    break;
                }
            }
            if (q == null) {
                throw new BaseException("해당 레벨(" + a.getLevel() + ")의 질문이 존재하지 않습니다.", ErrorCode.VALIDATION_ERROR);
            }

            if (q.getType() == QuestionType.CHOICE) {
                if (a.getOptionId() == null) {
                    throw new BaseException("선택형 질문은 optionId가 필요합니다.", ErrorCode.VALIDATION_ERROR);
                }

                // q의 옵션들 중 요청된 optionId가 실제로 있는지 검증
                boolean optionExists = false;
                if (q.getOptions() != null) {
                    for (QuestionOptionInfo opt : q.getOptions()) {
                        if (opt.getOptionId().equals(a.getOptionId())) {
                            optionExists = true;
                            break;
                        }
                    }
                }
                if (!optionExists) {
                    throw new BaseException("유효하지 않은 optionId 입니다.", ErrorCode.VALIDATION_ERROR);
                }
                newAnswers.add(new OnboardingSubCategoryAnswers(
                        command.getSubCategoryId(),
                        q.getLevel().name(),
                        a.getOptionId(),
                        null
                ));
            } else { // TEXT
                if (a.getTextAnswer() == null || a.getTextAnswer().isBlank()) {
                    throw new BaseException("서술형 질문은 textAnswer가 필요합니다. (level=" + a.getLevel() + ")", ErrorCode.VALIDATION_ERROR);
                }
                newAnswers.add(new OnboardingSubCategoryAnswers(
                        command.getSubCategoryId(),
                        q.getLevel().name(),
                        null,
                        a.getTextAnswer()
                ));
            }
        }
        // 5. 도메인 set 함수 호출을 통해 간단한 검증 및 답변 추가
        onboardingProcess.setSubCategoryAnswers(command.getSubCategoryId(), newAnswers);
        // 6. 저장
        OnboardingProcess updated = onboardingStore.store(onboardingProcess);
        // 7. 결과 반환
        return new OnboardingInfo.Process(updated);
    }

    @Override
    @Transactional
    public OnboardingInfo.Process submitInteractionStyle(OnboardingCommand.SubmitInteraction command) {
        // 1. 온보딩 프로세스 조회
        OnboardingProcess process = onboardingReader.findOnboardingProcessByUserId(command.getUserId());
        // 2. 현재 상태 검증 + 교류성향 검증 및 온보딩 완료 처리
        process.setInteractionStyle(command.getInteractionStyle());
        // 3. ProfileService 호출 (다른 도메인)
        profileService.updateInteractionStyle(
                command.getUserId(),
                command.getInteractionStyle()
        );
        // 4. 온보딩 상태 저장
        onboardingStore.store(process);
        // 5. 결과 반환
        return new OnboardingInfo.Process(process);
    }

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<GeneratedCardBio> generateCardBio(String userPrompt) {
        GeneratedCardBio generatedResult = cardBioGenerator.generate(userPrompt);
        return CompletableFuture.completedFuture(generatedResult);
    }
}

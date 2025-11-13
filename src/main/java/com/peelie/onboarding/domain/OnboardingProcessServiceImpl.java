package com.peelie.onboarding.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.onboarding.infra.GptCardGenerationService;
import com.peelie.profile.domain.ProfileService;
import com.peelie.questionnaire.domain.category.SubCategory;
import com.peelie.questionnaire.domain.category.SubCategoryReader;
import com.peelie.questionnaire.domain.question.QuestionOptionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peelie.questionnaire.domain.QuestionnaireService;
import com.peelie.questionnaire.domain.question.QuestionInfo;
import com.peelie.questionnaire.domain.question.QuestionType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingProcessServiceImpl implements OnboardingProcessService {
    private final OnboardingReader onboardingReader;
    private final OnboardingStore onboardingStore;

    private final QuestionnaireService questionnaireService;
    private final SubCategoryReader subCategoryReader;
    private final ProfileService profileService;
    private final GptCardGenerationService gptCardGenerationService;

    private static final Duration GENERATION_TIMEOUT = Duration.ofSeconds(12);

    private final Map<Long, CompletableFuture<OnboardingInfo.CardGeneration>> taskStorage = new ConcurrentHashMap<>();

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

            //LevelAnswerCommand.level(문자열)이 DB에 실제 존재하는 QuestionInfo.level(enum)과 일치하는지 검증
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

                //q의 옵션들 중 요청된 optionId가 실제로 있는지 검증
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
                    throw new BaseException("유효하지 않은 optionId 입니다.",
                            ErrorCode.VALIDATION_ERROR);
                }
                newAnswers.add(new OnboardingSubCategoryAnswers( //선택형 VO 추가
                        command.getSubCategoryId(),
                        q.getLevel().name(),
                        a.getOptionId(),
                        null
                ));

            }

            else { // TEXT
                if (a.getTextAnswer() == null || a.getTextAnswer().isBlank()) {
                    throw new BaseException("서술형 질문은 textAnswer가 필요합니다. (level=" + a.getLevel() + ")",
                            ErrorCode.VALIDATION_ERROR);
                }
                newAnswers.add(new OnboardingSubCategoryAnswers( //서술형 VO 추가
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
        profileService.updateInteractionStyle( // TODO: 프로필 도메인 수정 시 추후 반영
                command.getUserId(),
                command.getInteractionStyle()
        );
        // 4. 온보딩 상태 저장
        onboardingStore.store(process);
        // 5. 결과 반환
        return new OnboardingInfo.Process(process);
    }
    @Override
    @Transactional
    public OnboardingInfo.CardGeneration initializeCard(OnboardingCommand.InitializeCard command) {
        if (command.getUserId() == null) {
            log.error("❌ userId is null — JWT 주입 안 됨");
            return OnboardingInfo.CardGeneration.failed();
        }

        Long userId = command.getUserId();

        // 1. 비동기 작업 시작
        CompletableFuture<OnboardingInfo.CardGeneration> future =
                gptCardGenerationService.generateCard(
                        userId,
                        command.getCategoryIds());

        // 2. [핵심] 작업 추적을 위해 Future를 Map에 저장
        taskStorage.put(userId, future);
        log.info("✅ GPT generation task STARTED and stored for user: {}", userId);

        // 3. [핵심] 작업 완료 시 콜백 연결 (DB 상태 업데이트 로직 대체)
        future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                // GPT API 오류, 타임아웃 등 비동기 작업 실패 시
                log.error("❌ GPT 카드 생성 비동기 작업 실패 (User: {})", userId, throwable);
                // TODO: DB 또는 캐시에 FAILED 상태 영속화 (현재는 Map에 저장된 Future 자체가 FAILED 상태를 가짐)
            } else {
                // GPT 작업 성공 시 (result.getGenerationStatus()가 'DONE' 또는 'FAILED'일 수 있음)
                log.info("✅ GPT 카드 생성 비동기 작업 완료 (User: {}, Status: {})", userId, result.getGenerationStatus());
                // TODO: DB 또는 캐시에 최종 결과(StageCard 포함) 영속화
            }
            // (주의: Map에서 제거하면 getCardGenerationStatus에서 최종 결과를 볼 수 없으므로 제거하지 않습니다.)
        });

        // 4. [핵심] HTTP 요청을 차단하지 않고, 즉시 'GENERATING' 상태 반환
        return OnboardingInfo.CardGeneration.generating();
    }

    //
//    @Override
//    @Transactional
//    public OnboardingInfo.CardGeneration initializeCard(OnboardingCommand.InitializeCard command) {
//        // TODO: 추후 OnboardingProcess 엔티티 상태 변경 로직과 통합 검토
//        if (command.getUserId() == null) {
//            log.error("❌ userId is null — JWT 주입 안 됨");
//            return OnboardingInfo.CardGeneration.failed();
//        }
//        OnboardingInfo.CardGeneration generating = OnboardingInfo.CardGeneration.generating();
//
//        try {
//            CompletableFuture<OnboardingInfo.CardGeneration> future =
//                    CompletableFuture.supplyAsync(() ->
//                            gptCardGenerationService.generateCard(
//                                    command.getUserId(),  // ✅ 실제 인증된 사용자 ID
//                                    command.getCategoryIds())
//                    );
//
//            return future.get(GENERATION_TIMEOUT.toSeconds(), TimeUnit.SECONDS);
//        } catch (Exception e) {
//            // TODO: 예외 로깅 및 추적 시스템 연동
//            log.error("❌  GPT 호출 실패: ", e.getMessage());
//            return OnboardingInfo.CardGeneration.failed();
//        }
//    }


    public OnboardingInfo.CardGeneration getCardGenerationStatus(Long userId) {
        CompletableFuture<OnboardingInfo.CardGeneration> future = taskStorage.get(userId);

        // 1. 작업(Future)이 존재하지 않는 경우 "No task found"
        if (future == null) {
            log.warn("⚠️ No generation task found for user {}", userId);
            return OnboardingInfo.CardGeneration.failed();
        }

        // 2. 작업이 아직 진행 중인 경우 (GENERATING)
        if (!future.isDone()) {
            log.debug("Polling user {}: Task is GENERATING", userId);
            return OnboardingInfo.CardGeneration.generating();
        }

        // 3. 작업이 완료된 경우
        try {
            // 3-a. 작업이 예외(실패)로 완료된 경우
            if (future.isCompletedExceptionally()) {
                // 비동기 스레드에서 예외 발생 시 (GPT 호출 실패, 타임아웃 등)
                log.warn("Polling user {}: Task is FAILED due to exception in async thread", userId);
                return OnboardingInfo.CardGeneration.failed();
            }

            // 3-b. 작업이 성공적으로 완료 (DONE 또는 내부 FAILED DTO 반환)
            // .getNow(null)은 예외 없이 즉시 결과를 반환합니다.
            OnboardingInfo.CardGeneration result = future.getNow(null);

            if (result != null) {
                log.debug("Polling user {}: Task is DONE (Result Status: {})", userId, result.getGenerationStatus());
                // Map에 남아있는 최종 결과 DTO를 반환
                return result;
            } else {
                log.error("❌ Polling user {}: Future completed but result was null unexpectedly. \"Unknown error during completion.\"", userId);
                return OnboardingInfo.CardGeneration.failed();
            }

            //        CompletableFuture<OnboardingInfo.CardGeneration> future = taskStorage.get(userId);
//
//        // 1. 작업(Future)이 존재하지 않는 경우
//        if (future == null) {
//            log.warn("⚠️ No generation task found for user {}", userId);
//            return OnboardingInfo.CardGeneration.failed();
//        }
//
//        // 2. 작업이 아직 진행 중인 경우 (GENERATING)
//        if (!future.isDone()) {
//            log.debug("Polling user {}: Task is GENERATING", userId);
//            return OnboardingInfo.CardGeneration.generating();
//        }
//
//        // 3. 작업이 완료된 경우
//        try {
//            if (future.isCompletedExceptionally()) {
//                // 3-a. 작업이 예외(실패)로 완료된 경우
//                log.warn("Polling user {}: Task is FAILED", userId);
//                return OnboardingInfo.CardGeneration.failed();
//            }
//
//            // 3-b. 작업이 성공적으로 완료 (DONE)
//            OnboardingInfo.CardGeneration result = future.getNow(null); // null은 기본값
//            log.debug("Polling user {}: Task is DONE", userId);
//            return result; // (result.getGenerationStatus()가 'DONE'임)

        } catch (Exception e) {
            // .getNow() 또는 .isCompletedExceptionally()에서 오류 발생 시
            log.error("❌ Error retrieving status for user {}", userId, e);
            return OnboardingInfo.CardGeneration.failed();
        }
    }


}

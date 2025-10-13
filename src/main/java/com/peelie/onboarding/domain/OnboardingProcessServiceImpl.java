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
    //추가 논의 사항: 1. categoryReader를 호출하여 유효성 검사를 진행해야할까 x -> 프론트에서 버튼형태로 입력받기 때문에
    //            2. SubCategory 입력도 받아야 할까 ( 어떤 식으로?) o
    //TODO subCategory 입력 받기


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
                    .anyMatch(opt -> opt.getOptionId().equals(Long.valueOf(input)));
            if (!optionExists) {
                throw new BaseException("선택형 질문의 옵션이 유효하지 않습니다.", ErrorCode.VALIDATION_ERROR);
            }
            value = input;
        }
        // 5. 도메인에서 status 검증
        process.setAnswers(target.getQuestionId(), value);
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
        // 2. 현재 상태 검증 + 교류성향/한줄소개 값 검증 및 온보딩 완료 처리
        process.setInteractionStyle(command.getInteractionStyle(), command.getBio());
        // 3. ProfileService 호출 (다른 도메인)
        profileService.updateInteractionStyle( //updateInteractionStyle service에서 구현 필요
                command.getUserId(),
                InteractionStyle.valueOf(command.getInteractionStyle().trim().toUpperCase())
        );
        profileService.updateBio( // 스프링 빈이 아니라 주입 불가능해서 service에서도 구현되어야 함 (이미 도메인에 있긴 함)
                command.getUserId(),
                command.getBio()
        );
        // 4. 온보딩 상태 저장
        onboardingStore.store(process);
        // 5. 결과 반환
        return new OnboardingInfo.Process(process);
    }

//reader 호출로 인해서 store는 굳이 필요는 없다는 사실 인지만 하기
//온보딩을 불러오려 초기온보딩 데이터를 입력받고 시작해야함
}

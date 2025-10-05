package com.peelie.onboarding.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.peelie.questionnaire.domain.category.CategoryReader;

@Service
@RequiredArgsConstructor
public class OnboardingProcessServiceImpl implements OnboardingProcessService {
    private final OnboardingReader onboardingReader;
    private final OnboardingStore onboardingStore;

    private final CategoryReader categoryReader;

    @Override
    @Transactional
    public OnboardingInfo.Process selectCategories(OnboardingCommand.SelectCategories command) {
        // 1. 유저의 온보딩 프로세스를 조회
        OnboardingProcess onboardingProcess = onboardingReader.findOnboardingProcessByUserId(command.getUserId());
        // 2. 카테고리 선택 로직 수행 (매개변수로 카테고리 reader 호출)
        onboardingProcess.selectCategories(command.getCategoryIds());
        //3. 변경된 상태를 저장
        OnboardingProcess updated = onboardingStore.store(onboardingProcess);
        //4; 결과 반환
        return new OnboardingInfo.Process(updated);
    }

}

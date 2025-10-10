package com.peelie.onboarding.domain;

public enum OnboardingStatus {
    INITIALIZED, //초기 상태
    CATEGORIES_PENDING, // 카테고리 선택 대기
    QUESTIONS_PENDING,  // 질문 답변 대기
    INTERACTIONSTYLE_PENDING, // 교류 성향, 한줄 소개 대기
    COMPLETED           // 온보딩 질문 답변 완료
}

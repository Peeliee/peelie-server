package com.peelie.onboarding.infra;

import com.peelie.onboarding.domain.OnboardingProcess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OnboardingProcessRepository extends JpaRepository<OnboardingProcess, Long> {
    Optional<OnboardingProcess> findByUserId(Long userId);
}
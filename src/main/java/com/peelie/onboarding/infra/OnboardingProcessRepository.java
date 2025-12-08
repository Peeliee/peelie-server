package com.peelie.onboarding.infra;

import com.peelie.onboarding.domain.OnboardingProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OnboardingProcessRepository extends JpaRepository<OnboardingProcess, Long> {
    Optional<OnboardingProcess> findByUserId(@Param("userId")  Long userId);
}
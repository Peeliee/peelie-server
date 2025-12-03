package com.peelie.onboarding.infra;

import com.peelie.onboarding.domain.OnboardingProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OnboardingProcessRepository extends JpaRepository<OnboardingProcess, Long> {
    //TODO: ??? 이거 조인 하는 이유가 없는 듯
    @Query("SELECT o FROM OnboardingProcess o LEFT JOIN FETCH o.subCategoryAnswers WHERE o.userId = :userId")
    Optional<OnboardingProcess> findByUserId(@Param("userId")  Long userId);
}
package com.peelie.questionnaire.infra.question;

import com.peelie.questionnaire.domain.question.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
}


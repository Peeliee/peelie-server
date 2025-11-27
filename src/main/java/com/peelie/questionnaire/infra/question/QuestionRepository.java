package com.peelie.questionnaire.infra.question;

import org.springframework.data.jpa.repository.JpaRepository;
import com.peelie.questionnaire.domain.question.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}



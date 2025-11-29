    package com.peelie.quiz.domain;

import com.peelie.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quiz")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Quiz extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false)
    private QuizStage stage;

    @Lob
    @Column(name = "quiz_question")
    private String question;

    @Column(name = "right_answer")
    private String rightAnswer;

    @Column(name = "wrong_answer")
    private String wrongAnswer;

    @Builder
    public Quiz(Long userId, QuizStage stage, String question, String rightAnswer, String wrongAnswer) {
        this.userId = userId;
        this.stage = stage;
        this.question = question;
        this.rightAnswer = rightAnswer;
        this.wrongAnswer = wrongAnswer;
    }
}

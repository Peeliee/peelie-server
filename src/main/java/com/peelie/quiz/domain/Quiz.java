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

    @Column(unique = true)
    private Long userId;

    @Lob
    @Column(name = "quiz_question")
    private String question;

    @Column(name = "right_answer")
    private String rightAnswer;

    @Column(name = "wrong_answer")
    private String wrongAnswer;

    @Builder
    public Quiz(Long userId, String question, String rightAnswer, String wrongAnswer) {
        this.userId = userId;
        this.question = question;
        this.rightAnswer = rightAnswer;
        this.wrongAnswer = wrongAnswer;
    }
}

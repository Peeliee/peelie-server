package com.peelie.questionnaire.domain.question;

import com.peelie.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "question_options")
@Getter
public class QuestionOption extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    public void setQuestion(Question question) {
        this.question = question;
    }
}

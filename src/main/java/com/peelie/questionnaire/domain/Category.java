package com.peelie.questionnaire.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.common.jpa.BaseTimeEntity;
import com.peelie.questionnaire.domain.question.ChoiceQuestion;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "onboarding_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String categoryName;

    // L0 ~ L3 객관식 질문들
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChoiceQuestion> choiceQuestions = new ArrayList<>();

    // L4 서술형 질문
    private String l4Question;

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public void addChoiceQuestion(ChoiceQuestion question) {
        if (choiceQuestions.size() >= 4) { // L0~L3
            throw new BaseException("객관식 질문은 4개까지 등록 가능합니다.", ErrorCode.VALIDATION_ERROR);
        }
        this.choiceQuestions.add(question);
    }

    public void updateL4Question(String l4Question) {
        this.l4Question = l4Question;
    }
}

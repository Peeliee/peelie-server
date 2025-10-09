package com.peelie.questionnaire.domain.question;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.common.jpa.BaseTimeEntity;
import com.peelie.questionnaire.domain.category.SubCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "onboarding_questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionLevel level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id")
    private SubCategory subCategory;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionOption> options = new ArrayList<>();

    public Question(QuestionLevel level, QuestionType type, String content) {
        this.level = level;
        this.type = type;
        this.content = content;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }

    public void addOption(QuestionOption option) {
        if (this.type == QuestionType.TEXT){
            throw new BaseException("서술형 질문에는 선택지를 추가할 수 없습니다", ErrorCode.VALIDATION_ERROR);
        }
        this.options.add(option);
        option.setQuestion(this);
    }
}

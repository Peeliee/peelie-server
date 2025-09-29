package com.peelie.questionnaire.domain.question;

import com.peelie.common.jpa.BaseTimeEntity;
import com.peelie.questionnaire.domain.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "choice_questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChoiceQuestion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private QuestionLevel level;

    @Column(nullable = false)
    private String content;

    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_content")
    private List<String> options = new ArrayList<>();

    @Builder
    public ChoiceQuestion(Category category, QuestionLevel level, String content, List<String> options) {
        this.category = category;
        this.level = level;
        this.content = content;
        this.options = options;
    }

    public void update(String content, List<String> options) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("질문내용을 입력해주세요");
        }
        if (options == null || options.size() != 4) {
            throw new IllegalArgumentException("객관식 질문은 4개여야합니다");
        }
        this.content = content;
        this.options = options;
    }
}

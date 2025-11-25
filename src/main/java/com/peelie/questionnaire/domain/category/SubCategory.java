package com.peelie.questionnaire.domain.category;

import com.peelie.common.jpa.BaseTimeEntity;
import com.peelie.questionnaire.domain.question.Question;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questionnaire_subcategories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubCategory extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // L1 ~ L3 객관식 질문들
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    public SubCategory(String name) {
        this.name = name;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
        question.setSubCategory(this);
    }
}

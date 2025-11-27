package com.peelie.questionnaire.domain.category;

import com.peelie.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questionnaire_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String categoryQuestion; // 기존 PRD 에서의 L0질문 역할

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubCategory> subCategories = new ArrayList<>();

    public Category(String name, String categoryQuestion) {
        this.name = name;
        this.categoryQuestion = categoryQuestion;
    }

    public void addSubCategory(SubCategory subCategory) {
        this.subCategories.add(subCategory);
        subCategory.setCategory(this);
    }
}

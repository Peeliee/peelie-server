package com.peelie.onboarding.domain;

import com.peelie.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OnboardingProcess extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private OnboardingStatus status;


    @ElementCollection
    @CollectionTable(name = "onboarding_selected_categories",
            joinColumns = @JoinColumn(name = "onboarding_process_id"))
    @Column(name = "category_id")
    private Set<Long> selectedCategories = new HashSet<>();


    @ElementCollection
    @CollectionTable (name = "onboarding_answers",
            joinColumns = @JoinColumn(name = "onboarding_process_id"))
    private List<OnboardingAnswer> answers = new ArrayList<>() {
    };



}

















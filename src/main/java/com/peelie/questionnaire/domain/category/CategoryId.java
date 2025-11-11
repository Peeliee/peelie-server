package com.peelie.questionnaire.domain.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryId {
    CONTENTS_MEDIA(1L, "콘텐츠/미디어"),
    MUSIC_ART(2L, "음악/예술"),
    SPORTS_EXERCISE(3L, "스포츠/운동"),
    TRAVEL_LIFESTYLE(4L, "여행/라이프스타일"),
    PETS(5L, "반려동물"),
    FOOD(6L, "음식"),
    PERSONALITY(7L, "성격");

    private final Long id;
    private final String name;

    public static CategoryId fromId(Long id) {
        for (CategoryId categoryId : values()) {
            if (categoryId.id.equals(id)) {
                return categoryId;
            }
        }
        return null;
    }

    public static String getNameById(Long id) {
        CategoryId categoryId = fromId(id);
        return categoryId != null ? categoryId.getName() : null;
    }
}

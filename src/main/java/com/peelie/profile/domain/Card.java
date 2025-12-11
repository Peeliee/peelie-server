package com.peelie.profile.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card {

    private String title;
    private String subtitle;
    private String content;

    public Card(String title, String subtitle, String content) {
        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
    }
}

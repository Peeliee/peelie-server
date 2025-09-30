package com.peelie.profile.domain;

public enum InteractionStyle {
    CAUTIOUS, //신중형
    BALANCED, //중간형
    FAST, //신속형
    UNKNOWN; //비정의

    public static InteractionStyle fromString(String value) {
        if (value == null) return UNKNOWN;
        try {
            return InteractionStyle.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
package com.peelie.common.util;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;

public class TokenGenerator {
    private static final int TOKEN_LENGTH = 20;

    private static final RandomStringGenerator alphanumericGenerator = new RandomStringGenerator.Builder()
            .withinRange('0', 'z')
            .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
            .get();

    public static String randomCharacter(int length) {
        return alphanumericGenerator.generate(length);
    }

    public static String randomCharacterWithPrefix(String prefix) {
        return prefix + randomCharacter(TOKEN_LENGTH - prefix.length());
    }
}
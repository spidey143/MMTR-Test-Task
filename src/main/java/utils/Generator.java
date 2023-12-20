package utils;

import java.util.Random;

public class Generator {

    public static final String ENGLISH_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final Random RANDOM = new Random();

    public static String generateRandomString() {
        StringBuilder resultStr = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            resultStr.append(ENGLISH_ALPHABET.charAt(RANDOM.nextInt(ENGLISH_ALPHABET.length())));
        }
        return resultStr.toString();
    }
}

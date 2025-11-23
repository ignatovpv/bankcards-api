package com.example.bankcards.util;

public class CardMaskUtil {
    private CardMaskUtil() {}

    public static String maskCard(String number) {
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}

package com.locationworkmanagerdemo.util;

import android.text.TextUtils;

public class CommonMethods {


    public static String getFormattedString(String word) {

        if (word == null || word.isEmpty()
                || "null".equalsIgnoreCase(word)
                || word.trim().length() <= 0
                || word.trim().isEmpty()
                || word.trim().equalsIgnoreCase("")
                || word.trim().equalsIgnoreCase("null")
                || TextUtils.isEmpty(word)) {
            return "";
        }
        return word;
    }

}

package com.lerenard.counter3.helper;

/**
 * Created by mc on 08-Dec-16.
 */
public class Trimmer {

    public static String trim(String string, int width) {
        if (string.length() <= width) {
            return string;
        }
        return string.substring(0, width - 3) + "...";
    }
}

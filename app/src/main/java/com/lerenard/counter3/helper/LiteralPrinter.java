package com.lerenard.counter3.helper;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by mc on 13-Dec-16.
 */

public class LiteralPrinter {

    private static HashMap<Character, Character> escapes;

    static {
        escapes = new HashMap<>();
        char[] unescaped = {'\b', '\t', '\n', '\f', '\r'};
        char[] escaped = {'b', 't', 'n', 'f', 'r'};
        for (int i = 0; i < unescaped.length; ++i) {
            escapes.put(unescaped[i], escaped[i]);
        }
    }

    private static String preprocess(String s) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < s.length(); ++i) {
            if (escapes.containsKey(s.charAt(i))) {
                sb.insert(i + 1, escapes.get(s.charAt(i)));
                sb.setCharAt(i, '\\');
            }
        }
        sb.append("\\0");
        return sb.toString();
    }

    public static void log(String tag, String output) {
        Log.d(tag, preprocess(output));
    }
}

package com.lerenard.counter3.helper;

/**
 * Created by mc on 13-Dec-16.
 */

public class StringUtils {
    private StringUtils() {}

    /**
     * @return s, with all escape sequences converted to literal versions
     */
    public static String literalify(String s) {
        return LiteralPrinter.literalify(s);
    }

    /**
     * @return text up to width characters. The last 3 are replaced with "..."
     */
    public static String trim(String text, int width) {
            if (text.length() <= width) {
                return text;
            }
            return text.substring(0, width - 3) + "...";
    }

    /**
     * @return the index of the ordinal'th instance of c in text, or -1 if it does not exist
     */
    public static int indexOf(String text, char c, int ordinal) {
        int res = -1;
        while (ordinal-- > 0) {
            res = text.indexOf(c, res);
        }
        return res;
    }
}

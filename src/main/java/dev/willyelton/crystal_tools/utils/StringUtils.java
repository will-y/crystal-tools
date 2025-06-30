package dev.willyelton.crystal_tools.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class StringUtils {
    public static String capitalize(String in) {
        if (in != null && in.length() > 1) {
            return in.substring(0, 1).toUpperCase(Locale.ROOT) + in.substring(1);
        }
        return in;
    }

    public static String formatFloat(float f) {
        if ((float)((int) f) == f) {
            return Integer.toString((int) f);
        } else {
            return Float.toString(f);
        }
    }

    public static String formatKey(String key) {
        if (key == null) return "";
        return Arrays.stream(key.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    public static String formatPercent(float f) {
        return Math.round(f * 100) + "%";
    }

    public static String stripRomanNumeral(String s) {
        if (s == null) return "";

        String[] split = s.split(" ");

        if (split.length == 0 || split.length == 1) return s;

        String lastElement = split[split.length - 1];

        if (isRomanNumeral(lastElement)) {
            List<String> result = new ArrayList<>(Arrays.stream(split).toList());
            result.removeLast();
            return String.join(" ", result);
        }

        return s;
    }

    private static boolean isRomanNumeral(String s) {
        return s.matches("^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$");
    }
}

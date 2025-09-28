package dev.willyelton.crystal_tools.utils;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class StringUtils {
    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getCompactNumberInstance(Locale.ROOT, NumberFormat.Style.SHORT);

    static {
        NUMBER_FORMAT.setMaximumFractionDigits(2);
    }

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
        return Arrays.stream(key.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    public static String formatPercent(float f) {
        return Math.round(f * 100) + "%";
    }

    public static String intToCompactString(int n) {
        return NUMBER_FORMAT.format(n);
    }
}

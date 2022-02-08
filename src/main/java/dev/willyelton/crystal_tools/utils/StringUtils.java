package dev.willyelton.crystal_tools.utils;

import java.util.Locale;

public class StringUtils {
    public static String capitalize(String in) {
        if (in != null && in.length() > 1) {
            return in.substring(0, 1).toUpperCase(Locale.ROOT) + in.substring(1);
        }
        return in;
    }
}

package dev.willyelton.crystal_tools.utils;

public class ArrayUtils {
    public static boolean arrayContains(int[] array, int element) {
        for (int i : array) {
            if (element == i) {
                return true;
            }
        }

        return false;
    }
}

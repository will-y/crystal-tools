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

    public static <T> int indexOf(T[] array, T element) {
        for (int i = 0; i < array.length; i++) {
            if (element.equals(array[i])) {
                return i;
            }
        }

        return -1;
    }

    public static int indexOf(int[] array, int element) {
        for (int i = 0; i < array.length; i++) {
            if (element == array[i]) {
                return i;
            }
        }

        return -1;
    }
}

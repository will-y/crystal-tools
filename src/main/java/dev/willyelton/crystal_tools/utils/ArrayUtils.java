package dev.willyelton.crystal_tools.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static <T> int[] indicesOf(T[] array, T element) {
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) result.add(i);
        }

        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    public static int[] combineArrays(int[] array1, int[] array2) {
        List<Integer> list1 = new ArrayList<>(Arrays.stream(array1).boxed().toList());
        List<Integer> list2 = Arrays.stream(array2).boxed().toList();

        list1.addAll(list2);

        return list1.stream().mapToInt(Integer::intValue).toArray();
    }
}

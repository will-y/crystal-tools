package dev.willyelton.crystal_tools.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    public static <T> List<T> flattenList(List<List<T>> inputList) {
        List<T> result = new ArrayList<>();

        for (List<T> innerList : inputList) {
            result.addAll(innerList);
        }

        return result;
    }
}

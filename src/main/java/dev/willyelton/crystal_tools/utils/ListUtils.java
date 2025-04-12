package dev.willyelton.crystal_tools.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {
    public static <T> List<T> flattenList(List<List<T>> inputList) {
        List<T> result = new ArrayList<>();

        for (List<T> innerList : inputList) {
            result.addAll(innerList);
        }

        return result;
    }

    public static <T> List<List<T>> partition(List<T> inputList, int size) {
        List<List<T>> result = new ArrayList<>();
        List<T> currentList = new ArrayList<>();
        result.add(currentList);
        for (int i = 0; i < inputList.size(); i++) {
            currentList.add(inputList.get(i));
            if ((i + 1) % size == 0) {
                currentList = new ArrayList<>();
                result.add(currentList);
            }

        }

        return result;
    }
}

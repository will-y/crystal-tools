package dev.willyelton.crystal_tools.utils;

import net.minecraft.world.level.storage.ValueInput;

public class NBTUtils {
    public static int[] getIntArray(ValueInput valueInput, String arrayKey, int size) {
        int[] array;

        if (valueInput.keySet().contains(arrayKey)) {
            array = valueInput.getIntArray(arrayKey).orElse(new int[size]);
            if (array.length == 0 && size > 0) {
                array = new int[size];
            }
        } else {
            if (size >= 0) {
                array = new int[size];
            } else {
                array = new int[0];
            }
        }

        return array;
    }
}

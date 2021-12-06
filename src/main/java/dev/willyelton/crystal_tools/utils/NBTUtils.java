package dev.willyelton.crystal_tools.utils;

import net.minecraft.nbt.CompoundTag;

public class NBTUtils {
    public static void addValueToTag(CompoundTag tag, String key, float value) {
        if (tag.contains(key)) {
            tag.putFloat(key, tag.getFloat(key) + value);
        } else {
            tag.putFloat(key, value);
        }
    }

    public static void addValueToTag(CompoundTag tag, String key, int value) {
        if (tag.contains(key)) {
            tag.putInt(key, tag.getInt(key) + value);
        } else {
            tag.putInt(key, value);
        }
    }
}

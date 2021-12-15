package dev.willyelton.crystal_tools.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class NBTUtils {
    public static void addValueToTag(ItemStack itemStack, String key, float value) {
        CompoundTag tag = getTag(itemStack);
        if (tag.contains(key)) {
            tag.putFloat(key, tag.getFloat(key) + value);
        } else {
            tag.putFloat(key, value);
        }
    }

//    public static void addValueToTag(ItemStack itemStack, String key, int value) {
//        CompoundTag tag = getTag(itemStack);
//        if (tag.contains(key)) {
//            tag.putInt(key, tag.getInt(key) + value);
//        } else {
//            tag.putInt(key, value);
//        }
//    }

//    public static int getIntOrAddKey(ItemStack itemStack, String key) {
//        CompoundTag tag = getTag(itemStack);
//        if (tag.contains(key)) {
//            return tag.getInt(key);
//        } else {
//            tag.putInt(key, 0);
//            return 0;
//        }
//    }

    public static float getFloatOrAddKey(ItemStack itemStack, String key) {
        CompoundTag tag = getTag(itemStack);
        if (tag.contains(key)) {
            return tag.getFloat(key);
        } else {
            tag.putFloat(key, 0);
            return 0;
        }
    }

//    public static void setValue(ItemStack itemStack, String key, int value) {
//        CompoundTag tag = getTag(itemStack);
//        tag.putInt(key, value);
//    }

    public static void setValue(ItemStack itemStack, String key, float value) {
        CompoundTag tag = getTag(itemStack);
        tag.putFloat(key, value);
    }

    public static void addValueToArray(ItemStack itemStack, String arrayKey, int index, int value) {
        CompoundTag tag = getTag(itemStack);
        int[] points = getIntArray(itemStack, arrayKey);

        points[index] += value;
        tag.putIntArray(arrayKey, points);
    }

    public static int[] getIntArray(ItemStack itemStack, String arrayKey) {
        CompoundTag tag = getTag(itemStack);
        int[] points;

        if (tag.contains(arrayKey)) {
            points = tag.getIntArray(arrayKey);
        } else {
            // 100 should be enough right?
            points = new int[100];
        }

        return points;
    }

    public static CompoundTag getTag(ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();

        if (tag == null) {
            tag = new CompoundTag();
            itemStack.setTag(tag);
        }

        return tag;
    }

}

package dev.willyelton.crystal_tools.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;

public class NBTUtils {

    public static float addValueToTag(ItemStack itemStack, String key, float value) {
        CompoundTag tag = getTag(itemStack);
        if (tag.contains(key)) {
            float original = tag.getFloat(key);
            tag.putFloat(key, original + value);
            return original + value;
        } else {
            tag.putFloat(key, value);
            return value;
        }
    }

    public static float getFloatOrAddKey(ItemStack itemStack, String key) {
        return getFloatOrAddKey(itemStack, key, 0);
    }

    public static float getFloatOrAddKey(ItemStack itemStack, String key, float defaultValue) {
        CompoundTag tag = getTag(itemStack);
        if (tag.contains(key)) {
            return tag.getFloat(key);
        } else {
            tag.putFloat(key, defaultValue);
            return defaultValue;
        }
    }

    public static void setValue(ItemStack itemStack, String key, float value) {
        CompoundTag tag = getTag(itemStack);
        tag.putFloat(key, value);
    }

    public static void setValue(ItemStack itemStack, String key, boolean value) {
        CompoundTag tag = getTag(itemStack);
        tag.putBoolean(key, value);
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

    public static boolean getBoolean(ItemStack itemStack, String key) {
        return getBoolean(itemStack, key, false);
    }

    public static boolean getBoolean(ItemStack itemStack, String key, boolean defaultValue) {
        CompoundTag tag = getTag(itemStack);

        if (tag.contains(key)) {
            return tag.getBoolean(key);
        } else {
            return defaultValue;
        }
    }
}

package dev.willyelton.crystal_tools.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;

public class NBTUtils {

    public static float addValueToTag(ItemStack itemStack, String key, float value) {
        CompoundTag tag = getTag(itemStack);
        return addValueToTag(tag, key, value);
    }

    public static float addValueToTag(CompoundTag tag, String key, float value) {
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
        return getFloatOrAddKey(tag, key, defaultValue);
    }

    public static float getFloatOrAddKey(CompoundTag tag, String key) {
        return getFloatOrAddKey(tag, key, 0);
    }

    public static float getFloatOrAddKey(CompoundTag tag, String key, float defaultValue) {
        if (tag.contains(key)) {
            return tag.getFloat(key);
        } else {
            tag.putFloat(key, defaultValue);
            return defaultValue;
        }
    }

    public static void setValue(CompoundTag tag, String key, float value) {
        tag.putFloat(key, value);
    }

    public static void setValue(ItemStack itemStack, String key, float value) {
        CompoundTag tag = getTag(itemStack);
        setValue(tag, key, value);
    }

    public static void setValue(ItemStack itemStack, String key, boolean value) {
        CompoundTag tag = getTag(itemStack);
        tag.putBoolean(key, value);
    }

    public static void setValue(ItemStack itemStack, String key, String value) {
        CompoundTag tag = getTag(itemStack);
        tag.putString(key, value);
    }

    public static void addValueToArray(ItemStack itemStack, String arrayKey, int index, int value) {
        addValueToArray(itemStack.getTag(), arrayKey, index, value);
    }

    public static void addValueToArray(CompoundTag tag, String arrayKey, int index, int value) {
        int[] points = getIntArray(tag, arrayKey);

        points[index] += value;
        tag.putIntArray(arrayKey, points);
    }

    public static int[] getIntArray(CompoundTag tag, String arrayKey) {
        return getIntArray(tag, arrayKey, 100);
    }

    public static int[] getIntArray(CompoundTag tag, String arrayKey, int size) {
        int[] array;

        if (tag.contains(arrayKey)) {
            array = tag.getIntArray(arrayKey);
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

    public static int[] getIntArray(ItemStack itemStack, String arrayKey) {
        if (itemStack.getTag() == null) {
            return new int[0];
        }

        return getIntArray(itemStack.getTag(), arrayKey, 100);
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

    public static String getString(ItemStack itemStack, String key) {
        return getString(itemStack, key, "");
    }

    public static String getString(ItemStack itemStack, String key, String defaultValue) {
        CompoundTag tag = getTag(itemStack);

        if (tag.contains(key)) {
            return tag.getString(key);
        } else {
            return defaultValue;
        }
    }
}

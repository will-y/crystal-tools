package dev.willyelton.crystal_tools.utils;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NBTUtils {
    public static int[] getIntArray(CompoundTag tag, String arrayKey, int size) {
        int[] array;

        if (tag.contains(arrayKey)) {
            array = tag.getIntArray(arrayKey).orElse(new int[size]);
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

    public static void storeItemStackArray(CompoundTag tag, List<ItemStack> stackList, HolderLookup.Provider levelRegistry) {
        ListTag listTag = new ListTag();

        for (ItemStack stack : stackList) {
            CompoundTag stackTag = new CompoundTag();
            listTag.add(stack.save(levelRegistry, stackTag));
        }

        if (!listTag.isEmpty()) {
            tag.put("Items", listTag);
        }
    }

    public static List<ItemStack> getItemStackArray(CompoundTag tag, HolderLookup.Provider levelRegistry) {
        List<ItemStack> stacks = new ArrayList<>();
        ListTag listTag = tag.getList("Items").orElse(new ListTag());

        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag stackTag = listTag.getCompound(i).orElse(new CompoundTag());
            ItemStack stack = ItemStack.parse(levelRegistry, stackTag).orElse(ItemStack.EMPTY);

            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }

        return stacks;
    }
}

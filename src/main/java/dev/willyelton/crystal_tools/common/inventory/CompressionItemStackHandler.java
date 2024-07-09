package dev.willyelton.crystal_tools.common.inventory;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.ComponentItemHandler;

import java.util.ArrayList;
import java.util.List;

public class CompressionItemStackHandler extends ComponentItemHandler {
    private List<CompressionMode> slotModes;

    public CompressionItemStackHandler(ItemStack stack, DataComponentType<ItemContainerContents> component, int size) {
        super(stack, component, size);
        slotModes = new ArrayList<>(size / 2);

        for (int i = 0; i < size / 2; i++) {
            slotModes.add(CompressionMode.NONE);
        }
    }

    public CompressionMode getMode(int slot) {
        return slotModes.get(slot / 2);
    }

    public void setMode(CompressionMode mode, int slot) {
        this.slotModes.set(slot / 2, mode);
    }

    public enum CompressionMode {
        THREE_BY_THREE(9), TWO_BY_TWO(4), NONE(0);

        private final int count;

        CompressionMode(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public int toInt() {
            return this.ordinal();
        }

        public static CompressionMode fromInt(int value) {
            return values()[value];
        }
    }
}

package dev.willyelton.crystal_tools.common.inventory;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.transfer.access.ItemAccess;

import java.util.ArrayList;
import java.util.List;

public class CompressionItemStackHandler extends ItemAccessItemHandlerModifier {
    private List<CompressionMode> slotModes;
    private final ItemStack parent;

    public CompressionItemStackHandler(ItemStack stack, DataComponentType<ItemContainerContents> component, int size) {
        super(ItemAccess.forStack(stack), component, size);
        this.parent = stack;
        slotModes = new ArrayList<>(size / 2);
        List<Integer> storedSlotModes = stack.getOrDefault(DataComponents.COMPRESSION_MODES, new ArrayList<>());
        for (Integer storedSlotMode : storedSlotModes) {
            slotModes.add(CompressionMode.fromInt(storedSlotMode));
        }

        for (int i = storedSlotModes.size(); i < size / 2; i++) {
            slotModes.add(CompressionMode.NONE);
        }
    }

    public CompressionMode getMode(int slot) {
        return slotModes.get(slot / 2);
    }

    public void setMode(CompressionMode mode, int slot) {
        this.slotModes.set(slot / 2, mode);
        this.parent.set(DataComponents.COMPRESSION_MODES, this.slotModes.stream().map(CompressionMode::toInt).toList());
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

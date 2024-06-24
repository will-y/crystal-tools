package dev.willyelton.crystal_tools.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class CompressionItemStackHandler extends ItemStackHandler {
    private List<CompressionMode> slotModes;

    public CompressionItemStackHandler(int size) {
        super(size);
        slotModes = new ArrayList<>(size / 2);

        for (int i = 0; i < size / 2; i++) {
            slotModes.add(CompressionMode.NONE);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putIntArray("modes", slotModes.stream().map(CompressionMode::toInt).toList());

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("modes")) {
            int[] modes = nbt.getIntArray("modes");
            for (int i = 0; i < modes.length; i++) {
                slotModes.set(i, CompressionMode.fromInt(modes[i]));
            }
        }
    }

    public CompressionMode getMode(int slot) {
        return slotModes.get(slot / 2);
    }

    // TODO: In all of these methods we need to map from slot indexes to mode indexes
    public void setMode(CompressionMode mode, int slot) {
        this.slotModes.set(slot / 2, mode);
    }

    public List<CompressionMode> getModes() {
        return slotModes;
    }

    public void setModes(List<CompressionMode> modes) {
        this.slotModes = modes;
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

package dev.willyelton.crystal_tools.levelable.block.container.slot;

import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;

import java.util.Arrays;

public class CrystalFurnaceOutputSlot extends FurnaceResultSlot {
    private final CrystalFurnaceBlockEntity crystalFurnaceBlockEntity;

    public CrystalFurnaceOutputSlot(Player player, CrystalFurnaceBlockEntity crystalFurnaceBlockEntity, int pSlot, int pX, int pY) {
        super(player, crystalFurnaceBlockEntity, pSlot, pX, pY);
        this.crystalFurnaceBlockEntity = crystalFurnaceBlockEntity;
    }

    @Override
    public boolean isActive() {
        return ArrayUtils.arrayContains(crystalFurnaceBlockEntity.getActiveOutputSlots(), this.index);
    }
}

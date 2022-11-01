package dev.willyelton.crystal_tools.levelable.block.container.slot;

import dev.willyelton.crystal_tools.levelable.block.container.CrystalFurnaceContainer;
import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;

import java.util.Arrays;

public class CrystalFurnaceOutputSlot extends FurnaceResultSlot {
    private final CrystalFurnaceContainer crystalFurnaceContainer;

    public CrystalFurnaceOutputSlot(Player player, CrystalFurnaceContainer crystalFurnaceContainer, int pSlot, int pX, int pY) {
        super(player, crystalFurnaceContainer.getBlockEntity(), pSlot, pX, pY);
        this.crystalFurnaceContainer = crystalFurnaceContainer;
    }

    @Override
    public boolean isActive() {
        return ArrayUtils.arrayContains(crystalFurnaceContainer.getActiveOutputSlots(), this.index);
    }
}

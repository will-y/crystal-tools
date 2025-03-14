package dev.willyelton.crystal_tools.common.inventory.container.slot.furnace;

import dev.willyelton.crystal_tools.common.inventory.container.CrystalFurnaceContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.EventHooks;

public class CrystalFurnaceOutputSlot extends FurnaceResultSlot {
    private final CrystalFurnaceContainerMenu crystalFurnaceContainerMenu;
    private final Player player;
    private int removeCount;

    public CrystalFurnaceOutputSlot(Player player, CrystalFurnaceContainerMenu crystalFurnaceContainerMenu, int pSlot, int pX, int pY) {
        super(player, crystalFurnaceContainerMenu.getBlockEntity(), pSlot, pX, pY);
        this.crystalFurnaceContainerMenu = crystalFurnaceContainerMenu;
        this.player = player;
    }

    @Override
    public boolean isActive() {
        return ArrayUtils.arrayContains(crystalFurnaceContainerMenu.getActiveOutputSlots(), this.index);
    }

    protected void checkTakeAchievements(ItemStack stack) {
        stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
        if (this.player instanceof ServerPlayer && this.container instanceof CrystalFurnaceBlockEntity crystalFurnaceBlockEntity) {
            crystalFurnaceBlockEntity.popExp((ServerPlayer)this.player);
        }

        if (this.removeCount != 0) {
            EventHooks.firePlayerSmeltedEvent(this.player, stack, this.removeCount);
        }

        this.removeCount = 0;
    }

    @Override
    public ItemStack remove(int pAmount) {
        if (this.hasItem()) {
            this.removeCount += Math.min(pAmount, this.getItem().getCount());
        }

        return super.remove(pAmount);
    }

    protected void onQuickCraft(ItemStack pStack, int pAmount) {
        this.removeCount += pAmount;
        this.checkTakeAchievements(pStack);
    }
}

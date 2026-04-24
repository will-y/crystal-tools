package dev.willyelton.crystal_tools.common.inventory.container.slot.furnace;

import dev.willyelton.crystal_tools.common.inventory.container.CrystalFurnaceContainerMenu;
import dev.willyelton.crystal_tools.api.common.inventory.container.slot.CrystalSlotItemHandler;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalFurnaceBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class CrystalFurnaceOutputSlot extends CrystalSlotItemHandler {
    private final CrystalFurnaceContainerMenu crystalFurnaceContainerMenu;
    private final Player player;
    private int removeCount = 0;

    public CrystalFurnaceOutputSlot(CrystalFurnaceContainerMenu crystalFurnaceContainerMenu, ItemStacksResourceHandler itemHandler,
                                    Player player, int index, int x, int y) {
        super(itemHandler, index, x, y);

        this.crystalFurnaceContainerMenu = crystalFurnaceContainerMenu;
        this.player = player;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isActive() {
        return super.isActive() && crystalFurnaceContainerMenu.getNumActiveSlots() > this.getIndex();
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);
        super.onTake(player, stack);
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        stack.onCraftedBy(this.player, this.removeCount);
        if (this.player instanceof ServerPlayer && this.container instanceof CrystalFurnaceBlockEntity crystalFurnaceBlockEntity) {
            crystalFurnaceBlockEntity.popExp((ServerPlayer)this.player);
        }

        if (this.removeCount != 0) {
            EventHooks.firePlayerSmeltedEvent(this.player, stack, this.removeCount);
        }

        this.removeCount = 0;
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        this.removeCount += amount;
        this.checkTakeAchievements(stack);
    }
}

package dev.willyelton.crystal_tools.common.inventory.container;

import dev.willyelton.crystal_tools.common.inventory.container.slot.CrystalSlotItemHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public abstract class BaseContainerMenu extends AbstractContainerMenu {
    protected final InvWrapper playerInventory;

    protected BaseContainerMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory playerInventory) {
        super(pMenuType, pContainerId);
        this.playerInventory = new InvWrapper(playerInventory);
    }

    protected int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(handler, index, x, y);
            x += dx;
            index++;
        }
        return index;
    }

    protected int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    protected void addSlot(IItemHandler handler, int index, int x, int y) {
        addSlot(new CrystalSlotItemHandler(handler, index, x, y));
    }

    protected void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        layoutHotbar(leftCol, topRow + 58);

    }

    protected void layoutHotbar(int leftCol, int topRow) {
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
}

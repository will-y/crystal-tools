package dev.willyelton.crystal_tools.common.inventory.container;

import dev.willyelton.crystal_tools.client.gui.SlotFactory;
import dev.willyelton.crystal_tools.common.inventory.container.slot.CrystalSlotItemHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public abstract class BaseContainerMenu extends AbstractContainerMenu {
    protected static final int SLOT_SIZE = 18;

    protected final InvWrapper playerInventory;
    protected final Player player;
    protected final ContainerData data;
    protected final Level level;

    protected BaseContainerMenu(MenuType<?> pMenuType, int pContainerId, Inventory playerInventory, ContainerData data) {
        super(pMenuType, pContainerId);
        this.playerInventory = new InvWrapper(playerInventory);
        this.player = playerInventory.player;
        this.data = data;
        this.level = player.level();

        if (data != null) {
            this.addDataSlots(data);
        }
    }

    protected <T extends Slot> void addSlot(IItemHandler handler, int index, int x, int y, @Nullable NonNullList<T> slotList, SlotFactory<T> slotFactory) {
        T slot = slotFactory.create(handler, index, x, y);
        addSlot(slot);
        if (slotList != null) {
            slotList.add(slot);
        }
    }

    protected void addSlot(IItemHandler handler, int index, int x, int y) {
        addSlot(handler, index, x, y, null, CrystalSlotItemHandler::new);
    }

    protected int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        return addSlotRange(handler, index, x, y, amount, dx, null, CrystalSlotItemHandler::new);
    }

    protected <T extends Slot> int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx, @Nullable NonNullList<T> slotList, SlotFactory<T> slotFactory) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(handler, index, x, y, slotList, slotFactory);
            x += dx;
            index++;
        }
        return index;
    }

    protected int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        return addSlotBox(handler, index, x, y, horAmount, dx, verAmount, dy, null, CrystalSlotItemHandler::new);
    }

    protected <T extends Slot> int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy, @Nullable NonNullList<T> slotList, SlotFactory<T> slotFactory) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx, slotList, slotFactory);
            y += dy;
        }
        return index;
    }

    protected void layoutPlayerInventorySlots(int leftCol, int topRow) {
        layoutPlayerInventorySlots(leftCol, topRow, null, CrystalSlotItemHandler::new);
    }

    protected <T extends Slot> void layoutPlayerInventorySlots(int leftCol, int topRow, @Nullable NonNullList<T> slotList, SlotFactory<T> slotFactory) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18, slotList, slotFactory);

        layoutHotbar(leftCol, topRow + 58, slotList, slotFactory);
    }

    protected void layoutHotbar(int leftCol, int topRow) {
        layoutHotbar(leftCol, topRow, null, CrystalSlotItemHandler::new);
    }

    protected <T extends Slot> void layoutHotbar(int leftCol, int topRow, @Nullable NonNullList<T> slotList, SlotFactory<T> slotFactory) {
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18, slotList, slotFactory);
    }
}

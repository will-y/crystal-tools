package dev.willyelton.crystal_tools.common.inventory.container;

import dev.willyelton.crystal_tools.client.gui.SlotFactory;
import dev.willyelton.crystal_tools.common.inventory.container.slot.CrystalSlotItemHandler;
import dev.willyelton.crystal_tools.utils.TransferUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import org.jetbrains.annotations.Nullable;

public abstract class BaseContainerMenu extends AbstractContainerMenu {
    protected static final int SLOT_SIZE = 18;

    protected final PlayerInventoryWrapper playerInventory;
    protected final Player player;
    protected final ContainerData data;
    protected final Level level;

    protected BaseContainerMenu(MenuType<?> pMenuType, int pContainerId, Inventory playerInventory, ContainerData data) {
        super(pMenuType, pContainerId);
        this.playerInventory = PlayerInventoryWrapper.of(playerInventory);
        this.player = playerInventory.player;
        this.data = data;
        this.level = player.level();

        if (data != null) {
            this.addDataSlots(data);
        }
    }

    protected <T extends Slot> void addSlot(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> indexModifier, int index, int x, int y, @Nullable NonNullList<T> slotList, SlotFactory<T> slotFactory) {
        T slot = slotFactory.create(handler, indexModifier, index, x, y);
        addSlot(slot);
        if (slotList != null) {
            slotList.add(slot);
        }
    }

    protected void addSlot(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> indexModifier, int index, int x, int y) {
        addSlot(handler, indexModifier, index, x, y, null, CrystalSlotItemHandler::new);
    }

    protected <T extends Slot> void addSlot(ItemStacksResourceHandler handler, int index, int x, int y, @Nullable NonNullList<T> slotList, SlotFactory<T> slotFactory) {
        addSlot(handler, handler::set, index, x, y, slotList, slotFactory);
    }

    protected void addSlot(ItemStacksResourceHandler handler, int index, int x, int y) {
        addSlot(handler, index, x, y, null, CrystalSlotItemHandler::new);
    }

    protected int addSlotRange(ItemStacksResourceHandler handler, int index, int x, int y, int amount, int dx) {
        return addSlotRange(handler, handler::set, index, x, y, amount, dx, null, CrystalSlotItemHandler::new);
    }

    protected <T extends Slot> int addSlotRange(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> indexModifier, int index, int x, int y, int amount, int dx, @Nullable NonNullList<T> slotList, SlotFactory<T> slotFactory) {
        for (int i = 0; i < amount; i++) {
            addSlot(handler, indexModifier, index, x, y, slotList, slotFactory);
            x += dx;
            index++;
        }
        return index;
    }

    protected int addSlotBox(ItemStacksResourceHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        return addSlotBox(handler, index, x, y, horAmount, dx, verAmount, dy, null, CrystalSlotItemHandler::new);
    }

    protected <T extends Slot> int addSlotBox(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> indexModifier, int index, int x, int y, int horAmount, int dx, int verAmount, int dy, @Nullable NonNullList<T> slotList, SlotFactory<T> slotFactory) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, indexModifier, index, x, y, horAmount, dx, slotList, slotFactory);
            y += dy;
        }
        return index;
    }

    protected <T extends Slot> int addSlotBox(ItemStacksResourceHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy, @Nullable NonNullList<T> slotList, SlotFactory<T> slotFactory) {
        return addSlotBox(handler, handler::set, index, x, y, horAmount, dx, verAmount, dy, slotList, slotFactory);
    }

    protected void layoutPlayerInventorySlots(int leftCol, int topRow) {
        layoutPlayerInventorySlots(leftCol, topRow, null, CrystalSlotItemHandler::new);
    }

    protected <T extends Slot> void layoutPlayerInventorySlots(int leftCol, int topRow, @Nullable NonNullList<T> slotList, SlotFactory<T> slotFactory) {
        // Player inventory
//        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18, slotList, slotFactory);
        int index = 9;
        int y = topRow;
        for (int j = 0; j < 3; j++) {
            int x = leftCol;
            for (int i = 0; i < 9; i++) {
                T slot = slotFactory.create(playerInventory, TransferUtils.playerIndexModifier(player.getInventory()), index, x, y);
                addSlot(slot);
                if (slotList != null) {
                    slotList.add(slot);
                }
                x += 18;
                index++;
            }
            y += 18;
        }

        layoutHotbar(leftCol, topRow + 58, slotList, slotFactory);
    }

    protected <T extends Slot> void layoutHotbar(int leftCol, int topRow, @Nullable NonNullList<T> slotList, SlotFactory<T> slotFactory) {
//        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18, slotList, slotFactory);
        int x = leftCol;
        int index = 0;
        for (int i = 0; i < 9; i++) {
            T slot = slotFactory.create(playerInventory, TransferUtils.playerIndexModifier(player.getInventory()), index, x, topRow);
            addSlot(slot);
            if (slotList != null) {
                slotList.add(slot);
            }
            x += 18;
            index++;
        }
    }
}

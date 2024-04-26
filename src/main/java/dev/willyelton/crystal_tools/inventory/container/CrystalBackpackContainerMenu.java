package dev.willyelton.crystal_tools.inventory.container;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.inventory.container.slot.ReadOnlySlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.logging.log4j.Level;

public class CrystalBackpackContainerMenu extends BaseContainerMenu {
    public static final int START_Y = 18;
    private static final int START_X = 8;
    private static final int SLOT_SIZE = 18;
    private static final int SLOTS_PER_ROW = 9;
    // TODO: will be on stack or something
    private static final int FILTER_SLOTS = 5;
    private final CrystalBackpackInventory inventory;
    private final ItemStackHandler filterInventory;
    private final ItemStack stack;
    private final int rows;

    // Client constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, new CrystalBackpackInventory(data.readInt() * 9), ItemStack.EMPTY);
    }

    // Server constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, CrystalBackpackInventory backpackInventory, ItemStack stack) {
        super(Registration.CRYSTAL_BACKPACK_CONTAINER.get(), containerId, playerInventory);
        this.inventory = backpackInventory;
        this.stack = stack;
        this.rows = backpackInventory.getSlots() / 9;
        this.filterInventory = createFilterInventory(stack);
        this.layoutPlayerInventorySlots(START_X, START_Y + rows * SLOT_SIZE + 14);
        this.addSlotBox(backpackInventory, 0, START_X, START_Y, SLOTS_PER_ROW, SLOT_SIZE, rows, 18);
        this.addSlotBox(filterInventory, 0, START_X + SLOTS_PER_ROW * SLOT_SIZE + 11, START_Y, 5, SLOT_SIZE, 1, SLOT_SIZE);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem() && !isFilterSlot(index)) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            // if you clicked in played inventory
            if (index < 36) {
               if (!this.moveItemStackTo(itemstack1, 36, slots.size() - filterInventory.getSlots(), false)) {
                   return ItemStack.EMPTY;
               }
               // clicked in backpack
            } else if (!this.moveItemStackTo(itemstack1, 0, 36, true)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stack != null;
    }

    @Override
    protected void layoutHotbar(int leftCol, int topRow) {
        int index = 0;
        int x = leftCol;
        for (int i = 0 ; i < 9 ; i++) {
            if (playerInventory.getInv().getItem(index).is(Registration.CRYSTAL_BACKPACK.get())) {
                addSlot(new ReadOnlySlot(playerInventory, index, x, topRow));
            } else {
                addSlot(new SlotItemHandler(playerInventory, index, x, topRow));
            }
            x += 18;
            index++;
        }
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        stack.getOrCreateTag().put("filter", filterInventory.serializeNBT());
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (!this.isFilterSlot(slotId)) {
            super.clicked(slotId, button, clickType, player);
            return;
        }

        if (clickType == ClickType.THROW || clickType == ClickType.CLONE) {
            return;
        }

        ItemStack held = getCarried();
        int filterIndex = slotId - getNonFilterSlots();
        ItemStack toInsert;
        if (held.isEmpty()) {
            toInsert = ItemStack.EMPTY;
        } else {
            toInsert = held.copy();
            toInsert.setCount(1);
        }
        filterInventory.setStackInSlot(filterIndex, toInsert);
        getSlot(slotId).setChanged();
    }

    public int getRows() {
        return rows;
    }

    public int getFilterRows() {
        return this.filterInventory.getSlots() / 5;
    }

    private ItemStackHandler createFilterInventory(ItemStack stack) {
        // TODO this doesn't upgrade yet
        ItemStackHandler itemStackHandler = new ItemStackHandler(FILTER_SLOTS);
        CompoundTag tag = stack.getOrCreateTagElement("filter");
        if (!tag.isEmpty()) {
            itemStackHandler.deserializeNBT(tag);
        }

        return itemStackHandler;
    }

    private boolean isFilterSlot(int index) {
        return index >= getNonFilterSlots();
    }

    private int getNonFilterSlots() {
        return 36 + rows * SLOTS_PER_ROW;
    }
}

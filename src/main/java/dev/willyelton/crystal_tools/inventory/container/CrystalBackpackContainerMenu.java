package dev.willyelton.crystal_tools.inventory.container;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.inventory.container.slot.ReadOnlySlot;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.packet.BackpackScreenPacket;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CrystalBackpackContainerMenu extends BaseContainerMenu {
    public static final int START_Y = 18;
    private static final int START_X = 8;
    private static final int SLOT_SIZE = 18;
    private static final int SLOTS_PER_ROW = 9;
    public static final int FILTER_SLOTS_PER_ROW = 5;
    private final CrystalBackpackInventory inventory;
    @Nullable
    private final ItemStackHandler filterInventory;
    private final ItemStack stack;
    private final int rows;
    private final int filterRows;
    private boolean whitelist;

    // Client constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, new CrystalBackpackInventory(data.readInt() * 9), ItemStack.EMPTY,
                data.readInt());
    }

    // Server constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, CrystalBackpackInventory backpackInventory,
                                        ItemStack stack, int filterRows) {
        super(Registration.CRYSTAL_BACKPACK_CONTAINER.get(), containerId, playerInventory);
        this.inventory = backpackInventory;
        this.stack = stack;
        this.rows = backpackInventory.getSlots() / SLOTS_PER_ROW;
        this.filterRows = filterRows;
        this.filterInventory = createFilterInventory(stack);
        this.layoutPlayerInventorySlots(START_X, START_Y + rows * SLOT_SIZE + 14);
        this.addSlotBox(backpackInventory, 0, START_X, START_Y, SLOTS_PER_ROW, SLOT_SIZE, rows, 18);
        if (Objects.nonNull(filterInventory)) {
            this.addSlotBox(filterInventory, 0, START_X + SLOTS_PER_ROW * SLOT_SIZE + 11, START_Y, FILTER_SLOTS_PER_ROW, SLOT_SIZE, filterRows, SLOT_SIZE);
        }

        whitelist = NBTUtils.getBoolean(stack, "whitelist");
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
               if (!this.moveItemStackTo(itemstack1, 36, slots.size() - getFilterSlots(), false)) {
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
        if (Objects.nonNull(filterInventory)) {
            stack.getOrCreateTag().put("filter", filterInventory.serializeNBT());
        }

        stack.getOrCreateTag().putBoolean("whitelist", whitelist);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (!this.isFilterSlot(slotId)) {
            super.clicked(slotId, button, clickType, player);
            return;
        }

        if (Objects.isNull(filterInventory) || clickType == ClickType.THROW || clickType == ClickType.CLONE) {
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
        return filterRows;
    }

    public boolean getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(boolean whitelist) {
        this.whitelist = whitelist;
    }

    public void sendUpdatePacket(BackpackScreenPacket.Type type) {
        PacketHandler.sendToServer(new BackpackScreenPacket(type));
    }

    public void sort() {
        this.inventory.sort(CrystalToolsConfig.BACKPACK_SORT_TYPE.get());
    }

    private int getFilterSlots() {
        if (Objects.isNull(filterInventory)) {
            return 0;
        }

        return filterInventory.getSlots();
    }

    private ItemStackHandler createFilterInventory(ItemStack stack) {
        if (filterRows == 0) {
            return null;
        }

        ItemStackHandler itemStackHandler = new ItemStackHandler(filterRows * FILTER_SLOTS_PER_ROW);
        ItemStackHandler storedItems = new ItemStackHandler(0);
        CompoundTag tag = stack.getOrCreateTagElement("filter");
        if (!tag.isEmpty()) {
            storedItems.deserializeNBT(tag);
        }

        InventoryUtils.copyTo(storedItems, itemStackHandler);

        return itemStackHandler;
    }

    private boolean isFilterSlot(int index) {
        return index >= getNonFilterSlots();
    }

    private int getNonFilterSlots() {
        return 36 + rows * SLOTS_PER_ROW;
    }
}

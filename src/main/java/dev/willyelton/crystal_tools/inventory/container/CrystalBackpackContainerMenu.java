package dev.willyelton.crystal_tools.inventory.container;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.gui.ScrollableMenu;
import dev.willyelton.crystal_tools.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.inventory.container.slot.CompressionInputSlot;
import dev.willyelton.crystal_tools.inventory.container.slot.CrystalSlotItemHandler;
import dev.willyelton.crystal_tools.inventory.container.slot.ReadOnlySlot;
import dev.willyelton.crystal_tools.inventory.container.slot.ScrollableSlot;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.packet.BackpackScreenPacket;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CrystalBackpackContainerMenu extends BaseContainerMenu implements ScrollableMenu {
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
    private int maxRows;
    private boolean canSort;
    private final NonNullList<ScrollableSlot> backpackSlots;

    // Client constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, new CrystalBackpackInventory(data.readInt() * 9), ItemStack.EMPTY,
                data.readInt(), data.readBoolean(), data.readBoolean());
    }

    // Server constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, CrystalBackpackInventory backpackInventory,
                                        ItemStack stack, int filterRows, boolean whitelist, boolean canSort) {
        super(Registration.CRYSTAL_BACKPACK_CONTAINER.get(), containerId, playerInventory);
        this.inventory = backpackInventory;
        this.stack = stack;
        this.rows = backpackInventory.getSlots() / SLOTS_PER_ROW;
        this.filterRows = filterRows;
        this.filterInventory = createFilterInventory(stack);
        this.backpackSlots = NonNullList.createWithCapacity(rows * SLOTS_PER_ROW);
        this.whitelist = whitelist;
        this.canSort = canSort;
    }

    @Override
    protected void addSlot(IItemHandler handler, int index, int x, int y) {
        if (handler instanceof CrystalBackpackInventory) {
            ScrollableSlot slot = new ScrollableSlot(handler, index, x, y);
            backpackSlots.add(slot);
            addSlot(slot);
        } else {
            CrystalSlotItemHandler slot = new CrystalSlotItemHandler(handler, index, x, y);
            addSlot(slot);
        }
    }

    private void setUpPlayerSlots() {
        this.layoutPlayerInventorySlots(START_X, START_Y + Math.min(maxRows, rows) * SLOT_SIZE + 14);
    }

    private void setUpBackpackSlots() {
        int rowsToDraw;

        if (maxRows == 0) {
            rowsToDraw = rows;
        } else {
            rowsToDraw = Math.min(rows, maxRows);
        }

        this.addSlotBox(this.inventory, 0, START_X, START_Y, SLOTS_PER_ROW, SLOT_SIZE, rowsToDraw, SLOT_SIZE);
    }

    private void setUpFilterSlots() {
        if (Objects.nonNull(filterInventory)) {
            int xOffset = canScroll() ? 18 : 0;
            this.addSlotBox(filterInventory, 0, START_X + SLOTS_PER_ROW * SLOT_SIZE + xOffset + 11, START_Y, FILTER_SLOTS_PER_ROW, SLOT_SIZE, filterRows, SLOT_SIZE);
        }
    }

    @Override
    public void setUpSlots() {
        setUpPlayerSlots();
        setUpBackpackSlots();
        setUpFilterSlots();
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
                   // Need to check if there is room in inventory off-screen
                   // TODO: Works, little bit of a client desync though
                   itemstack1 = inventory.insertStack(itemstack);
                   if (!itemstack1.isEmpty()) {
                       return ItemStack.EMPTY;
                   }
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
                addSlot(new CrystalSlotItemHandler(playerInventory, index, x, topRow));
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
        // TODO: Do something similar with filter slots
        if (getSlot(slotId) instanceof CompressionInputSlot compressionInputSlot) {
            compressionInputSlot.onClicked(getCarried());
        } else if (this.isFilterSlot(slotId)) {
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
        } else {
            super.clicked(slotId, button, clickType, player);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getFilterRows() {
        return filterRows;
    }

    public boolean canSort() {
        return canSort;
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
        int displayedRows;
        if (maxRows == 0) {
            displayedRows = rows;
        } else {
            displayedRows = Math.min(rows, maxRows);
        }
        return 36 + displayedRows * SLOTS_PER_ROW;
    }

    @Override
    public int getRowIndexForScroll(float scrollOffset) {
        return Math.max((int)((double)(scrollOffset * (float) (rows - maxRows)) + 0.5D), 0);
    }

    @Override
    public float getScrollForRowIndex(int row) {
        return Mth.clamp(row / (float) (rows - maxRows), 0.0F, 1.0F);
    }

    @Override
    public void scrollTo(int row) {
        if (row > this.rows - this.maxRows || row < 0) {
            return;
        }

        for (int i = 0; i < backpackSlots.size(); i++) {
            backpackSlots.get(i).setSlotIndex(i + row * 9);
        }
    }

    @Override
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
        if (this.slots.isEmpty()) {
            // First time
            setUpSlots();
        }

    }

    @Override
    public boolean canScroll() {
        return rows > maxRows;
    }

    public Inventory getPlayerInventory() {
        return (Inventory) playerInventory.getInv();
    }
}

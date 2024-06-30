package dev.willyelton.crystal_tools.common.inventory.container;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.inventory.container.slot.ReadOnlySlot;
import dev.willyelton.crystal_tools.common.inventory.container.slot.ScrollableSlot;
import dev.willyelton.crystal_tools.common.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.common.inventory.container.slot.CrystalSlotItemHandler;
import dev.willyelton.crystal_tools.common.levelable.CrystalBackpack;
import dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.network.PacketDistributor;
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
    private final IItemHandlerModifiable filterInventory;
    private final ItemStack stack;
    private final int rows;
    private final int filterRows;
    private boolean whitelist;
    private int maxRows;
    private final boolean canSort;
    private final NonNullList<ScrollableSlot> backpackSlots;

    // Client constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, ItemStack.OPTIONAL_STREAM_CODEC.decode(data));
    }

    // Server constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, ItemStack stack) {
        super(Registration.CRYSTAL_BACKPACK_CONTAINER.get(), containerId, playerInventory);
        this.inventory = CrystalBackpack.getInventory(stack);
        this.stack = stack;
        this.rows = inventory.getSlots() / SLOTS_PER_ROW;
        this.filterRows = stack.getOrDefault(DataComponents.FILTER_CAPACITY, 0);
        this.filterInventory = createFilterInventory(stack);
        this.backpackSlots = NonNullList.createWithCapacity(rows * SLOTS_PER_ROW);
        this.whitelist = stack.getOrDefault(DataComponents.WHITELIST, true);
        this.canSort = stack.getOrDefault(DataComponents.SORT_ENABLED, false);
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
        // TODO: Does it save automatically?
//        if (Objects.nonNull(filterInventory)) {
//            ItemContainerContents.fromItems(filterInventory.);
//            stack.getOrCreateTag().put("filter", filterInventory.serializeNBT());
//        }
//
//        stack.getOrCreateTag().putBoolean("whitelist", whitelist);
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

    public boolean canSort() {
        return canSort;
    }

    public boolean getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(boolean whitelist) {
        this.whitelist = whitelist;
    }

    public void sendUpdatePacket(BackpackScreenPayload.PickupType type) {
        PacketDistributor.sendToServer(new BackpackScreenPayload(type));
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

    private IItemHandlerModifiable createFilterInventory(ItemStack stack) {
        if (filterRows == 0) {
            return null;
        }

        ComponentItemHandler componentItemHandler = new ComponentItemHandler(stack, DataComponents.FILTER_INVENTORY.get(), filterRows * FILTER_SLOTS_PER_ROW);

        return componentItemHandler;

//        ItemStackHandler itemStackHandler = new ItemStackHandler(filterRows * FILTER_SLOTS_PER_ROW);
//        ItemStackHandler storedItems = new ItemStackHandler(0);
//        CompoundTag tag = stack.getOrCreateTagElement("filter");
//        if (!tag.isEmpty()) {
//            storedItems.deserializeNBT(tag);
//        }
//
//        InventoryUtils.copyTo(storedItems, itemStackHandler);
//
//        return itemStackHandler;
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

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while (!stack.isEmpty() && (reverseDirection ? i >= startIndex : i < endIndex)) {
                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameComponents(stack, itemstack)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int k = slot.getMaxStackSize(itemstack);
                    if (j <= k) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.set(itemstack);
                        flag = true;
                    } else if (itemstack.getCount() < k) {
                        stack.shrink(k - itemstack.getCount());
                        itemstack.setCount(k);
                        slot.set(itemstack);
                        flag = true;
                    }
                }

                if (reverseDirection) {
                    i--;
                } else {
                    i++;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while (reverseDirection ? i >= startIndex : i < endIndex) {
                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(stack)) {
                    int l = slot1.getMaxStackSize(stack);
                    slot1.setByPlayer(stack.split(Math.min(stack.getCount(), l)));
                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if (reverseDirection) {
                    i--;
                } else {
                    i++;
                }
            }
        }

        return flag;
    }
}

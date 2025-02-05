package dev.willyelton.crystal_tools.common.inventory.container;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.inventory.CompressionItemStackHandler;
import dev.willyelton.crystal_tools.common.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.common.inventory.container.slot.CrystalSlotItemHandler;
import dev.willyelton.crystal_tools.common.inventory.container.slot.ReadOnlySlot;
import dev.willyelton.crystal_tools.common.inventory.container.slot.ScrollableSlot;
import dev.willyelton.crystal_tools.common.inventory.container.slot.backpack.BackpackFilterSlot;
import dev.willyelton.crystal_tools.common.inventory.container.slot.backpack.CompressionInputSlot;
import dev.willyelton.crystal_tools.common.inventory.container.slot.backpack.CompressionOutputSlot;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.FilterContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.FilterMenuContents;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenType;
import dev.willyelton.crystal_tools.common.levelable.CrystalBackpack;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.Objects;

import static dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenType.COMPRESS;
import static dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenType.FILTER;
import static dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenType.NONE;

public class CrystalBackpackContainerMenu extends BaseContainerMenu implements ScrollableMenu, SubScreenContainerMenu, FilterContainerMenu {
    public static final int START_Y = 18;
    private static final int START_X = 8;
    private static final int SLOTS_PER_ROW = 9;
    public static final int FILTER_SLOTS_PER_ROW = 9;

    private final CrystalBackpackInventory inventory;
    private CompressionItemStackHandler compressionInventory;

    private final FilterMenuContents<CrystalBackpackContainerMenu> filterMenuContents;

    private final ItemStack stack;
    private final Player player;
    private final int slotIndex;
    private final int rows;
    private final int filterRows;
    private final boolean canSort;
    private final boolean canCompress;

    private int maxRows;
    private SubScreenType openSubScreen = NONE;

    private final NonNullList<ScrollableSlot> backpackSlots;
    private NonNullList<CompressionInputSlot> compressionInputSlots;
    private NonNullList<CompressionOutputSlot> compressionOutputSlots;

    // Client constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, ItemStack.OPTIONAL_STREAM_CODEC.decode(data), data.readInt());
    }

    // Server constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, ItemStack stack, int slotIndex) {
        super(Registration.CRYSTAL_BACKPACK_CONTAINER.get(), containerId, playerInventory, null);
        this.inventory = CrystalBackpack.getInventory(stack);
        this.stack = stack;
        this.player = playerInventory.player;
        this.slotIndex = slotIndex;
        this.rows = inventory.getSlots() / SLOTS_PER_ROW;
        this.filterRows = stack.getOrDefault(DataComponents.FILTER_CAPACITY, 0);
        this.filterMenuContents = new FilterMenuContents<>(this, filterRows * FILTER_SLOTS_PER_ROW, stack.getOrDefault(DataComponents.WHITELIST, true));
        this.canSort = stack.getOrDefault(DataComponents.SORT_ENABLED, false);
        this.canCompress = stack.getOrDefault(DataComponents.COMPRESSION_ENABLED, false);
        this.backpackSlots = NonNullList.createWithCapacity(rows * SLOTS_PER_ROW);
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

        this.addSlotBox(this.inventory, 0, START_X, START_Y, SLOTS_PER_ROW, SLOT_SIZE, rowsToDraw, SLOT_SIZE, backpackSlots, ScrollableSlot::new);
    }

    private void setUpFilterSlots() {
        if (Objects.nonNull(filterMenuContents.getInventory())) {
            int filterRows = Math.min(rows, getFilterRows());
            this.addSlotBox(filterMenuContents.getInventory(), 0, START_X, START_Y, FILTER_SLOTS_PER_ROW, SLOT_SIZE, filterRows, SLOT_SIZE, filterMenuContents.getFilterSlots(), BackpackFilterSlot::new);
        }
    }

    private void setUpCompressionSlots() {
        this.compressionInputSlots = NonNullList.createWithCapacity(getCompressionSlots() / 2);
        this.compressionOutputSlots = NonNullList.createWithCapacity(getCompressionSlots() / 2);
        this.compressionInventory = createCompressionInventory(stack);

        int compressionRows = getCompressionRows();

        for (int i = 0; i < compressionRows; i++) {
            for (int j = 0; j < 3; j++) {
                CompressionOutputSlot outputSlot = new CompressionOutputSlot(compressionInventory, i * 6 + j * 2 + 1, START_X + j * 54 + 32, START_Y + SLOT_SIZE * i);
                CompressionInputSlot inputSlot = new CompressionInputSlot(compressionInventory, i * 6 + j * 2, START_X + j * 54, START_Y + SLOT_SIZE * i, outputSlot, player.level());
                outputSlot.setInputSlot(inputSlot);

                outputSlot.setActive(false);
                inputSlot.setActive(false);

                addSlot(inputSlot);
                addSlot(outputSlot);

                compressionInputSlots.add(inputSlot);
                compressionOutputSlots.add(outputSlot);
            }
        }
    }

    @Override
    public void setUpSlots() {
        setUpPlayerSlots();
        setUpBackpackSlots();
        setUpFilterSlots();
        setUpCompressionSlots();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem() && !isFilterSlot(index)) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            // if you clicked in player's inventory
            if (index < 36) {
                switch (openSubScreen) {
                    case NONE -> {
                        if (!this.moveItemStackTo(itemstack1, 36, slots.size() - filterMenuContents.getSlotCount() - getCompressionSlots(), false)) {
                            // Need to check if there is room in inventory off-screen
                            // TODO: Works, little bit of a client desync though
                            itemstack1 = inventory.insertStack(itemstack);
                            if (!itemstack1.isEmpty()) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                    case COMPRESS -> {
                        return quickMoveToCompression(itemstack);
                    }
                    case FILTER -> {
                        return filterMenuContents.quickMove(itemstack);
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

    private ItemStack quickMoveToCompression(ItemStack stack) {
        if (!stack.isEmpty()) {
            for (CompressionInputSlot inputSlot : compressionInputSlots) {
                if (inputSlot.getItem().isEmpty()) {
                    inputSlot.onClicked(stack);
                    break;
                }
            }
        }

        return ItemStack.EMPTY;
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
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0) {
            Slot slot = getSlot(slotId);
            switch (slot) {
                case CompressionInputSlot compressionInputSlot -> compressionInputSlot.onClicked(getCarried());
                case CompressionOutputSlot compressionOutputSlot -> compressionOutputSlot.onClicked(getCarried());
                case BackpackFilterSlot filterSlot -> {
                    if (Objects.isNull(filterMenuContents.getInventory()) || clickType == ClickType.THROW || clickType == ClickType.CLONE) {
                        return;
                    }
                    filterSlot.onClicked(getCarried());
                }
                default -> super.clicked(slotId, button, clickType, player);
            }
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

    public boolean canCompress() {
        return canCompress;
    }

    @Override
    public void setWhitelist(boolean whitelist) {
        FilterContainerMenu.super.setWhitelist(whitelist);
        stack.set(DataComponents.WHITELIST, whitelist);
    }

    public void sort() {
        this.inventory.sort(CrystalToolsConfig.BACKPACK_SORT_TYPE.get());
    }

    public int getCompressionRows() {
        if (canScroll()) {
            return Math.min(maxRows, CrystalToolsConfig.MAX_COMPRESSION_SLOT_ROWS.get());
        } else {
            return Math.min(rows, CrystalToolsConfig.MAX_COMPRESSION_SLOT_ROWS.get());
        }
    }

    private int getCompressionSlots() {
        return getCompressionRows() * 6;
    }

    @Override
    public IItemHandlerModifiable getFilterInventory() {
        if (filterRows == 0) {
            return null;
        }

        return new ComponentItemHandler(stack, DataComponents.FILTER_INVENTORY.get(), filterRows * FILTER_SLOTS_PER_ROW);
    }

    private CompressionItemStackHandler createCompressionInventory(ItemStack stack) {
        return new CompressionItemStackHandler(stack, DataComponents.COMPRESSION_INVENTORY.get(), getCompressionSlots());
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

    @Override
    public void openSubScreen(SubScreenType subScreenType) {
        this.openSubScreen = subScreenType;
        this.backpackSlots.forEach(scrollableSlot -> scrollableSlot.setActive(false));

        if (subScreenType == FILTER) {
            this.filterMenuContents.toggleSlots(true);
        } else if (subScreenType == COMPRESS) {
            this.compressionInputSlots.forEach(compressionInputSlot -> compressionInputSlot.setActive(true));
            this.compressionOutputSlots.forEach(compressionOutputSlot -> compressionOutputSlot.setActive(true));
        }
    }

    @Override
    public void closeSubScreen() {
        this.openSubScreen = NONE;
        this.backpackSlots.forEach(scrollableSlot -> scrollableSlot.setActive(true));
        this.compressionInputSlots.forEach(compressionInputSlot -> compressionInputSlot.setActive(false));
        this.compressionOutputSlots.forEach(compressionOutputSlot -> compressionOutputSlot.setActive(false));
        this.filterMenuContents.toggleSlots(false);
    }

    // Closes and reopens container
    public void reopenBackpack() {
        if (player instanceof ServerPlayer serverPlayer) {
            player.closeContainer();
            CrystalBackpack backpack = (CrystalBackpack) stack.getItem();
            backpack.openBackpack(serverPlayer, stack, slotIndex);
        }
    }

    public void compress() {
        int compressionSlots = getCompressionSlots();

        for (int i = 0; i < compressionSlots; i+=2) {
            ItemStack inputItem = compressionInventory.getStackInSlot(i);
            ItemStack outputItem = compressionInventory.getStackInSlot(i + 1);
            if (inputItem.isEmpty() || outputItem.isEmpty()) continue;

            int count = 0;

            for (int j = 0; j < inventory.getSlots(); j++) {
                ItemStack stack = inventory.getStackInSlot(j);
                if (stack.is(inputItem.getItem())) {
                    count += stack.getCount();
                    // If any item has tags or anything this will delete them but that's probably fine
                    inventory.setStackInSlot(j, ItemStack.EMPTY);
                }
            }

            int requiredCount = compressionInventory.getMode(i).getCount();

            if (requiredCount == 0) continue;

            int outputCount = count / requiredCount;
            int inputCount = count % requiredCount;

            ItemStack inputStack = new ItemStack(inputItem.getItem(), inputCount);
            inventory.insertStack(inputStack);

            int outputStackCount = outputCount / outputItem.getMaxStackSize();
            int outputRemainder = outputCount % outputItem.getMaxStackSize();

            for (int j = 0; j < outputStackCount; j++) {
                inventory.insertStack(new ItemStack(outputItem.getItem(), outputItem.getMaxStackSize()));
            }

            inventory.insertStack(new ItemStack(outputItem.getItem(), outputRemainder));
        }
    }

    @Override
    public void matchContentsFilter(boolean isShiftDown) {
        this.filterMenuContents.matchContents(this.inventory, isShiftDown);
    }

    @Override
    public FilterMenuContents<?> getFilterMenuContents() {
        return filterMenuContents;
    }

    public ItemStack getBackpackStack() {
        return stack;
    }

    public Player getPlayer() {
        return player;
    }

    public int getSlotIndex() {
        return slotIndex;
    }
}

package dev.willyelton.crystal_tools.inventory.container;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.gui.ScrollableMenu;
import dev.willyelton.crystal_tools.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.inventory.container.slot.*;
import dev.willyelton.crystal_tools.levelable.CrystalBackpack;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.packet.BackpackScreenPacket;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
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

import static dev.willyelton.crystal_tools.inventory.container.CrystalBackpackContainerMenu.SubScreenType.*;

public class CrystalBackpackContainerMenu extends BaseContainerMenu implements ScrollableMenu {
    public static final int START_Y = 18;
    private static final int START_X = 8;
    private static final int SLOT_SIZE = 18;
    private static final int SLOTS_PER_ROW = 9;
    public static final int FILTER_SLOTS_PER_ROW = 9;
    private final CrystalBackpackInventory inventory;
    @Nullable
    private final ItemStackHandler filterInventory;
    private ItemStackHandler compressionInventory;
    private final ItemStack stack;
    private final int rows;
    private final int filterRows;
    private boolean whitelist;
    private int maxRows;
    private final boolean canSort;
    private final boolean canCompress;
    private final NonNullList<ScrollableSlot> backpackSlots;
    private NonNullList<CompressionInputSlot> compressionInputSlots;
    private NonNullList<CompressionOutputSlot> compressionOutputSlots;
    private final NonNullList<BackpackFilterSlot> filterSlots;
    private final Player player;
    private final int slotIndex;
    private SubScreenType openSubScreen = NONE;

    // Client constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, new CrystalBackpackInventory(data.readInt() * 9), ItemStack.EMPTY,
                data.readInt(), data.readBoolean(), data.readBoolean(), data.readBoolean(),
                data.readInt(), Minecraft.getInstance().player);
    }

    // Server constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, CrystalBackpackInventory backpackInventory,
                                        ItemStack stack, int filterRows, boolean whitelist, boolean canSort, boolean canCompress,
                                        int slotIndex, Player player) {
        super(Registration.CRYSTAL_BACKPACK_CONTAINER.get(), containerId, playerInventory);
        this.inventory = backpackInventory;
        this.stack = stack;
        this.rows = backpackInventory.getSlots() / SLOTS_PER_ROW;
        this.filterRows = filterRows;
        this.filterInventory = createFilterInventory(stack);
        this.backpackSlots = NonNullList.createWithCapacity(rows * SLOTS_PER_ROW);
        this.filterSlots = NonNullList.createWithCapacity(filterRows * FILTER_SLOTS_PER_ROW);
        this.whitelist = whitelist;
        this.canSort = canSort;
        this.canCompress = canCompress;
        this.slotIndex = slotIndex;
        this.player = player;
    }

    @Override
    protected void addSlot(IItemHandler handler, int index, int x, int y) {
        // TODO 1.21: Fix this in BaseContainerMenu
        if (handler == inventory) {
            ScrollableSlot slot = new ScrollableSlot(handler, index, x, y);
            backpackSlots.add(slot);
            addSlot(slot);
        } else if (handler == filterInventory) {
            BackpackFilterSlot slot = new BackpackFilterSlot(handler, index, x, y);
            filterSlots.add(slot);
            slot.setActive(false);
            addSlot(slot);
        } else {
            addSlot(new CrystalSlotItemHandler(handler, index, x, y));
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
            int filterRows = Math.min(rows, getFilterRows());
            this.addSlotBox(filterInventory, 0, START_X, START_Y, FILTER_SLOTS_PER_ROW, SLOT_SIZE, filterRows, SLOT_SIZE);
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
            // if you clicked in played inventory
            if (index < 36) {
                switch (openSubScreen) {
                    case NONE -> {
                        if (!this.moveItemStackTo(itemstack1, 36, slots.size() - getFilterSlots() - getCompressionSlots(), false)) {
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
                        return quickMoveFilter(itemstack);
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

    private ItemStack quickMoveFilter(ItemStack stack) {
        if (!stack.isEmpty()) {
            for (BackpackFilterSlot filterSlot : filterSlots) {
                if (filterSlot.getItem().isEmpty()) {
                    filterSlot.onClicked(stack);
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
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        saveFilters();
        saveCompressions();
        saveWhitelist();
    }

    private void saveFilters() {
        if (Objects.nonNull(filterInventory)) {
            stack.getOrCreateTag().put("filter", filterInventory.serializeNBT());
        }
    }

    private void saveCompressions() {
        stack.getOrCreateTag().put("compression", compressionInventory.serializeNBT());
    }

    private void saveWhitelist() {
        stack.getOrCreateTag().putBoolean("whitelist", whitelist);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        // TODO: Move onClicked to my slot class and then do all on clicked stuff in slots
        if (slotId >= 0) {
            Slot slot = getSlot(slotId);
            if (slot instanceof CompressionInputSlot compressionInputSlot) {
                compressionInputSlot.onClicked(getCarried());
                saveCompressions();
            } else if (slot instanceof CompressionOutputSlot compressionOutputSlot) {
                compressionOutputSlot.onClicked(getCarried());
                saveCompressions();
            } else if (slot instanceof BackpackFilterSlot filterSlot) {
                if (Objects.isNull(filterInventory) || clickType == ClickType.THROW || clickType == ClickType.CLONE) {
                    return;
                }

                filterSlot.onClicked(getCarried());
                saveFilters();
            } else {
                super.clicked(slotId, button, clickType, player);
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

    public boolean getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(boolean whitelist) {
        this.whitelist = whitelist;
        saveWhitelist();
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

    private ItemStackHandler createFilterInventory(ItemStack stack) {
        if (filterRows == 0) {
            return null;
        }

        ItemStackHandler itemStackHandler = new ItemStackHandler(filterRows * FILTER_SLOTS_PER_ROW);
        if (!stack.isEmpty()) {
            ItemStackHandler storedItems = new ItemStackHandler(0);
            CompoundTag tag = stack.getOrCreateTagElement("filter");
            if (!tag.isEmpty()) {
                storedItems.deserializeNBT(tag);
            }

            InventoryUtils.copyTo(storedItems, itemStackHandler);
        }

        return itemStackHandler;
    }

    private ItemStackHandler createCompressionInventory(ItemStack stack) {
        ItemStackHandler itemStackHandler = new ItemStackHandler(getCompressionSlots());
        if (!stack.isEmpty()) {
            ItemStackHandler storedItems = new ItemStackHandler(0);
            CompoundTag tag = stack.getOrCreateTagElement("compression");
            if (!tag.isEmpty()) {
                storedItems.deserializeNBT(tag);
            }

            InventoryUtils.copyTo(storedItems, itemStackHandler);
        }


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

    // TODO 1.21: Abstract
    public void openCompressionScreen() {
        this.openSubScreen = COMPRESS;
        this.backpackSlots.forEach(scrollableSlot -> scrollableSlot.setActive(false));
        this.compressionInputSlots.forEach(compressionInputSlot -> compressionInputSlot.setActive(true));
        this.compressionOutputSlots.forEach(compressionOutputSlot -> compressionOutputSlot.setActive(true));
        this.filterSlots.forEach(filterSlot -> filterSlot.setActive(false));
    }

    public void openFilterScreen() {
        this.openSubScreen = FILTER;
        this.backpackSlots.forEach(scrollableSlot -> scrollableSlot.setActive(false));
        this.compressionInputSlots.forEach(compressionInputSlot -> compressionInputSlot.setActive(false));
        this.compressionOutputSlots.forEach(compressionOutputSlot -> compressionOutputSlot.setActive(false));
        this.filterSlots.forEach(filterSlot -> filterSlot.setActive(true));
    }

    // Closes and reopens container
    public void reopenBackpack() {
        if (player instanceof ServerPlayer serverPlayer) {
            player.closeContainer();
            CrystalBackpack backpack = (CrystalBackpack) stack.getItem();
            backpack.openBackpack(serverPlayer, stack, slotIndex);
        }
    }

    public void closeSubScreen() {
        this.openSubScreen = NONE;
        this.backpackSlots.forEach(scrollableSlot -> scrollableSlot.setActive(true));
        this.compressionInputSlots.forEach(compressionInputSlot -> compressionInputSlot.setActive(false));
        this.compressionOutputSlots.forEach(compressionOutputSlot -> compressionOutputSlot.setActive(false));
        this.filterSlots.forEach(filterSlot -> filterSlot.setActive(false));
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

            // TODO: Eventually could be 4
            int outputCount = count / 9;
            int inputCount = count % 9;

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

    public ItemStack getBackpackStack() {
        return stack;
    }

    public Player getPlayer() {
        return player;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    protected enum SubScreenType {
        NONE, COMPRESS, FILTER
    }
}

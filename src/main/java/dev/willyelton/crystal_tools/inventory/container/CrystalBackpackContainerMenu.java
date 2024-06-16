package dev.willyelton.crystal_tools.inventory.container;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.gui.ScrollableMenu;
import dev.willyelton.crystal_tools.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.inventory.container.slot.CompressionInputSlot;
import dev.willyelton.crystal_tools.inventory.container.slot.CompressionOutputSlot;
import dev.willyelton.crystal_tools.inventory.container.slot.CrystalSlotItemHandler;
import dev.willyelton.crystal_tools.inventory.container.slot.ReadOnlySlot;
import dev.willyelton.crystal_tools.inventory.container.slot.ScrollableSlot;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.packet.BackpackScreenPacket;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import net.minecraft.client.Minecraft;
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
    private final ItemStackHandler compressionInventory;
    private final ItemStack stack;
    private final int rows;
    private final int filterRows;
    private boolean whitelist;
    private int maxRows;
    private boolean canSort;
    private final NonNullList<ScrollableSlot> backpackSlots;
    private final Player player;
    private boolean hasSubScreenOpen = false;

    // Client constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, new CrystalBackpackInventory(data.readInt() * 9), ItemStack.EMPTY,
                // TODO: this ok?
                data.readInt(), data.readBoolean(), data.readBoolean(), Minecraft.getInstance().player);
    }

    // Server constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, CrystalBackpackInventory backpackInventory,
                                        ItemStack stack, int filterRows, boolean whitelist, boolean canSort, Player player) {
        super(Registration.CRYSTAL_BACKPACK_CONTAINER.get(), containerId, playerInventory);
        this.inventory = backpackInventory;
        this.stack = stack;
        this.rows = backpackInventory.getSlots() / SLOTS_PER_ROW;
        this.filterRows = filterRows;
        this.filterInventory = createFilterInventory(stack);
        this.compressionInventory = createCompressionInventory(stack);
        this.backpackSlots = NonNullList.createWithCapacity(rows * SLOTS_PER_ROW);
        this.whitelist = whitelist;
        this.canSort = canSort;
        this.player = player;
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

    private void setUpCompressionSlots() {
        // TODO: Going to want to be able to add a lot of these (in columns probably)
        // Infinite upgrade. Or just go by rows in backpack up to 6. Too annoying to change screen size
        int compressionRows = Math.max(this.getRows(), CrystalToolsConfig.MAX_COMPRESSION_SLOT_ROWS.get());

        for (int i = 0; i < compressionRows; i++) {
            for (int j = 0; j < 3; j++) {
                CompressionOutputSlot outputSlot = new CompressionOutputSlot(compressionInventory, compressionRows * 3 + i * 3 + j, START_X + j * 54 + 32, START_Y + SLOT_SIZE * i);
                CompressionInputSlot inputSlot = new CompressionInputSlot(compressionInventory, i * 3 + j, START_X + j * 54, START_Y + SLOT_SIZE * i, outputSlot, player.level());
                outputSlot.setInputSlot(inputSlot);

                outputSlot.setActive(false);
                inputSlot.setActive(false);

                addSlot(inputSlot);
                addSlot(outputSlot);
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
        // TODO: Going to need to probably account for compression slots somewhere
        // TODO: For some reason shift click goes into slot even though its disabled (maybe not always??? server desync?)
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        // TODO: We do want some shift click behavior for subscreens eventually
        if (slot.hasItem() && !isFilterSlot(index) && !hasSubScreenOpen) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            // if you clicked in played inventory
            if (index < 36) {
               if (!this.moveItemStackTo(itemstack1, 36, slots.size() - getFilterSlots() - getCompressionSlots(), false)) {
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

        stack.getOrCreateTag().put("compression", compressionInventory.serializeNBT());

        stack.getOrCreateTag().putBoolean("whitelist", whitelist);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        // TODO: Move onClicked to my slot class and then do all on clicked stuff in slots
        if (slotId >= 0) {
            Slot slot = getSlot(slotId);
            if (slot instanceof CompressionInputSlot compressionInputSlot) {
                compressionInputSlot.onClicked(getCarried());
            } else if (slot instanceof CompressionOutputSlot compressionOutputSlot) {
                compressionOutputSlot.onClicked(getCarried());
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

    private int getCompressionSlots() {
        return Math.max(this.getRows(), CrystalToolsConfig.MAX_COMPRESSION_SLOT_ROWS.get()) * 6;
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

    private ItemStackHandler createCompressionInventory(ItemStack stack) {
        ItemStackHandler itemStackHandler = new ItemStackHandler(getCompressionSlots());
        ItemStackHandler storedItems = new ItemStackHandler(0);
        CompoundTag tag = stack.getOrCreateTagElement("compression");
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

    // TODO: Abstract and maybe screen enum
    public void openCompressionScreen() {
        // TODO: Maybe we just override isActive and have an enum or something for active screen
        this.hasSubScreenOpen = true;
        slots.forEach(slot -> {
            if (slot instanceof ScrollableSlot scrollableSlot) {
                scrollableSlot.setActive(false);
            } else if (slot instanceof CompressionInputSlot compressionInputSlot) {
                compressionInputSlot.setActive(true);
            } else if (slot instanceof CompressionOutputSlot compressionOutputSlot) {
                compressionOutputSlot.setActive(true);
            }
        });
    }

    public void closeSubScreen() {
        this.hasSubScreenOpen = false;
        slots.forEach(slot -> {
            if (slot instanceof ScrollableSlot scrollableSlot) {
                scrollableSlot.setActive(true);
            } else if (slot instanceof CompressionInputSlot compressionInputSlot) {
                compressionInputSlot.setActive(false);
            } else if (slot instanceof CompressionOutputSlot compressionOutputSlot) {
                compressionOutputSlot.setActive(false);
            }
        });
    }

    public void compress() {
        int compressionSlots = getCompressionSlots() / 2;

        for (int i = 0; i < compressionSlots; i++) {
            ItemStack inputItem = compressionInventory.getStackInSlot(i);
            ItemStack outputItem = compressionInventory.getStackInSlot(i + compressionSlots);
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
}

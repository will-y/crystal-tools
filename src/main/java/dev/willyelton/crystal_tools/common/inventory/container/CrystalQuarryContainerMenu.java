package dev.willyelton.crystal_tools.common.inventory.container;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.inventory.container.slot.CrystalSlotItemHandler;
import dev.willyelton.crystal_tools.common.inventory.container.slot.backpack.BackpackFilterSlot;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.FilterContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.FilterMenuContents;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenType;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalQuarryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class CrystalQuarryContainerMenu extends EnergyLevelableContainerMenu implements SubScreenContainerMenu, FilterContainerMenu {
    private final CrystalQuarryBlockEntity blockEntity;
    private FilterMenuContents<CrystalQuarryContainerMenu> filterMenuContents;
    // TODO: Why did I need both of these?
    private final NonNullList<CrystalSlotItemHandler> quarryInventorySlots;
    private final NonNullList<CrystalSlotItemHandler> filterInventorySlots;
    private final NonNullList<CrystalSlotItemHandler> quarrySlots;
    private final int filterRows;

    public CrystalQuarryContainerMenu(int containerId, Level level, BlockPos pos, int filterRows, Inventory playerInventory, ContainerData data) {
        super(Registration.CRYSTAL_QUARRY_CONTAINER.get(), containerId, playerInventory, data);
        blockEntity = (CrystalQuarryBlockEntity) level.getBlockEntity(pos);
        quarryInventorySlots = NonNullList.createWithCapacity(36);
        filterInventorySlots = NonNullList.createWithCapacity(36);
        quarrySlots = NonNullList.createWithCapacity(27);
        this.filterRows = filterRows;

        if (blockEntity == null) return;

        filterMenuContents = new FilterMenuContents<>(this, filterRows, true);
        this.addSlotBox(blockEntity.getItemHandler(), 0, 8, 59, 9, SLOT_SIZE, 3, SLOT_SIZE, quarrySlots, CrystalSlotItemHandler::new);
        this.addSlotBox(blockEntity.getFilterItemHandler(), 0, 8, 18, 9, SLOT_SIZE, filterRows, SLOT_SIZE, filterMenuContents.getFilterSlots(), BackpackFilterSlot::new);
        filterMenuContents.toggleSlots(false);
        this.layoutPlayerInventorySlots(8, 145, quarryInventorySlots, CrystalSlotItemHandler::new);
        this.layoutPlayerInventorySlots(8, 86, filterInventorySlots, CrystalSlotItemHandler::new);
        filterInventorySlots.forEach(s -> s.setActive(false));
    }

    @Override
    protected void addSlot(IItemHandler handler, int index, int x, int y) {
        if (handler == filterMenuContents.getInventory()) {
            BackpackFilterSlot slot = new BackpackFilterSlot(handler, index, x, y);
            filterMenuContents.addSlot(slot);
            addSlot(slot);
        } else {
            super.addSlot(handler, index, x, y);
        }
    }

    // TODO: Default implementation
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();
            // Fuel Slot
            if (index == 0) {
                if (!this.moveItemStackTo(slotStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    // TODO: Default implementation (generic block entity type in BaseContainerMenu)
    @Override
    public boolean stillValid(Player player) {
        if (level != null && level.getBlockEntity(blockEntity.getBlockPos()) != blockEntity) {
            return false;
        } else {
            return player.distanceToSqr((double) blockEntity.getBlockPos().getX() + 0.5D, (double) blockEntity.getBlockPos().getY() + 0.5D, (double) blockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0) {
            Slot slot = getSlot(slotId);

            if (slot instanceof BackpackFilterSlot filterSlot) {
                if (filterMenuContents.getInventory() == null || clickType == ClickType.THROW || clickType == ClickType.CLONE) {
                    return;
                }
                filterSlot.onClicked(getCarried());
                blockEntity.setChanged();
                return;
            }
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public String getBlockType() {
        return "quarry";
    }

    // TODO: Probably not needed if i add block entity to base container menu
    @Override
    public CrystalQuarryBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public float getCurrentEnergy() {
        return this.data.get(3);
    }

    @Override
    public float getMaxEnergy() {
        return this.data.get(4);
    }

    public BlockPos getMiningAt() {
        return new BlockPos(this.data.get(5), this.data.get(6), this.data.get(7));
    }

    public BlockPos getBlockPos() {
        return blockEntity.getBlockPos();
    }

    @Override
    public boolean getWhitelist() {
        return this.data.get(8) == 1;
    }

    @Override
    public void setWhitelist(boolean whitelist) {
        this.data.set(8, whitelist ? 1 : 0);
    }

    @Override
    public int getFilterRows() {
        // Can't use container data here because it isn't synced when init is called on the screen
        return this.filterRows;
    }

    @Override
    public IItemHandlerModifiable getFilterInventory() {
        return blockEntity.getFilterItemHandler();
    }

    @Override
    public FilterMenuContents<?> getFilterMenuContents() {
        return filterMenuContents;
    }

    @Override
    public void closeSubScreen() {
        filterMenuContents.toggleSlots(false);
        quarrySlots.forEach(slot -> slot.setActive(true));
        quarryInventorySlots.forEach(slot -> slot.setActive(true));
        filterInventorySlots.forEach(slot -> slot.setActive(false));
    }

    @Override
    public void openSubScreen(SubScreenType subScreenType) {
        if (subScreenType == SubScreenType.FILTER) {
            filterMenuContents.toggleSlots(true);
            filterInventorySlots.forEach(slot -> slot.setActive(true));
        }

        quarrySlots.forEach(slot -> slot.setActive(false));
        quarryInventorySlots.forEach(slot -> slot.setActive(false));
    }

    public boolean getSetting(int setting) {
        return this.data.get(9 + setting) == 1;
    }

    public void setSetting(int setting, boolean value) {
        this.data.set(9 + setting, value ? 1 : 0);
    }

    public int getEnergyCost() {
        return this.data.get(13);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        boolean newValue = this.data.get(9 + id) == 0;
        this.data.set(9 + id, newValue ? 1 : 0);

        if (id == 1 && newValue) {
            this.data.set(11, 0);
        } else if (id == 2 && newValue) {
            this.data.set(10, 0);
        }

        return true;
    }
}

package dev.willyelton.crystal_tools.common.inventory.container.subscreen;

import dev.willyelton.crystal_tools.common.inventory.container.BaseContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.slot.backpack.BackpackFilterSlot;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import static dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu.FILTER_SLOTS_PER_ROW;

/**
 * Holds all logic and data for the filter menu used for
 * the backpack and quarry
 */
public class FilterMenuContents<T extends BaseContainerMenu & FilterContainerMenu> {
    private final T menu;
    @Nullable
    private final IItemHandlerModifiable filterInventory;
    private final NonNullList<BackpackFilterSlot> filterSlots;

    private boolean whitelist;

    public FilterMenuContents(T menu, int filterRows, boolean whiteList) {
        this.menu = menu;
        filterInventory = filterRows > 0 ? menu.getFilterInventory() : null;
        filterSlots = NonNullList.createWithCapacity(filterRows * FILTER_SLOTS_PER_ROW);
        this.whitelist = whiteList;
    }

    public boolean getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(boolean whitelist) {
        this.whitelist = whitelist;
    }

    public int getSlotCount() {
        if (filterInventory != null) {
            return filterInventory.getSlots();
        }

        return 0;
    }

    public NonNullList<BackpackFilterSlot> getFilterSlots() {
        return filterSlots;
    }

    public IItemHandlerModifiable getInventory() {
        return filterInventory;
    }

    public void addSlot(BackpackFilterSlot slot) {
        filterSlots.add(slot);
    }

    public ItemStack quickMove(ItemStack stack) {
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

    public void toggleSlots(boolean enabled) {
        filterSlots.forEach(filterSlot -> filterSlot.setActive(enabled));
    }

    public void clear() {
        if (filterInventory != null) {
            InventoryUtils.clear(filterInventory);
        }
    }

    public void matchContents(IItemHandler inventory, boolean shiftDown) {
        if (filterInventory == null) return;

        if (shiftDown) {
            InventoryUtils.clear(filterInventory);
        }

        int filterIndex = 0;
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (filterIndex >= getSlotCount()) break;
            ItemStack stack = inventory.getStackInSlot(i).copy();
            stack.setCount(1);
            // Would like to use a set but stack doesn't implement equals and hashcode
            if (!InventoryUtils.contains(filterInventory, stack)) {
                while (!filterInventory.getStackInSlot(filterIndex).isEmpty()) {
                    filterIndex++;
                    if (filterIndex >= getSlotCount()) {
                        return;
                    }
                }
                filterInventory.setStackInSlot(filterIndex++, stack);
            }
        }
    }
}

package dev.willyelton.crystal_tools.common.inventory.container.subscreen;

import dev.willyelton.crystal_tools.common.inventory.container.BaseContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.slot.backpack.BackpackFilterSlot;
import dev.willyelton.crystal_tools.utils.TransferUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import static dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu.FILTER_SLOTS_PER_ROW;

/**
 * Holds all logic and data for the filter menu used for
 * the backpack and quarry
 */
public class FilterMenuContents<T extends BaseContainerMenu & FilterContainerMenu> {
    private final T menu;
    @Nullable
    private final ResourceHandler<ItemResource> filterInventory;
    private final NonNullList<BackpackFilterSlot> filterSlots;

    private boolean whitelist;

    public FilterMenuContents(T menu, int filterSlots, boolean whiteList) {
        this.menu = menu;
        filterInventory = filterSlots > 0 ? menu.getFilterInventory() : null;
        this.filterSlots = NonNullList.createWithCapacity(filterSlots * FILTER_SLOTS_PER_ROW);
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
            return filterInventory.size();
        }

        return 0;
    }

    public NonNullList<BackpackFilterSlot> getFilterSlots() {
        return filterSlots;
    }

    public ResourceHandler<ItemResource> getInventory() {
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
            TransferUtils.clear(filterInventory);
        }
    }

    public void matchContents(ResourceHandler<ItemResource> handler, boolean shiftDown) {
        if (filterInventory == null) return;

        if (shiftDown) {
            TransferUtils.clear(filterInventory);
        }

        int filterIndex = 0;
        for (int i = 0; i < handler.size(); i++) {
            if (filterIndex >= getSlotCount()) break;
            ItemResource resource = handler.getResource(i);
            if (!resource.isEmpty()) {
                if (!ResourceHandlerUtil.contains(filterInventory, resource)) {
                    try (Transaction tx = Transaction.open(null)) {
                        filterInventory.insert(resource, 1, tx);
                        tx.commit();
                    }
                }
            }
        }
    }
}

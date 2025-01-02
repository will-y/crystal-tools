package dev.willyelton.crystal_tools.common.inventory.container.subscreen;

import net.neoforged.neoforge.items.IItemHandlerModifiable;

/**
 *
 */
public interface FilterContainerMenu {
    default boolean getWhitelist() {
        return getFilterMenuContents().getWhitelist();
    }

    default void setWhitelist(boolean whitelist) {
        getFilterMenuContents().setWhitelist(whitelist);
    }

    int getFilterRows();

    IItemHandlerModifiable getFilterInventory();

    default void clearFilters() {
        getFilterMenuContents().clear();
    }

    FilterMenuContents<?> getFilterMenuContents();

    default void matchContentsFilter(boolean isShiftDown) {}
}

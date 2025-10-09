package dev.willyelton.crystal_tools.common.inventory.container.subscreen;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

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

    ResourceHandler<ItemResource> getFilterInventory();

    default void clearFilters() {
        getFilterMenuContents().clear();
    }

    FilterMenuContents<?> getFilterMenuContents();

    default void matchContentsFilter(boolean isShiftDown) {}
}

package dev.willyelton.crystal_tools.common.inventory;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemAccessItemHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class ItemAccessItemHandlerModifier extends ItemAccessItemHandler {
    public ItemAccessItemHandlerModifier(ItemAccess itemAccess, DataComponentType<ItemContainerContents> component, int size) {
        super(itemAccess, component, size);
    }

    public IndexModifier<ItemResource> indexModifier(ItemStack stack) {
        return (index, resource, amount) -> {
            ItemContainerContents result = update(ItemResource.of(stack), index, resource, amount).get(this.component);

            stack.set(this.component, result);
        };
    }
}

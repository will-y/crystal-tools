package dev.willyelton.crystal_tools.inventory.container.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CompressionInputSlot extends SlotItemHandler {
    private final CompressionOutputSlot outputSlot;

    public CompressionInputSlot(IItemHandler itemHandler, int slot, int x, int y, CompressionOutputSlot outputSlot) {
        super(itemHandler, slot, x, y);
        this.outputSlot = outputSlot;
    }

    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    public void onClicked(ItemStack stack) {
        // TODO: lookup recipes
        // TODO: Just need to construct a CraftingContainer and then call the same method as furnace
        // Need to get level from somewhere, menu surely has it
        Level level;

//        level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING)
        ItemStack stackCopy = stack.copy();
        stackCopy.setCount(1);
        set(stackCopy);
        setChanged();
    }
}

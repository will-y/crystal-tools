package dev.willyelton.crystal_tools.common.inventory;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.datamap.GeneratorFuelData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import static dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalGeneratorBlockEntity.getFuelData;

public class PortableGeneratorInventory extends ListComponentItemHandler {
    // TODO: Move to BE maybe?
    public static final int MAX_SIZE = 27;

    private final ItemStack generatorStack;
    // Has to be a better way?
    private @Nullable Level level;

    public PortableGeneratorInventory(int size) {
        super(ItemStack.EMPTY.copy(), DataComponents.BACKPACK_INVENTORY.get(), Math.min(size, MAX_SIZE));
        this.generatorStack = ItemStack.EMPTY.copy();
    }

    public PortableGeneratorInventory(ItemStack stack) {
        super(stack, DataComponents.BACKPACK_INVENTORY.get(), Math.min(stack.getOrDefault(DataComponents.PORTABLE_GENERATOR_SLOTS, 0) + 1, MAX_SIZE));

        this.generatorStack = stack;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        boolean canFit = !stack.is(Registration.PORTABLE_GENERATOR) && super.isItemValid(slot, stack);
        if (level == null) return canFit;

        // TODO: Other types of generators
        GeneratorFuelData newFuelData = getFuelData(stack, generatorStack.getOrDefault(DataComponents.FOOD_GENERATOR, false),
                generatorStack.getOrDefault(DataComponents.METAL_GENERATOR, false), generatorStack.getOrDefault(DataComponents.GEM_GENERATOR, false));
        return canFit && (newFuelData != null || stack.getBurnTime(RecipeType.SMELTING) > 0);
    }

    public ItemStack insertStack(ItemStack stack) {
        return ItemHandlerHelper.insertItem(this, stack, false);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public ItemStack nextItem() {
        for (int i = 0; i < this.getSlots(); i++) {
            ItemStack stack = this.getStackInSlot(i);

            if (!stack.isEmpty()) {
                ItemStack toReturn = stack.copyWithCount(1);
                ItemStack leftOver = stack.copy();
                leftOver.shrink(1);
                this.setStackInSlot(i, leftOver);

                return toReturn;
            }
        }

        return ItemStack.EMPTY.copy();
    }
}

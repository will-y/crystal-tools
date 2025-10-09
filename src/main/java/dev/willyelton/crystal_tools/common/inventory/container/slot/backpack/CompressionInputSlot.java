package dev.willyelton.crystal_tools.common.inventory.container.slot.backpack;

import dev.willyelton.crystal_tools.common.inventory.CompressionItemStackHandler;
import dev.willyelton.crystal_tools.common.inventory.container.slot.CrystalSlotItemHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.willyelton.crystal_tools.common.inventory.CompressionItemStackHandler.CompressionMode.THREE_BY_THREE;
import static dev.willyelton.crystal_tools.common.inventory.CompressionItemStackHandler.CompressionMode.TWO_BY_TWO;

// TODO: This probably works, but I should probably do something better
// I think I don't need to store recipes anymore, just try to assemble the inputs and store the output?
public class CompressionInputSlot extends CrystalSlotItemHandler {
    private final CompressionOutputSlot outputSlot;
    private final Level level;
    private final CompressionItemStackHandler compressionHandler;

    public CompressionInputSlot(CompressionItemStackHandler itemHandler, IndexModifier<ItemResource> indexModifier, int index, int x, int y, CompressionOutputSlot outputSlot, Level level) {
        super(itemHandler, indexModifier, index, x, y);
        this.outputSlot = outputSlot;
        this.level = level;
        this.compressionHandler = itemHandler;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }

    public void onClicked(ItemStack stack) {
        Optional<RecipeHolder<CraftingRecipe>> threeOptional = getRecipe(stack, 3);
        Optional<RecipeHolder<CraftingRecipe>> twoOptional = getRecipe(stack, 2);
        CompressionItemStackHandler.CompressionMode compressionMode = compressionHandler.getMode(getIndex());

        if (stack.is(this.getItem().getItem())) {
            if (compressionMode == THREE_BY_THREE && twoOptional.isPresent()) {
                setSlots(stack, twoOptional.get(), 2);
                this.compressionHandler.setMode(TWO_BY_TWO, getIndex());
                return;
            } else if (compressionMode == TWO_BY_TWO && threeOptional.isPresent()) {
                setSlots(stack, threeOptional.get(), 3);
                this.compressionHandler.setMode(THREE_BY_THREE, getIndex());
                return;
            }
        }

        // TODO: Check for uncrafting and show warning if not
        if (threeOptional.isPresent()) {
            setSlots(stack, threeOptional.get(), 3);
            this.compressionHandler.setMode(THREE_BY_THREE, getIndex());
        } else {
            if (twoOptional.isPresent()) {
                setSlots(stack, twoOptional.get(), 2);

                this.compressionHandler.setMode(TWO_BY_TWO, getIndex());
            } else {
                set(ItemStack.EMPTY);
                setChanged();

                outputSlot.set(ItemStack.EMPTY);
                outputSlot.setChanged();
            }
        }
    }

    private CraftingInput getCraftingInput(ItemStack stack, int size) {
        List<ItemStack> inputStacks = new ArrayList<>();
        for (int i = 0; i < size * size; i++) {
            inputStacks.add(stack.copy());
        }

        return CraftingInput.of(size, size, inputStacks);
    }

    private Optional<RecipeHolder<CraftingRecipe>> getRecipe(ItemStack stack, int size) {
        if (stack.isEmpty()) return Optional.empty();
        CraftingInput craftingInput = getCraftingInput(stack, size);

        if (level.recipeAccess() instanceof RecipeManager recipeManager) {
            return recipeManager.getRecipeFor(RecipeType.CRAFTING, craftingInput, level);
        }

        // TODO: see if this is ok to return on the client?
        return Optional.empty();
    }

    private void setSlots(ItemStack stack, RecipeHolder<CraftingRecipe> recipe, int size) {
        CraftingRecipe actualRecipe = recipe.value();
        ItemStack inputStack = stack.copy();
        inputStack.setCount(1);
        set(inputStack);
        setChanged();

        ItemStack outputStack = actualRecipe.assemble(getCraftingInput(stack, size), level.registryAccess()).copy();
        outputSlot.set(outputStack);
        outputSlot.setChanged();
    }
}
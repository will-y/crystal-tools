package dev.willyelton.crystal_tools.inventory.container.slot;

import dev.willyelton.crystal_tools.inventory.CompressionItemStackHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.willyelton.crystal_tools.inventory.CompressionItemStackHandler.CompressionMode.*;

public class CompressionInputSlot extends CrystalSlotItemHandler {
    private final CompressionOutputSlot outputSlot;
    private final Level level;
    private final CompressionItemStackHandler compressionHandler;

    public CompressionInputSlot(CompressionItemStackHandler itemHandler, int index, int x, int y, CompressionOutputSlot outputSlot, Level level) {
        super(itemHandler, index, x, y);
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
        Optional<CraftingRecipe> threeOptional = getRecipe(stack, 3);
        Optional<CraftingRecipe> twoOptional = getRecipe(stack, 2);
        CompressionItemStackHandler.CompressionMode compressionMode = compressionHandler.getMode(getIndex());

        if (stack.is(this.getItem().getItem())) {
            if (compressionMode == THREE_BY_THREE && twoOptional.isPresent()) {
                setSlots(stack, twoOptional.get());
                this.compressionHandler.setMode(TWO_BY_TWO, getIndex());
                return;
            } else if (compressionMode == TWO_BY_TWO && threeOptional.isPresent()) {
                setSlots(stack, threeOptional.get());
                this.compressionHandler.setMode(THREE_BY_THREE, getIndex());
                return;
            }
        }

        // TODO: Check for uncrafting and show warning if not
        if (threeOptional.isPresent()) {
            setSlots(stack, threeOptional.get());
            this.compressionHandler.setMode(THREE_BY_THREE, getIndex());
        } else {
            if (twoOptional.isPresent()) {
                setSlots(stack, twoOptional.get());

                this.compressionHandler.setMode(TWO_BY_TWO, getIndex());
            } else {
                set(ItemStack.EMPTY);
                setChanged();

                outputSlot.set(ItemStack.EMPTY);
                outputSlot.setChanged();
            }
        }
    }

    private Optional<CraftingRecipe> getRecipe(ItemStack stack, int size) {
        if (stack.isEmpty()) return Optional.empty();
        // 1.21: CraftingInput
        CraftingContainer craftingContainer = new CraftingContainer() {
            @Override
            public int getWidth() {
                return size;
            }

            @Override
            public int getHeight() {
                return size;
            }

            @Override
            public List<ItemStack> getItems() {
                ItemStack copy = stack.copy();
                copy.setCount(1);
                List<ItemStack> items = new ArrayList<>();
                for (int i = 0; i < getContainerSize(); i++) {
                    items.add(copy.copy());
                }

                return items;
            }

            @Override
            public int getContainerSize() {
                return size * size;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public ItemStack getItem(int pSlot) {
                return getItems().get(pSlot);
            }

            @Override
            public ItemStack removeItem(int pSlot, int pAmount) {
                return null;
            }

            @Override
            public ItemStack removeItemNoUpdate(int pSlot) {
                return null;
            }

            @Override
            public void setItem(int pSlot, ItemStack pStack) {

            }

            @Override
            public void setChanged() {

            }

            @Override
            public boolean stillValid(Player pPlayer) {
                return true;
            }

            @Override
            public void clearContent() {

            }

            @Override
            public void fillStackedContents(StackedContents pContents) {

            }
        };

        return level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingContainer, level);
    }

    private void setSlots(ItemStack stack, CraftingRecipe recipe) {
        ItemStack inputStack = stack.copy();
        inputStack.setCount(1);
        set(inputStack);
        setChanged();

        ItemStack outputStack = recipe.getResultItem(level.registryAccess()).copy();
        outputSlot.set(outputStack);
        outputSlot.setChanged();
    }
}

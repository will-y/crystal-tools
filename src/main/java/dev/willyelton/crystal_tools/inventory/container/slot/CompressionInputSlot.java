package dev.willyelton.crystal_tools.inventory.container.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompressionInputSlot extends CrystalSlotItemHandler {
    private final CompressionOutputSlot outputSlot;
    private final Level level;

    public CompressionInputSlot(IItemHandler itemHandler, int index, int x, int y, CompressionOutputSlot outputSlot, Level level) {
        super(itemHandler, index, x, y);
        this.outputSlot = outputSlot;
        this.level = level;
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
        // 1.21: CraftingInput
        CraftingContainer craftingContainer = new CraftingContainer() {
            @Override
            public int getWidth() {
                return 3;
            }

            @Override
            public int getHeight() {
                return 3;
            }

            @Override
            public List<ItemStack> getItems() {
                // TODO: 1.21 allow 2x2 compression
                ItemStack copy = stack.copy();
                copy.setCount(1);
                List<ItemStack> items = new ArrayList<>();
                for (int i = 0; i < 9; i++) {
                    items.add(copy.copy());
                }

                return items;
            }

            @Override
            public int getContainerSize() {
                return 9;
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

        Optional<CraftingRecipe> optional = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingContainer, level);

        // TODO: Check for uncrafting and show warning if not
        if (optional.isPresent()) {
            ItemStack inputStack = stack.copy();
            inputStack.setCount(1);
            set(inputStack);
            setChanged();

            ItemStack outputStack = optional.get().getResultItem(level.registryAccess()).copy();
            outputSlot.set(outputStack);
            outputSlot.setChanged();
        } else {
            set(ItemStack.EMPTY);
            setChanged();

            outputSlot.set(ItemStack.EMPTY);
            outputSlot.setChanged();
        }
    }
}

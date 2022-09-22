package dev.willyelton.crystal_tools.levelable.block.entity;

import dev.willyelton.crystal_tools.levelable.block.ModBlocks;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.windows.INPUT;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CrystalFurnaceBlockEntity extends BlockEntity implements WorldlyContainer {
    private final int[] INPUT_SLOTS = new int[] {0, 1, 2, 3, 4};
    private final int[] OUTPUT_SLOTS = new int[] {5, 6, 7, 8, 9};
    private final int[] FUEL_SLOTS = new int[] {10, 11, 12, 13, 14};

    private final int SIZE = 15;

    private final List<ItemStack> items;

    LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] invHandlers = SidedInvWrapper.create(this, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);

    public CrystalFurnaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.CRYSTAL_FURNACE_BLOCK_ENTITY.get(), pPos, pBlockState);
        items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!this.isRemoved() && side != null && cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.DOWN)
                return invHandlers[0].cast();
            else if (side == Direction.UP)
                return invHandlers[1].cast();
            else if (side == Direction.NORTH)
                return invHandlers[2].cast();
            else if (side == Direction.SOUTH)
                return invHandlers[3].cast();
            else if (side == Direction.WEST)
                return invHandlers[4].cast();
            else
                return invHandlers[5].cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction face) {
        switch (face) {
            case DOWN -> {
                return OUTPUT_SLOTS;
            }
            case UP -> {
                return INPUT_SLOTS;
            }
            case NORTH, SOUTH, WEST, EAST -> {
                return FUEL_SLOTS;
            }
        }

        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, @NotNull ItemStack pItemStack, @org.jetbrains.annotations.Nullable Direction pDirection) {
        return false;
    }

    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        if (ArrayUtils.arrayContains(INPUT_SLOTS, index)) {
            return true;
        } else if (ArrayUtils.arrayContains(OUTPUT_SLOTS, index)) {
            return false;
        } else if (ArrayUtils.arrayContains(FUEL_SLOTS, index)) {
            return net.minecraftforge.common.ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
        }

        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return pDirection != Direction.DOWN || pIndex != 1;
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public @NotNull ItemStack getItem(int pSlot) {
        return this.items.get(pSlot);
    }

    @Override
    public @NotNull ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(items, pSlot, pAmount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(items, pSlot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.items.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return pPlayer.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }
}


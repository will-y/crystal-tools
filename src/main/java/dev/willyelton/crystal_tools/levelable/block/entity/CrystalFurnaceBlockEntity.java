package dev.willyelton.crystal_tools.levelable.block.entity;

import dev.willyelton.crystal_tools.levelable.block.ModBlocks;
import dev.willyelton.crystal_tools.levelable.block.container.CrystalFurnaceContainer;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CrystalFurnaceBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    private final int[] INPUT_SLOTS = new int[] {0, 1, 2, 3, 4};
    private final int[] OUTPUT_SLOTS = new int[] {5, 6, 7, 8, 9};
    private final int[] FUEL_SLOTS = new int[] {10, 11, 12};

    public static final int SIZE = 13;

    private NonNullList<ItemStack> items;

    LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] invHandlers = SidedInvWrapper.create(this, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);

    private final RecipeType<SmeltingRecipe> recipeType = RecipeType.SMELTING;

    // Furnace related fields
    private int litTime;
    private int litDuration;
    private int[] cookingProgress;
    private int cookingTotalTime;

    // Crystal furnace fields
    private int numSlots;
    private int numFuelSlots = 3;

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
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
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

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.crystal_tools.crystal_furnace");
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new CrystalFurnaceContainer(pContainerId, pPlayer.getLevel(), this.getBlockPos(), pPlayerInventory, this.dataAccess);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.items = NonNullList.withSize(this.getMaxStackSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.items);
    }


    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        ContainerHelper.saveAllItems(nbt, this.items);
    }

    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int dataIndex) {
            return switch (dataIndex) {
                case 0 -> CrystalFurnaceBlockEntity.this.litTime;
                case 1 -> CrystalFurnaceBlockEntity.this.litDuration;
                case 2 -> CrystalFurnaceBlockEntity.this.cookingTotalTime;
                case 3 -> CrystalFurnaceBlockEntity.this.numSlots;
                case 4 -> CrystalFurnaceBlockEntity.this.numFuelSlots;
                case 5 -> CrystalFurnaceBlockEntity.this.cookingProgress[0];
                case 6 -> CrystalFurnaceBlockEntity.this.cookingProgress[1];
                case 7 -> CrystalFurnaceBlockEntity.this.cookingProgress[2];
                case 8 -> CrystalFurnaceBlockEntity.this.cookingProgress[3];
                case 9 -> CrystalFurnaceBlockEntity.this.cookingProgress[4];
                default -> 0;
            };
        }

        public void set(int dataIndex, int value) {
            switch (dataIndex) {
                case 0 -> CrystalFurnaceBlockEntity.this.litTime = value;
                case 1 -> CrystalFurnaceBlockEntity.this.litDuration = value;
                case 2 -> CrystalFurnaceBlockEntity.this.cookingTotalTime = value;
                case 3 -> CrystalFurnaceBlockEntity.this.numSlots = value;
                case 4 -> CrystalFurnaceBlockEntity.this.numFuelSlots = value;
                case 5 -> CrystalFurnaceBlockEntity.this.cookingProgress[0] = value;
                case 6 -> CrystalFurnaceBlockEntity.this.cookingProgress[1] = value;
                case 7 -> CrystalFurnaceBlockEntity.this.cookingProgress[2] = value;
                case 8 -> CrystalFurnaceBlockEntity.this.cookingProgress[3] = value;
                case 9 -> CrystalFurnaceBlockEntity.this.cookingProgress[4] = value;
            }

        }

        public int getCount() {
            return 10;
        }
    };

    public boolean isFuel(ItemStack stack) {
        return net.minecraftforge.common.ForgeHooks.getBurnTime(stack, this.recipeType) > 0;
    }

    public int[] getInputSlots() {
        return this.INPUT_SLOTS;
    }

    public int[] getOutputSLots() {
        return this.OUTPUT_SLOTS;
    }

    public int[] getFuelSlots() {
        return this.FUEL_SLOTS;
    }
}


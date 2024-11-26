package dev.willyelton.crystal_tools.common.levelable.block.entity;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.energy.CrystalEnergyStorage;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalQuarryContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.LevelableContainerData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CrystalQuarryBlockEntity extends LevelableBlockEntity implements MenuProvider {
    public static final int DATA_SIZE = 1;

    private static final int INVENTORY_SIZE = 9;
    private static final int MAX_BLOCKS_PER_TICK = 20;

    // Item storage
    private NonNullList<ItemStack> storedItems;
    private IItemHandler itemHandler;

    // Energy Storage
    private CrystalEnergyStorage energyStorage;

    // Config
    private final int baseFEUsage;
    private final boolean useDirt;

    // Upgrades

    // Quarry things
    private int tickCounter = 0;
    private int currentSpeed = 1;
    private BlockPos miningAt = null;
    private int yLevel;
    private boolean finished = false;

    // Range
    // Should just use block positions?
    private int minX;
    private int maxX;
    private int minZ;
    private int maxZ;
    private int minY;
    private int maxY;

    // Can also use a position for this maybe?
    private int currentX;
    private int currentY;
    private int currentZ;

    private BlockPos topCorner;
    private BlockPos bottomCorner;

    public CrystalQuarryBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.CRYSTAL_QUARRY_BLOCK_ENTITY.get(), pos, state);
        storedItems = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

        // TODO: Config
        baseFEUsage = 50;
        useDirt = false;

        // Caps
        itemHandler = new ItemStackHandler(storedItems);
        energyStorage = new CrystalEnergyStorage(10000, baseFEUsage * 2, 0, 0);
    }

    public IItemHandler getItemHandlerCapForSide(Direction side) {
        return itemHandler;
    }

    public IEnergyStorage getEnergyStorageCapForSide(Direction side) {
        return energyStorage;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.crystal_tools.crystal_quarry");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // TODO: Could make super class generic on container menu?
        return new CrystalQuarryContainerMenu(containerId, player.level(), this.getBlockPos(), playerInventory, this.dataAccess);
    }

    // TODO
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }

    // TODO
    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
    }

    // TODO
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    // TODO
    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
    }

    // TODO
    @Override
    protected void addToExtraData(String key, float value) {

    }

    // TODO
    @Override
    protected void resetExtraSkills() {

    }

    // TODO
    protected final ContainerData dataAccess = new LevelableContainerData(this) {
        @Override
        protected int getExtra(int index) {
            return switch (index) {
                default -> 0;
            };
        }

        @Override
        protected void setExtra(int index, int value) {
            switch (index) {

            }
        }

        @Override
        protected int getExtraDataSize() {
            return CrystalGeneratorBlockEntity.DATA_SIZE;
        }
    };

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (finished) return;

        if (topCorner == null || bottomCorner == null) return;

        tickCounter++;
        if (tickCounter >= currentSpeed) {
            tickCounter = 0;
            miningAt = new BlockPos(currentX, currentY, currentZ);

            int blocksThisTick = 0;
            while (!finished && blocksThisTick < MAX_BLOCKS_PER_TICK) {
                blocksThisTick++;
                if (canMine(level.getBlockState(miningAt))) {
                    level.destroyBlock(miningAt, false, null);
                    nextPosition();
                    break;
                }
                blocksThisTick++;
                nextPosition();
            }
        }
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    // TODO: do I need to store all of the stabilizer positions to check if they are broken?
    public void setPositions(BlockPos topCorner, BlockPos bottomCorner) {
        this.minX = Math.min(topCorner.getX(), bottomCorner.getX()) + 1;
        this.maxX = Math.max(topCorner.getX(), bottomCorner.getX()) - 1;
        this.minZ = Math.min(topCorner.getZ(), bottomCorner.getZ()) + 1;
        this.maxZ = Math.max(topCorner.getZ(), bottomCorner.getZ()) - 1;
        this.minY = Math.min(topCorner.getY(), bottomCorner.getY());
        this.maxY = Math.max(topCorner.getY(), bottomCorner.getY());

        this.currentX = minX;
        this.currentZ = minZ;
        this.currentY = maxY;

        this.topCorner = topCorner;
        this.bottomCorner = bottomCorner;
        this.yLevel = topCorner.getY();
        this.miningAt = topCorner;
    }

    private boolean canMine(BlockState state) {
        return !state.isAir() && state.getDestroySpeed(level, worldPosition) > 0;
    }

    private void nextPosition() {
        currentX++;
        if (currentX > maxX) {
            currentX = minX;
            currentZ++;
            if (currentZ > maxZ) {
                currentZ = minZ;
                currentY--;
                if (currentY < minY) {
                    finished = true;
                }
            }
        }
        miningAt = new BlockPos(currentX, currentY, currentZ);
    }
}

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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class CrystalQuarryBlockEntity extends LevelableBlockEntity implements MenuProvider {
    public static final int DATA_SIZE = 2;

    private static final int INVENTORY_SIZE = 27;
    private static final int MAX_BLOCKS_PER_TICK = 20;

    // Item storage
    private NonNullList<ItemStack> storedItems;
    private IItemHandler itemHandler;

    // Energy Storage
    private CrystalEnergyStorage energyStorage;

    // Config
    private final int baseFEUsage;
    private final boolean useDirt;
    private final float blockEnergyModifier;

    // Upgrades

    // Quarry things
    private int tickCounter = 0;
    private int currentSpeed = 1;
    private BlockPos miningAt = null;
    private boolean finished = false;

    // Range
    // Should just use block positions?
    private int minX;
    private int maxX;
    private int minZ;
    private int maxZ;
    private int minY;
    private int maxY;

    public CrystalQuarryBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.CRYSTAL_QUARRY_BLOCK_ENTITY.get(), pos, state);
        storedItems = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

        // TODO: Config
        baseFEUsage = 50;
        useDirt = false;
        blockEnergyModifier = 1.0F;

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
        ContainerHelper.loadAllItems(tag, this.storedItems, registries);

        this.tickCounter = tag.getInt("TickCounter");
        this.currentSpeed = tag.getInt("CurrentSpeed");
        if (tag.contains("MiningAt")) {
            this.miningAt = BlockPos.of(tag.getLong("MiningAt"));
        }
        this.finished = tag.getBoolean("Finished");

        this.minX = tag.getInt("MinX");
        this.maxX = tag.getInt("MaxX");
        this.minZ = tag.getInt("MinZ");
        this.maxZ = tag.getInt("MaxZ");
        this.minY = tag.getInt("MinY");
        this.maxY = tag.getInt("MaxY");
    }

    // TODO
    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        // TODO: Probably wanna do just items and energy here. Or maybe everything and reset when you select new coords?
        super.applyImplicitComponents(componentInput);
    }

    // TODO
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.storedItems, registries);

        tag.putInt("TickCounter", this.tickCounter);
        tag.putInt("CurrentSpeed", this.currentSpeed);
        if (this.miningAt != null) {
            tag.putLong("MiningAt", this.miningAt.asLong());
        }
        tag.putBoolean("Finished", this.finished);

        tag.putInt("MinX", this.minX);
        tag.putInt("MaxX", this.maxX);
        tag.putInt("MinZ", this.minZ);
        tag.putInt("MaxZ", this.maxZ);
        tag.putInt("MinY", this.minY);
        tag.putInt("MaxY", this.maxY);
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
                case 3 -> energyStorage.getEnergyStored();
                case 4 -> energyStorage.getMaxEnergyStored();
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
            return CrystalQuarryBlockEntity.DATA_SIZE;
        }
    };

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (finished) return;

        if (miningAt == null) return;

        tickCounter++;
        if (tickCounter >= currentSpeed) {
            tickCounter = 0;

            int blocksThisTick = 0;
            while (!finished && blocksThisTick < MAX_BLOCKS_PER_TICK) {
                blocksThisTick++;
                int energyCost = getEnergyForBlock(level.getBlockState(miningAt), miningAt);

                if (energyCost > energyStorage.getEnergyStored()) {
                    break;
                }

                if (canMine(level.getBlockState(miningAt))) {
                    List<ItemStack> drops = Block.getDrops(level.getBlockState(miningAt), (ServerLevel) level, miningAt, level.getBlockEntity(miningAt));
                    if (dropsFit(drops)) {
                        level.destroyBlock(miningAt, false, null);
                        insertDrops(drops);
                        energyStorage.removeEnergy(energyCost);
                        nextPosition();
                        setChanged();
                    }

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

        this.miningAt = new BlockPos(minX, minZ, maxY);
    }

    private boolean canMine(BlockState state) {
        return !state.isAir() && state.getDestroySpeed(level, worldPosition) > 0;
    }

    private void nextPosition() {
        int currentX = miningAt.getX();
        int currentY = miningAt.getY();
        int currentZ = miningAt.getZ();

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

    private boolean dropsFit(List<ItemStack> stacksToInsert) {
        for (ItemStack stack : stacksToInsert) {
            ItemStack result = ItemHandlerHelper.insertItem(this.itemHandler, stack, true);

            if (!result.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private void insertDrops(List<ItemStack> stacksToInsert) {
        // Shouldn't be anything here, maybe possible if things can be added mid-tick
        List<ItemStack> leftover = new ArrayList<>();

        for (ItemStack stack : stacksToInsert) {
            leftover.add(ItemHandlerHelper.insertItem(this.itemHandler, stack, false));
        }

        leftover.stream().filter(stack -> !stack.isEmpty()).forEach(stack -> Block.popResource(level, worldPosition, stack));
    }

    private int getEnergyForBlock(BlockState state, BlockPos pos) {
        // TODO: Fluids have really high hardness
        // TODO: Add skill modifiers
        return (int) Math.ceil(state.getDestroySpeed(this.level, pos) * blockEnergyModifier + this.baseFEUsage);
    }
}

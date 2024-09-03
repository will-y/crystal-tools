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
    private int tickCounter;
    private int maxCounter;
    private BlockPos miningAt;
    private int yLevel;

    // Range
    private BlockPos bottomLeft;
    private BlockPos topRight;

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

    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }
}

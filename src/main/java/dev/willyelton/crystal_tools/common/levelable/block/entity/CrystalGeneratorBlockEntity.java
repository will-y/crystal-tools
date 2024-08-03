package dev.willyelton.crystal_tools.common.levelable.block.entity;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalGeneratorContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalFurnaceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

// TODO: Superclass with just furnace
// TODO: Override EnergyStorage, add setters
// TODO: Persist energy
// TODO: Send energy out (directions round robin probabl)
public class CrystalGeneratorBlockEntity extends LevelableBlockEntity implements MenuProvider {
    public static final int DATA_SIZE = 107;
    private static final int SIZE = 1;

    // Item storage
    private NonNullList<ItemStack> fuelItems;
    private IItemHandler fuelHandler;

    // Energy storage
    private IEnergyStorage energyStorage;

    // Config TODO
    private final int baseFePerTick = 40;

    private int litTime;
    private int litTotalTime;

    public CrystalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.CRYSTAL_GENERATOR_BLOCK_ENTITY.get(), pos, state);
        fuelItems = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        fuelHandler = new ItemStackHandler(fuelItems);
        energyStorage = new EnergyStorage(10000, 40, 80, 0);
    }

    public IItemHandler getItemHandlerCapForSide(Direction side) {
        // TODO: Catalyst different side?
        return fuelHandler;
    }

    public IEnergyStorage getEnergyStorageCapForSide(Direction side) {
        return energyStorage;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.crystal_tools.crystal_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CrystalGeneratorContainerMenu(containerId, player.level(), this.getBlockPos(), playerInventory, this.dataAccess);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, this.fuelItems, registries);

        this.litTime = tag.getInt("LitTime");
        this.litTotalTime = tag.getInt("LitTotalTime");
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        ItemContainerContents contents = componentInput.get(DataComponents.CONTAINER);
        this.fuelItems = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        this.fuelHandler = new ItemStackHandler(fuelItems);
        if (contents != null) {
            contents.copyInto(this.fuelItems);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.fuelItems, registries);
        tag.putInt("LitTime", this.litTime);
        tag.putInt("LitTotalTime", this.litTotalTime);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        ItemContainerContents contents = ItemContainerContents.fromItems(this.fuelItems);
        components.set(DataComponents.CONTAINER, contents);
    }

    @Override
    protected void addToExtraData(String key, float value) {

    }

    protected final ContainerData dataAccess = new LevelableContainerData(this) {
        @Override
        int getExtra(int index) {
            return switch (index) {
                case 3 -> litTime;
                case 4 -> litTotalTime;
                case 5 -> energyStorage.getEnergyStored();
                case 6 -> energyStorage.getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        void setExtra(int index, int value) {
            switch (index) {
                case 3 -> litTime = value;
                case 4 -> litTotalTime = value;
            }
        }

        @Override
        int getExtraDataSize() {
            return 4;
        }
    };

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        boolean isLit = this.isLit();
        boolean needsChange = false;

        if (isLit) {
            litTime--;
            energyStorage.receiveEnergy(baseFePerTick, false);
        }

        ItemStack fuelItemStack = this.fuelItems.getFirst();
        boolean hasFuel = !fuelItemStack.isEmpty();

        if (!this.isLit() && hasFuel) {
            this.litTime = this.getBurnDuration(fuelItemStack);
            this.litTotalTime = this.litTime;

            if (this.isLit()) {
                needsChange = true;
                if (fuelItemStack.hasCraftingRemainingItem()) {
                    fuelItems.set(0, fuelItemStack.getCraftingRemainingItem());
                } else {
                    fuelItemStack.shrink(1);
                    if (fuelItemStack.isEmpty()) {
                        fuelItems.set(0, fuelItemStack.getCraftingRemainingItem());
                    }
                }
            }
        }

        if (isLit != this.isLit()) {
            needsChange = true;
            state = state.setValue(CrystalFurnaceBlock.LIT, this.isLit());
            level.setBlock(pos, state, 3);
        }

        if (needsChange) {
            setChanged(level, pos, state);
        }
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    private int getBurnDuration(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        } else {
            return stack.getBurnTime(null);
        }
    }

    public IItemHandler getFuelHandler() {
        return fuelHandler;
    }
}

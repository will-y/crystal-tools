package dev.willyelton.crystal_tools.common.levelable.block.entity;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.energy.CrystalEnergyStorage;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalGeneratorContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalFurnaceBlock;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.LevelableContainerData;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CrystalGeneratorBlockEntity extends LevelableBlockEntity implements MenuProvider {
    public static final int DATA_SIZE = 4;
    private static final int SIZE = 1;

    // Item storage
    private NonNullList<ItemStack> fuelItems;
    private IItemHandler fuelHandler;

    // Energy storage
    private CrystalEnergyStorage energyStorage;

    // Config
    private final int baseFEStorage;
    private final int baseFETransfer;

    // Upgrades
    private float addedFEGeneration = 0;
    private float fuelEfficiency = 0;
    private float addedFEStorage = 0;
    private boolean redstoneControl = false;
    private boolean saveFuel = false;
    private boolean metalGenerator = false;
    private boolean foodGenerator = false;
    private boolean gemGenerator = false;

    // Base generator things
    private int litTime;
    private int litTotalTime;

    public CrystalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.CRYSTAL_GENERATOR_BLOCK_ENTITY.get(), pos, state);
        fuelItems = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        fuelHandler = new ItemStackHandler(fuelItems);

        baseFEStorage = CrystalToolsConfig.BASE_FE_STORAGE.get();
        baseFETransfer = CrystalToolsConfig.BASE_FE_TRANSFER.get();

        energyStorage = new CrystalEnergyStorage(baseFEStorage, 0, baseFETransfer, 0);
    }

    public IItemHandler getItemHandlerCapForSide(Direction side) {
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

        // Upgrades
        this.addedFEGeneration = tag.getFloat("AddedFEGeneration");
        this.fuelEfficiency = tag.getFloat("FuelEfficiency");
        this.addedFEStorage = tag.getFloat("AddedFEStorage");
        this.redstoneControl = tag.getBoolean("RedstoneControl");
        this.saveFuel = tag.getBoolean("SaveFuel");
        this.metalGenerator = tag.getBoolean("MetalGenerator");
        this.foodGenerator = tag.getBoolean("FoodGenerator");
        this.gemGenerator = tag.getBoolean("GemGenerator");

        int energy = tag.getInt("Energy");
        energyStorage = new CrystalEnergyStorage(baseFEStorage + (int) addedFEStorage, 0, baseFETransfer + (int) addedFEGeneration * 2, energy);
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
        // TODO: Get everything
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.fuelItems, registries);
        tag.putInt("LitTime", this.litTime);
        tag.putInt("LitTotalTime", this.litTotalTime);

        tag.putFloat("AddedFEGeneration", this.addedFEGeneration);
        tag.putFloat("FuelEfficiency", this.fuelEfficiency);
        tag.putFloat("AddedFEStorage", this.addedFEStorage);
        tag.putBoolean("RedstoneControl", this.redstoneControl);
        tag.putBoolean("SaveFuel", this.saveFuel);
        tag.putBoolean("MetalGenerator", this.metalGenerator);
        tag.putBoolean("FoodGenerator", this.foodGenerator);
        tag.putBoolean("GemGenerator", this.gemGenerator);

        tag.putInt("Energy", this.energyStorage.getEnergyStored());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        ItemContainerContents contents = ItemContainerContents.fromItems(this.fuelItems);
        components.set(DataComponents.CONTAINER, contents);
        // TODO: Store everything
    }

    @Override
    protected void addToExtraData(String key, float value) {
        switch (key) {
            case "fe_generation" -> {
                int generationToAdd = (int) value * CrystalToolsConfig.FE_GENERATION_PER_LEVEL.get();
                this.addedFEGeneration += generationToAdd;
                this.energyStorage.setMaxExtract(this.energyStorage.getMaxExtract() + generationToAdd * 2);
            }
            case "fuel_efficiency" -> this.fuelEfficiency += value;
            case "fe_capacity" -> {
                int storageToAdd = (int) value * CrystalToolsConfig.FE_STORAGE_PER_LEVEL.get();
                this.addedFEStorage += storageToAdd;
                this.energyStorage.setCapacity(this.energyStorage.getMaxEnergyStored() + storageToAdd);
            }
            case "redstone_control" -> this.redstoneControl = value == 1F;
            case "save_fuel" -> this.saveFuel = value == 1F;
            case "metal_generator" -> this.metalGenerator = value == 1F;
            case "food_generator" -> this.foodGenerator = value == 1F;
            case "gem_generator" -> this.gemGenerator = value == 1F;
        }
    }

    protected final ContainerData dataAccess = new LevelableContainerData(this) {
        @Override
        protected int getExtra(int index) {
            return switch (index) {
                case 3 -> litTime;
                case 4 -> litTotalTime;
                case 5 -> energyStorage.getEnergyStored();
                case 6 -> energyStorage.getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        protected void setExtra(int index, int value) {
            switch (index) {
                case 3 -> litTime = value;
                case 4 -> litTotalTime = value;
            }
        }

        @Override
        protected int getExtraDataSize() {
            return CrystalGeneratorBlockEntity.DATA_SIZE;
        }
    };

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        boolean hasRedstone = level.hasNeighborSignal(pos);
        if (hasRedstone && redstoneControl) {
            return;
        }

        boolean wasLit = this.isLit();
        boolean needsChange = false;

        if (wasLit) {
            boolean canFitEnergy = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored() >= getGeneration(ItemStack.EMPTY);
            if (canFitEnergy || !saveFuel) {
                litTime--;
            }
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

        if (this.isLit()) {
            // TODO: Store the currently burning fuel (needed for non furnace fuel stacks)
            int generation = getGeneration(ItemStack.EMPTY);
            if (energyStorage.canAdd(generation)) {
                energyStorage.addEnergy(generation);
            }
        }

        distributeEnergy(level, pos);

        if (wasLit != this.isLit()) {
            needsChange = true;
            state = state.setValue(CrystalFurnaceBlock.LIT, this.isLit());
            level.setBlock(pos, state, 3);
        }

        if (needsChange) {
            setChanged(level, pos, state);
        }
    }

    private void distributeEnergy(Level level, BlockPos pos) {
        List<IEnergyStorage> possibleDestinations = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(direction), direction.getOpposite());
            if (energyStorage != null && energyStorage.canReceive()) {
                possibleDestinations.add(energyStorage);
            }
        }

        if (possibleDestinations.isEmpty()) {
            return;
        }

        int amountToTransfer = Math.min(this.baseFETransfer, energyStorage.getEnergyStored());
        int amountAdded = 0;
        boolean didTransfer = true;

        while (amountAdded < amountToTransfer && !possibleDestinations.isEmpty() && didTransfer) {
            int amountPerBlock = (amountToTransfer - amountAdded) / possibleDestinations.size();
            if (amountPerBlock == 0) {
                amountPerBlock = (amountToTransfer - amountAdded) % possibleDestinations.size();
                if (amountPerBlock == 0) {
                    break;
                }
            }
            didTransfer = false;
            Iterator<IEnergyStorage> itr = possibleDestinations.iterator();
            while (itr.hasNext() && amountAdded < amountToTransfer) {
                IEnergyStorage storage = itr.next();
                int added = storage.receiveEnergy(amountPerBlock, false);
                if (added > 0) {
                    didTransfer = true;
                }
                amountAdded += added;
                if (added != amountPerBlock) {
                    itr.remove();
                }
            }
        }

        energyStorage.extractEnergy(amountAdded, false);
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    private int getBurnDuration(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        } else {
            // TODO: Some config for this?
            return (int) (stack.getBurnTime(null) * (1 + fuelEfficiency));
        }
    }

    private int getGeneration(ItemStack burnedStack) {
        return CrystalToolsConfig.BASE_FE_GENERATION.get() + (int) this.addedFEGeneration;
    }

    public IItemHandler getFuelHandler() {
        return fuelHandler;
    }
}

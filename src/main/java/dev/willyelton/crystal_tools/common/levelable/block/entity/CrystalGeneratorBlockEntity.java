package dev.willyelton.crystal_tools.common.levelable.block.entity;

import com.google.common.base.Predicates;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.components.GeneratorData;
import dev.willyelton.crystal_tools.common.components.GeneratorUpgrades;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.datamap.DataMaps;
import dev.willyelton.crystal_tools.common.datamap.GeneratorFuelData;
import dev.willyelton.crystal_tools.common.energy.CrystalEnergyStorage;
import dev.willyelton.crystal_tools.common.inventory.GeneratorItemStackHandler;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalGeneratorContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalFurnaceBlock;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.LevelableContainerData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.*;

public class CrystalGeneratorBlockEntity extends LevelableBlockEntity implements MenuProvider {
    public static final int DATA_SIZE = 5;
    private static final int SIZE = 1;

    // Item storage
    private ItemStacksResourceHandler fuelHandler;

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
    private ItemStack burnedItem = ItemStack.EMPTY;

    public CrystalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistration.CRYSTAL_GENERATOR_BLOCK_ENTITY.get(), pos, state);
        fuelHandler = new GeneratorItemStackHandler(NonNullList.withSize(SIZE, ItemStack.EMPTY), this);

        baseFEStorage = CrystalToolsConfig.BASE_FE_STORAGE.get();
        baseFETransfer = CrystalToolsConfig.BASE_FE_TRANSFER.get();

        energyStorage = new CrystalEnergyStorage(baseFEStorage, 0, baseFETransfer, 0);
    }

    public ResourceHandler<ItemResource> getItemHandlerCapForSide(Direction side) {
        return fuelHandler;
    }

    public EnergyHandler getEnergyStorageCapForSide(Direction side) {
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
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.fuelHandler.deserialize(valueInput);

        this.litTime = valueInput.getInt("LitTime").orElse(0);
        this.litTotalTime = valueInput.getInt("LitTotalTime").orElse(0);
        this.burnedItem = valueInput.read("BurnedItem", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);

        // Upgrades
        this.addedFEGeneration = valueInput.getFloatOr("AddedFEGeneration", 0F);
        this.fuelEfficiency = valueInput.getFloatOr("FuelEfficiency", 0F);
        this.addedFEStorage = valueInput.getFloatOr("AddedFEStorage", 0F);
        this.redstoneControl = valueInput.getBooleanOr("RedstoneControl", false);
        this.saveFuel = valueInput.getBooleanOr("SaveFuel", false);
        this.metalGenerator = valueInput.getBooleanOr("MetalGenerator", false);
        this.foodGenerator = valueInput.getBooleanOr("FoodGenerator", false);
        this.gemGenerator = valueInput.getBooleanOr("GemGenerator", false);

        int energy = valueInput.getInt("Energy").orElse(0);
        energyStorage = new CrystalEnergyStorage(baseFEStorage + (int) addedFEStorage, 0, baseFETransfer + (int) addedFEGeneration * 2, energy);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentInput) {
        super.applyImplicitComponents(componentInput);
        ItemContainerContents contents = componentInput.get(DataComponents.CONTAINER);
        if (contents != null) {
            for (int i = 0; i < contents.getSlots(); i++) {
                ItemStack stack = contents.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    this.fuelHandler.set(i, ItemResource.of(stack), stack.getCount());
                }
            }
        }

        GeneratorUpgrades generatorUpgrades = componentInput.get(dev.willyelton.crystal_tools.common.components.DataComponents.GENERATOR_UPGRADES);

        if (generatorUpgrades != null) {
            this.addedFEGeneration = generatorUpgrades.feGeneration();
            this.fuelEfficiency = generatorUpgrades.fuelEfficiency();
            this.addedFEStorage = generatorUpgrades.feStorage();
            this.redstoneControl = generatorUpgrades.redstoneControl();
            this.saveFuel = generatorUpgrades.saveFuel();
            this.metalGenerator = generatorUpgrades.metalGenerator();
            this.foodGenerator = generatorUpgrades.foodGenerator();
            this.gemGenerator = generatorUpgrades.gemGenerator();
        }

        GeneratorData generatorData = componentInput.get(dev.willyelton.crystal_tools.common.components.DataComponents.GENERATOR_DATA);
        if (generatorData != null) {
            this.litTime = generatorData.litTime();
            this.litTotalTime = generatorData.litTotalTime();
            this.burnedItem = generatorData.burnedItem().copy();

            int energy = generatorData.energy();
            this.energyStorage = new CrystalEnergyStorage(baseFEStorage + (int) addedFEStorage, 0, baseFETransfer + (int) addedFEGeneration * 2, energy);
        } else {
            this.energyStorage = new CrystalEnergyStorage(baseFEStorage + (int) addedFEStorage, 0, baseFETransfer + (int) addedFEGeneration * 2, 0);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        this.fuelHandler.serialize(valueOutput);
        valueOutput.putInt("LitTime", this.litTime);
        valueOutput.putInt("LitTotalTime", this.litTotalTime);

        if (!burnedItem.isEmpty()) {
            valueOutput.store("BurnedItem", ItemStack.OPTIONAL_CODEC, burnedItem);
        }

        valueOutput.putFloat("AddedFEGeneration", this.addedFEGeneration);
        valueOutput.putFloat("FuelEfficiency", this.fuelEfficiency);
        valueOutput.putFloat("AddedFEStorage", this.addedFEStorage);
        valueOutput.putBoolean("RedstoneControl", this.redstoneControl);
        valueOutput.putBoolean("SaveFuel", this.saveFuel);
        valueOutput.putBoolean("MetalGenerator", this.metalGenerator);
        valueOutput.putBoolean("FoodGenerator", this.foodGenerator);
        valueOutput.putBoolean("GemGenerator", this.gemGenerator);

        valueOutput.putInt("Energy", this.energyStorage.getAmountAsInt());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        ItemContainerContents contents = ItemContainerContents.fromItems(this.fuelHandler.copyToList());
        components.set(DataComponents.CONTAINER, contents);

        GeneratorUpgrades generatorUpgrades = new GeneratorUpgrades((int) addedFEGeneration, fuelEfficiency, (int) addedFEStorage,
                redstoneControl, saveFuel, metalGenerator, foodGenerator, gemGenerator);
        components.set(dev.willyelton.crystal_tools.common.components.DataComponents.GENERATOR_UPGRADES, generatorUpgrades);

        GeneratorData generatorData = new GeneratorData(litTime, litTotalTime, burnedItem.copy(), energyStorage.getAmountAsInt());
        components.set(dev.willyelton.crystal_tools.common.components.DataComponents.GENERATOR_DATA, generatorData);
    }

    @Override
    protected void addToExtraData(String key, float value) {
        if (FE_GENERATION.toString().equals(key)) {
            float generationToAdd = value * CrystalToolsConfig.FE_GENERATION_PER_LEVEL.get();
            this.addedFEGeneration += generationToAdd;
            this.energyStorage.setMaxExtract(this.energyStorage.getMaxExtract() + (int) generationToAdd * 2);
        } else if (FUEL_EFFICIENCY.toString().equals(key)) {
            this.fuelEfficiency += value;
        } else if (FE_CAPACITY.toString().equals(key)) {
            float storageToAdd = value * CrystalToolsConfig.FE_STORAGE_PER_LEVEL.get();
            this.addedFEStorage += storageToAdd;
            this.energyStorage.setCapacity(this.energyStorage.getCapacityAsInt() + (int) storageToAdd);
        } else if (REDSTONE_CONTROL.toString().equals(key)) {
            this.redstoneControl = value == 1F;
        } else if (SAVE_FUEL.toString().equals(key)) {
            this.saveFuel = value == 1F;
        } else if (METAL_GENERATOR.toString().equals(key)) {
            this.metalGenerator = value == 1F;
        } else if (FOOD_GENERATOR.toString().equals(key)) {
            this.foodGenerator = value == 1F;
        } else if (GEM_GENERATOR.toString().equals(key)) {
            this.gemGenerator = value == 1F;
        }
    }

    @Override
    protected void resetExtraSkills() {
        this.addedFEGeneration = 0;
        this.energyStorage = new CrystalEnergyStorage(baseFEStorage, 0, baseFETransfer, Math.max(energyStorage.getAmountAsInt(), baseFEStorage));
        this.fuelEfficiency = 0;
        this.addedFEStorage = 0;
        this.redstoneControl = false;
        this.saveFuel = false;
        this.metalGenerator = false;
        this.foodGenerator = false;
        this.gemGenerator = false;
    }

    protected final ContainerData dataAccess = new LevelableContainerData(this) {
        @Override
        protected int getExtra(int index) {
            return switch (index) {
                case 3 -> litTime;
                case 4 -> litTotalTime;
                case 5 -> energyStorage.getAmountAsInt();
                case 6 -> energyStorage.getCapacityAsInt();
                case 7 -> litTime > 0 ? getGeneration(burnedItem) : 0;
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

    @Override
    protected int getBaseExpCap() {
        return CrystalToolsConfig.GENERATOR_BASE_EXPERIENCE_CAP.get();
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        boolean hasRedstone = level.hasNeighborSignal(pos);
        if (hasRedstone && redstoneControl) {
            return;
        }

        boolean wasLit = this.isLit();
        boolean needsChange = false;

        if (wasLit) {
            boolean canFitEnergy = energyStorage.getCapacityAsInt() - energyStorage.getAmountAsInt() >= getGeneration(ItemStack.EMPTY);
            if (canFitEnergy || !saveFuel) {
                litTime--;

                if (litTime <= 0) {
                    this.burnedItem = ItemStack.EMPTY;
                    needsChange = true;
                }
            }
        }

        try (Transaction tx = Transaction.open(null)) {
            ResourceStack<ItemResource> resourceStack = ResourceHandlerUtil.extractFirst(fuelHandler, Predicates.alwaysTrue(), 1, tx);
            ItemStack fuelItemStack = resourceStack == null ? ItemStack.EMPTY : resourceStack.resource().toStack();
            boolean hasFuel = !fuelItemStack.isEmpty();

            if (!this.isLit() && hasFuel) {
                this.litTime = this.getBurnDuration(fuelItemStack);
                this.litTotalTime = this.litTime;
                this.burnedItem = fuelItemStack.copy();

                // TODO: Something here to do the food leftover item?
                if (this.isLit()) {
                    needsChange = true;
                    addSkillExpFromBurn(this.litTotalTime);
                    if (!fuelItemStack.getCraftingRemainder().isEmpty()) {
                        fuelHandler.insert(ItemResource.of(fuelItemStack.getCraftingRemainder()), fuelItemStack.getCraftingRemainder().getCount(), tx);
                    }

                    tx.commit();
                }
            }
        }

        if (this.isLit()) {
            int generation = getGeneration(burnedItem);
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
        List<EnergyHandler> possibleDestinations = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            EnergyHandler handler = level.getCapability(Capabilities.Energy.BLOCK, pos.relative(direction), direction.getOpposite());
            if (handler != null) {
                possibleDestinations.add(handler);
            }
        }

        if (possibleDestinations.isEmpty()) {
            return;
        }

        int amountToTransfer = Math.min(this.baseFETransfer + (int) this.addedFEGeneration * 2, energyStorage.getAmountAsInt());
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
            Iterator<EnergyHandler> itr = possibleDestinations.iterator();
            while (itr.hasNext() && amountAdded < amountToTransfer) {
                EnergyHandler storage = itr.next();
                try (Transaction tx = Transaction.open(null)) {
                    int added = storage.insert(amountPerBlock, tx);
                    if (added > 0) {
                        didTransfer = true;
                    }
                    amountAdded += added;
                    if (added != amountPerBlock) {
                        itr.remove();
                    }
                    tx.commit();
                }
            }
        }

        try (Transaction tx = Transaction.open(null)) {
            energyStorage.extract(amountAdded, tx);
            tx.commit();
        }
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    public int getBurnDuration(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        } else {
            GeneratorFuelData fuelData = getFuelData(stack);

            if (fuelData != null) {
                return (int) (fuelData.burnTime() * (1 + fuelEfficiency));
            }

            return (int) (stack.getBurnTime(null, this.level.fuelValues()) * (1 + fuelEfficiency));
        }
    }

    private int getGeneration(ItemStack burnedStack) {
        GeneratorFuelData fuelData = getFuelData(burnedStack);

        int bonusGeneration = fuelData == null ? 0 : fuelData.bonusGeneration();

        return CrystalToolsConfig.BASE_FE_GENERATION.get() + (int) this.addedFEGeneration + bonusGeneration;
    }

    private @Nullable GeneratorFuelData getFuelData(ItemStack stack) {
        return getFuelData(stack, foodGenerator, metalGenerator, gemGenerator);
    }

    public static @Nullable GeneratorFuelData getFuelData(ItemStack stack, boolean foodGenerator, boolean metalGenerator, boolean gemGenerator) {
        if (stack.isEmpty()) return null;

        if (foodGenerator) {
            FoodProperties foodData = stack.get(DataComponents.FOOD);

            if (foodData != null) {
                return new GeneratorFuelData(getBurnTimeFromFood(foodData), 0);
            }
        }

        if (metalGenerator) {
            GeneratorFuelData data = stack.getItemHolder().getData(DataMaps.GENERATOR_METALS);
            if (data != null) {
                return data;
            }
        }

        if (gemGenerator) {
            return stack.getItemHolder().getData(DataMaps.GENERATOR_GEMS);
        }

        return null;
    }

    private void addSkillExpFromBurn(int burnTime) {
        this.addExp((int) Math.ceil(CrystalToolsConfig.SKILL_POINTS_PER_BURN_TIME.get() * burnTime));
    }

    public ItemStacksResourceHandler getFuelHandler() {
        return fuelHandler;
    }

    public static int getBurnTimeFromFood(FoodProperties foodProperties) {
        return (int) (CrystalToolsConfig.FOOD_BURN_TIME_MULTIPLIER.get() * (foodProperties.nutrition() + foodProperties.saturation()));
    }
}

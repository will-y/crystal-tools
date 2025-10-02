package dev.willyelton.crystal_tools.common.levelable.block.entity;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.client.particle.quarry.breakblock.QuarryBreakParticleData;
import dev.willyelton.crystal_tools.common.components.QuarryData;
import dev.willyelton.crystal_tools.common.components.QuarrySettings;
import dev.willyelton.crystal_tools.common.components.QuarryUpgrades;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.energy.CrystalEnergyStorage;
import dev.willyelton.crystal_tools.common.inventory.ItemResourceHandlerAdapterModifiable;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalQuarryContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalQuarryBlock;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.Action;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.AutoOutputAction;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.AutoOutputable;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.ChunkLoader;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.ChunkLoadingAction;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.LevelableContainerData;
import dev.willyelton.crystal_tools.common.network.data.QuarryMineBlockPayload;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.*;

public class CrystalQuarryBlockEntity extends LevelableBlockEntity implements MenuProvider, AutoOutputable, ChunkLoader {
    public static final int DATA_SIZE = 14;

    private static final int INVENTORY_SIZE = 27;
    private static final int MAX_BLOCKS_PER_TICK = 20;

    // Itemstacks for mocking fortune and silk touch
    private static final ItemStack FORTUNE_STACK = new ItemStack(ModRegistration.CRYSTAL_AIOT);
    private static final ItemStack SILK_TOUCH_STACK = new ItemStack(ModRegistration.CRYSTAL_AIOT);

    // Item storage
    private final NonNullList<ItemStack> storedItems;
    private final ItemStacksResourceHandler itemHandler;

    // Filter Things
    private final NonNullList<ItemStack> filterItems;
    private final ResourceHandler<ItemResource> filterItemHandler;
    private boolean whitelist = true;
    private int filterRows = 0;

    // Energy Storage
    private CrystalEnergyStorage energyStorage;

    // Settings
    private boolean useDirt;
    private boolean silkTouchEnabled;
    private boolean fortuneEnabled;
    private boolean autoOutputEnabled = true;

    // Upgrades
    private float speedUpgrade = 0;
    private boolean redstoneControl = false;
    private int fortuneLevel = 0;
    private boolean silkTouch = false;
    private int extraEnergyCost;

    // Quarry things
    private BlockPos miningAt = null;
    private float currentProgress = 0;
    private BlockState miningState = null;
    private boolean finished = false;
    private List<ItemStack> waitingStacks = new ArrayList<>();

    // Range
    // Should just use block positions?
    private int minX;
    private int maxX;
    private int minZ;
    private int maxZ;
    private int minY;
    private int maxY;

    private List<BlockPos> stabilizerPositions;

    // For rendering the cube
    private float centerX;
    private float centerY;
    private float centerZ;

    private AABB aabb;

    private AutoOutputAction autoOutputAction;
    private ChunkLoadingAction chunkLoadingAction;

    public CrystalQuarryBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistration.CRYSTAL_QUARRY_BLOCK_ENTITY.get(), pos, state);
        storedItems = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

        useDirt = false;

        // Caps
        itemHandler = new ItemStacksResourceHandler(storedItems);
        energyStorage = new CrystalEnergyStorage(10000, getEnergyCost() * 2, 0, 0);

        // Filter
        filterItems = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
        filterItemHandler = new ItemStacksResourceHandler(filterItems);
    }

    @Override
    protected Collection<Action> getDefaultActions() {
        autoOutputAction = new AutoOutputAction(this, this);
        chunkLoadingAction = new ChunkLoadingAction(this, this);

        return List.of(autoOutputAction, chunkLoadingAction);
    }

    public ResourceHandler<ItemResource> getItemHandlerCapForSide(Direction side) {
        return itemHandler;
    }

    public EnergyHandler getEnergyStorageCapForSide(Direction side) {
        return energyStorage;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.crystal_tools.crystal_quarry");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CrystalQuarryContainerMenu(containerId, player.level(), this.getBlockPos(), this.filterRows, playerInventory, this.dataAccess);
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        ContainerHelper.loadAllItems(valueInput, this.storedItems);
        ContainerHelper.loadAllItems(valueInput.childOrEmpty("FilterInventory"), this.filterItems);

        this.filterRows = valueInput.getInt("FilterRows").orElse(0);
        this.whitelist = valueInput.getBooleanOr("Whitelist", false);

        long pos = valueInput.getLongOr("MiningAt", 0L);
        if (pos != 0) {
            this.miningAt = BlockPos.of(pos);
        }

        this.currentProgress = valueInput.getFloatOr("CurrentProgress", 0F);
        this.waitingStacks = new ArrayList<>(valueInput.read("WaitingStacks", ItemStack.OPTIONAL_CODEC.listOf()).orElse(new ArrayList<>()));
        this.finished = valueInput.getBooleanOr("Finished", false);

        this.useDirt = valueInput.getBooleanOr("UseDirt", false);
        this.silkTouchEnabled = valueInput.getBooleanOr("SilkTouchEnabled", false);
        this.fortuneEnabled = valueInput.getBooleanOr("FortuneEnabled", false);
        this.autoOutputEnabled = valueInput.getBooleanOr("AutoOutputEnabled", false);
        this.autoOutputAction.setDisabled(!autoOutputEnabled);

        this.minX = valueInput.getInt("MinX").orElse(0);
        this.maxX = valueInput.getInt("MaxX").orElse(0);
        this.minZ = valueInput.getInt("MinZ").orElse(0);
        this.maxZ = valueInput.getInt("MaxZ").orElse(0);
        this.minY = valueInput.getInt("MinY").orElse(0);
        this.maxY = valueInput.getInt("MaxY").orElse(0);

        this.centerX = valueInput.getFloatOr("CenterX", 0F);
        this.centerY = valueInput.getFloatOr("CenterY", 0F);
        this.centerZ = valueInput.getFloatOr("CenterZ", 0F);

        this.speedUpgrade = valueInput.getFloatOr("SpeedUpgrade", 0F);
        this.redstoneControl = valueInput.getBooleanOr("RedstoneControl", false);
        this.fortuneLevel = valueInput.getInt("FortuneLevel").orElse(0);
        this.silkTouch = valueInput.getBooleanOr("SilkTouch", false);
        this.extraEnergyCost = valueInput.getInt("ExtraEnergyCost").orElse(0);

        int energy = valueInput.getInt("Energy").orElse(0);
        energyStorage = new CrystalEnergyStorage(10000, getEnergyCost() * 2, 0, energy);
        this.stabilizerPositions = valueInput.read("StabilizerPositions", Codec.LONG.listOf()).orElse(List.of()).stream().map(BlockPos::of).toList();

        createAABB();
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentInput) {
        super.applyImplicitComponents(componentInput);

        ItemContainerContents contents = componentInput.get(DataComponents.CONTAINER);
        if (contents != null) {
            contents.copyInto(this.storedItems);
        }

        ItemContainerContents filterContents = componentInput.get(dev.willyelton.crystal_tools.common.components.DataComponents.FILTER_INVENTORY);
        if (filterContents != null) {
            filterContents.copyInto(this.filterItems);
        }

        QuarryUpgrades quarryUpgrades = componentInput.get(dev.willyelton.crystal_tools.common.components.DataComponents.QUARRY_UPGRADES);
        if (quarryUpgrades != null) {
            this.speedUpgrade = quarryUpgrades.speedUpgrade();
            this.redstoneControl = quarryUpgrades.redstoneControl();
            this.fortuneLevel = quarryUpgrades.fortuneLevel();
            this.silkTouch = quarryUpgrades.silkTouch();
            this.extraEnergyCost = quarryUpgrades.extraEnergyCost();
            this.filterRows = quarryUpgrades.filterRows();
        }

        List<BlockPos> stabilizers = componentInput.get(dev.willyelton.crystal_tools.common.components.DataComponents.QUARRY_BOUNDS);
        if (stabilizers != null) {
            this.setStabilizers(stabilizers);
        } else {
            // TODO: Some default thing?
        }

        QuarryData quarryData = componentInput.get(dev.willyelton.crystal_tools.common.components.DataComponents.QUARRY_DATA);
        if (quarryData != null) {
            this.miningAt = quarryData.miningAt();
            this.currentProgress = quarryData.currentProgress();
            this.miningState = quarryData.miningState();
            this.finished = quarryData.finished();
            this.waitingStacks = new ArrayList<>(quarryData.waitingStacks());
            this.energyStorage = new CrystalEnergyStorage(10000, getEnergyCost() * 2, 0, quarryData.currentEnergy());
            this.whitelist = quarryData.whitelist();
        } else {
            this.energyStorage = new CrystalEnergyStorage(10000, getEnergyCost() * 2, 0, 0);
        }

        QuarrySettings quarrySettings = componentInput.get(dev.willyelton.crystal_tools.common.components.DataComponents.QUARRY_SETTINGS);
        if (quarrySettings != null) {
            this.useDirt = quarrySettings.useDirt();
            this.silkTouchEnabled = quarrySettings.silkTouchEnabled();
            this.fortuneEnabled = quarrySettings.fortuneEnabled();
            this.autoOutputEnabled = quarrySettings.autoOutputEnabled();
            this.autoOutputAction.setDisabled(!this.autoOutputEnabled);
        }

        createAABB();
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        ContainerHelper.saveAllItems(valueOutput, this.storedItems);

        ContainerHelper.saveAllItems(valueOutput.child("FilterInventory"), this.filterItems);
        valueOutput.putInt("FilterRows", this.filterRows);
        valueOutput.putBoolean("Whitelist", this.whitelist);

        if (this.miningAt != null) {
            valueOutput.putLong("MiningAt", this.miningAt.asLong());
        }
        valueOutput.putFloat("CurrentProgress", this.currentProgress);
        if (!this.waitingStacks.isEmpty()) {
            valueOutput.store("WaitingStacks", ItemStack.OPTIONAL_CODEC.listOf(), this.waitingStacks);
        }
        valueOutput.putBoolean("Finished", this.finished);

        valueOutput.putBoolean("UseDirt", this.useDirt);
        valueOutput.putBoolean("SilkTouchEnabled", this.silkTouchEnabled);
        valueOutput.putBoolean("FortuneEnabled", this.fortuneEnabled);
        valueOutput.putBoolean("AutoOutputEnabled", this.autoOutputEnabled);

        valueOutput.putInt("MinX", this.minX);
        valueOutput.putInt("MaxX", this.maxX);
        valueOutput.putInt("MinZ", this.minZ);
        valueOutput.putInt("MaxZ", this.maxZ);
        valueOutput.putInt("MinY", this.minY);
        valueOutput.putInt("MaxY", this.maxY);

        valueOutput.putFloat("CenterX", this.centerX);
        valueOutput.putFloat("CenterY", this.centerY);
        valueOutput.putFloat("CenterZ", this.centerZ);

        valueOutput.putInt("Energy", this.energyStorage.getAmountAsInt());

        valueOutput.putFloat("SpeedUpgrade", this.speedUpgrade);
        valueOutput.putBoolean("RedstoneControl", this.redstoneControl);
        valueOutput.putInt("FortuneLevel", this.fortuneLevel);
        valueOutput.putBoolean("SilkTouch", this.silkTouch);
        valueOutput.putInt("ExtraEnergyCost", this.extraEnergyCost);

        if (this.stabilizerPositions != null) {
            valueOutput.store("StabilizerPositions", Codec.LONG.listOf(), this.stabilizerPositions.stream().mapToLong(BlockPos::asLong).boxed().toList());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        ItemContainerContents contents = ItemContainerContents.fromItems(this.storedItems);
        components.set(DataComponents.CONTAINER, contents);

        ItemContainerContents filterContents = ItemContainerContents.fromItems(this.filterItems);
        components.set(dev.willyelton.crystal_tools.common.components.DataComponents.FILTER_INVENTORY, filterContents);

        QuarryData quarryData = new QuarryData(miningAt == null ? BlockPos.ZERO : miningAt, currentProgress, miningState, finished, waitingStacks, energyStorage.getAmountAsInt(), whitelist);
        components.set(dev.willyelton.crystal_tools.common.components.DataComponents.QUARRY_DATA, quarryData);

        QuarryUpgrades quarryUpgrades = new QuarryUpgrades(speedUpgrade, redstoneControl, fortuneLevel, silkTouch, extraEnergyCost, filterRows);
        components.set(dev.willyelton.crystal_tools.common.components.DataComponents.QUARRY_UPGRADES, quarryUpgrades);

        QuarrySettings quarrySettings = new QuarrySettings(useDirt, silkTouchEnabled, fortuneEnabled, autoOutputEnabled);
        components.set(dev.willyelton.crystal_tools.common.components.DataComponents.QUARRY_SETTINGS, quarrySettings);

        components.set(dev.willyelton.crystal_tools.common.components.DataComponents.QUARRY_BOUNDS, stabilizerPositions);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        enchantTempItems(level);
    }

    @Override
    protected void addToExtraData(String key, float value) {
        if (MINING_SPEED.toString().equals(key)) {
            this.speedUpgrade += value;
            this.extraEnergyCost += Math.max(1, (int) (CrystalToolsConfig.QUARRY_SPEED_COST_INCREASE.get() * value));
            updateEnergyStorage();
        } else if (REDSTONE_CONTROL.toString().equals(key)) {
            this.redstoneControl = true;
        } else if (FORTUNE.toString().equals(key)) {
            this.fortuneLevel += (int) value;
            this.fortuneEnabled = !silkTouch || !silkTouchEnabled;
            this.enchantTempItems(level);
            this.extraEnergyCost += CrystalToolsConfig.QUARRY_FORTUNE_COST_INCREASE.get();
            updateEnergyStorage();
        } else if (SILK_TOUCH.toString().equals(key)) {
            this.silkTouch = true;
            this.silkTouchEnabled = fortuneLevel == 0 || !fortuneEnabled;
            this.enchantTempItems(level);
            this.extraEnergyCost += CrystalToolsConfig.QUARRY_SILK_TOUCH_COST_INCREASE.get();
            updateEnergyStorage();
        } else if (TRASH_FILTER.toString().equals(key)) {
            this.filterRows += (int) value;
        }
    }

    @Override
    protected void resetExtraSkills() {
        super.resetExtraSkills();

        this.speedUpgrade = 0;
        this.redstoneControl = false;
        this.fortuneLevel = 0;
        this.silkTouch = false;
        this.filterRows = 0;
        this.whitelist = true;
        this.extraEnergyCost = 0;
    }

    protected final ContainerData dataAccess = new LevelableContainerData(this) {
        @Override
        protected int getExtra(int index) {
            return switch (index) {
                case 3 -> energyStorage.getAmountAsInt();
                case 4 -> energyStorage.getCapacityAsInt();
                case 5 -> miningAt.getX();
                case 6 -> miningAt.getY();
                case 7 -> miningAt.getZ();
                case 8 -> whitelist ? 1 : 0;
                case 9 -> useDirt ? 1 : 0;
                case 10 -> silkTouchEnabled ? 1 : 0;
                case 11 -> fortuneEnabled ? 1 : 0;
                case 12 -> autoOutputEnabled ? 1 : 0;
                case 13 -> getEnergyCost();
                case 14 -> silkTouch ? 1 : 0;
                case 15 -> fortuneLevel;
                case 16 -> autoOutputAction.isActive() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        protected void setExtra(int index, int value) {
            switch (index) {
                case 8 -> whitelist = value == 1;
                case 9 -> useDirt = value == 1;
                case 10 -> silkTouchEnabled = value == 1;
                case 11 -> fortuneEnabled = value == 1;
                case 12 -> {
                    autoOutputEnabled = value == 1;
                    autoOutputAction.setDisabled(!autoOutputEnabled);
                }
            }
        }

        @Override
        protected int getExtraDataSize() {
            return CrystalQuarryBlockEntity.DATA_SIZE;
        }
    };

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        boolean hasRedstone = level.hasNeighborSignal(pos);
        if (hasRedstone && redstoneControl) {
            if (state.getValue(CrystalQuarryBlock.ACTIVE)) {
                state = state.setValue(CrystalQuarryBlock.ACTIVE, false);
                level.setBlock(pos, state, 3);
            }
            return;
        }

        if (finished) return;

        if (miningAt == null) return;

        super.serverTick(level, pos, state);

        if (!waitingStacks.isEmpty()) {
            if (level.getGameTime() % 20 == 0) {
                // TODO (TRANSFER)
                List<ItemStack> noFit = new ArrayList<>(InventoryUtils.tryInsertStacks(ItemResourceHandlerAdapterModifiable.of(itemHandler), waitingStacks, this::shouldPickUp));

                if (!noFit.isEmpty()) {
                    waitingStacks = noFit;
                    return;
                } else {
                    waitingStacks.clear();
                }
            } else {
                return;
            }
        }

        if (!level.isLoaded(miningAt)) {
            if (this.chunkLoadingAction.isActive()) {
                this.chunkLoadingAction.loadChunk((ServerLevel) level, new ChunkPos(miningAt));
            } else {
                return;
            }
        }

        int energyCost = getEnergyCost();
        if (energyStorage.getAmountAsInt() >= energyCost) {
            energyStorage.removeEnergy(energyCost);
            if (!state.getValue(CrystalQuarryBlock.ACTIVE)) {
                state = state.setValue(CrystalQuarryBlock.ACTIVE, true);
                level.setBlock(pos, state, 3);
            }
            setChanged();
        } else {
            if (state.getValue(CrystalQuarryBlock.ACTIVE)) {
                state = state.setValue(CrystalQuarryBlock.ACTIVE, false);
                level.setBlock(pos, state, 3);
            }

            return;
        }

        if (miningState == null) {
            // Find new state
            int blocksThisTick = 0;
            while (!finished && blocksThisTick < MAX_BLOCKS_PER_TICK) {
                if (canMine(level.getBlockState(miningAt))) {
                    miningState = level.getBlockState(miningAt);
                    if (!miningState.getFluidState().isEmpty()) {
                        // TODO: Tank option later
                        BlockState blockState = useDirt ? Blocks.DIRT.defaultBlockState() : Blocks.AIR.defaultBlockState();
                        level.setBlock(miningAt, blockState, 3);
                        blocksThisTick++;
                        miningState = null;
                        nextPosition();
                        continue;
                    }

                    PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, new ChunkPos(miningAt), new QuarryMineBlockPayload(this.getBlockPos(), this.miningAt, this.miningState));
                    break;
                } else {
                    blocksThisTick++;
                    nextPosition();
                }
            }

            if (miningState == null) {
                return;
            }
        }

        // Keep mining
        currentProgress += getDestroyProgress(miningState, level, miningAt);

        if (level.getGameTime() % 2 == 0) {
            spawnBreakingParticles((ServerLevel) level);
        }

        // TODO: Maybe try to break multiple blocks in a tick later?
        if (currentProgress >= 10) {
            if (miningState.equals(level.getBlockState(miningAt))) {
                ItemStack miningStack = ItemStack.EMPTY;
                if (fortuneLevel > 0 && fortuneEnabled) {
                    miningStack = FORTUNE_STACK;
                } else if (silkTouch && silkTouchEnabled) {
                    miningStack = SILK_TOUCH_STACK;
                }

                List<ItemStack> drops = new ArrayList<>(Block.getDrops(level.getBlockState(miningAt), (ServerLevel) level, miningAt, level.getBlockEntity(miningAt), null, miningStack));
                drops.addAll(getInventoryContents(level));
                List<ItemStack> noFit = new ArrayList<>(InventoryUtils.tryInsertStacks(ItemResourceHandlerAdapterModifiable.of(itemHandler), drops, this::shouldPickUp));

                if (!noFit.isEmpty()) {
                    this.waitingStacks = noFit;
                }

                level.destroyBlock(miningAt, false, null);
                if (useDirt) {
                    level.setBlock(miningAt, Blocks.DIRT.defaultBlockState(), 3);
                }

                addExp(1);

                nextPosition();
                setChanged();
            }

            miningState = null;
            currentProgress = 0;
        } else {
            // Don't need to do this for instamines
            level.destroyBlockProgress(-1, miningAt, (int) currentProgress);
        }
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return itemHandler;
    }

    public ResourceHandler<ItemResource> getFilterItemHandler() {
        return filterItemHandler;
    }

    public int getFilterRows() {
        return filterRows;
    }

    // TODO: do I need to store all of the stabilizer positions to check if they are broken?
    public void setStabilizers(List<BlockPos> positions) {
        setPositions(positions.get(0), positions.get(2).atY(-64));
        this.stabilizerPositions = positions;
        createAABB();
    }

    private void setPositions(BlockPos topCorner, BlockPos bottomCorner) {
        this.minX = Math.min(topCorner.getX(), bottomCorner.getX()) + 1;
        this.maxX = Math.max(topCorner.getX(), bottomCorner.getX()) - 1;
        this.minZ = Math.min(topCorner.getZ(), bottomCorner.getZ()) + 1;
        this.maxZ = Math.max(topCorner.getZ(), bottomCorner.getZ()) - 1;
        this.minY = Math.min(topCorner.getY(), bottomCorner.getY());
        this.maxY = Math.max(topCorner.getY(), bottomCorner.getY());

        this.miningAt = new BlockPos(minX, maxY, minZ);

        this.centerX = (maxX - minX ) / 2.0F + minX;
        this.centerY = maxY + 3;
        this.centerZ = (maxZ - minZ) / 2.0F + minZ;
    }

    private int getEnergyCost() {
        int cost = CrystalToolsConfig.QUARRY_BASE_ENERGY_COST.get() + this.extraEnergyCost;
        if (silkTouch && !silkTouchEnabled) {
            cost -= CrystalToolsConfig.QUARRY_SILK_TOUCH_COST_INCREASE.get();
        }

        if (fortuneLevel > 0 && !fortuneEnabled) {
            cost -= CrystalToolsConfig.QUARRY_FORTUNE_COST_INCREASE.get();
        }

        return cost;
    }

    private boolean canMine(BlockState state) {
        return !state.isAir() && state.getDestroySpeed(level, worldPosition) >= 0;
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
                    this.chunkLoadingAction.unloadAll((ServerLevel) level);
                    BlockState state = getBlockState().setValue(CrystalQuarryBlock.ACTIVE, false);
                    level.setBlock(getBlockPos(), state, 3);
                }
            }
        }

        miningAt = new BlockPos(currentX, currentY, currentZ);
    }

    private float getDestroyProgress(BlockState state, BlockGetter level, BlockPos pos) {
        float f = state.getDestroySpeed(level, pos);
        if (f == -1.0F) {
            return 0.0F;
        } else {
            // Eff V netherite is 234
            // Base netherite is 9
            return (float) ((9F + speedUpgrade * CrystalToolsConfig.QUARRY_SPEED_UPGRADE_MULTIPLIER.get()) / f / 30.0F);
        }
    }

    private List<ItemStack> getInventoryContents(Level level) {
        List<ItemStack> stacks = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            // TODO (TRANSFER)
            ResourceHandler<ItemResource> newHandler = level.getCapability(Capabilities.Item.BLOCK, miningAt, miningState, level.getBlockEntity(miningAt), direction);
            IItemHandler itemHandler = newHandler == null ? null : ItemResourceHandlerAdapterModifiable.of(newHandler);

            if (itemHandler != null) {
                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    ItemStack storedStack = itemHandler.getStackInSlot(i);
                    if (!storedStack.isEmpty()) {
                        stacks.add(itemHandler.extractItem(i, storedStack.getCount(), false));
                    }
                }
            }
        }

        return stacks;
    }

    private List<ItemStack> tryInsertStacks(List<ItemStack> stacksToInsert) {
        List<ItemStack> noFit = new ArrayList<>();

        for (ItemStack stack : stacksToInsert) {
            if (shouldPickUp(stack)) {
                continue;
            }
            // TODO (TRANSFER)
            ItemStack result = ItemHandlerHelper.insertItem(ItemResourceHandlerAdapterModifiable.of(this.itemHandler), stack, false);

            if (!result.isEmpty()) {
                noFit.add(result);
            }
        }

        return noFit;
    }

    private void enchantTempItems(Level level) {
        level.registryAccess().lookup(Registries.ENCHANTMENT).ifPresent((enchantments) -> {
            FORTUNE_STACK.enchant(enchantments.getOrThrow(Enchantments.FORTUNE), this.fortuneLevel);
            SILK_TOUCH_STACK.enchant(enchantments.getOrThrow(Enchantments.SILK_TOUCH), 1);
        });
    }

    private boolean shouldPickUp(ItemStack stack) {
        for (ItemStack filterStack : filterItems) {
            // TODO: Mode matching
            if (filterStack.is(stack.getItem())) {
                return !whitelist;
            }
        }

        return whitelist;
    }

    @Override
    public Map<Integer, ItemStack> getOutputStacks() {
        HashMap<Integer, ItemStack> items = new HashMap<>();

        for (int i = 0; i < itemHandler.size(); i++) {
            if (!itemHandler.getResource(i).isEmpty()) {
                items.put(i, ItemUtil.getStack(itemHandler, i));
            }
        }
        return items;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        itemHandler.set(slot, ItemResource.of(stack), stack.getCount());
    }

    @Override
    protected int getBaseExpCap() {
        return CrystalToolsConfig.QUARRY_BASE_EXPERIENCE_CAP.get();
    }

    private void updateEnergyStorage() {
        this.energyStorage.maxInsert(getEnergyCost() * 2);
    }

    // ------------------ Things used for block entity renderer ------------------
    public BlockPos getMiningAt() {
        return this.miningAt;
    }

    public void setMiningAt(BlockPos miningAt) {
        this.miningAt = miningAt;
    }

    public void setMiningState(BlockState miningState) {
        this.miningState = miningState;
    }

    public List<BlockPos> getStabilizerPositions() {
        List<BlockPos> result = new ArrayList<>();

        result.add(new BlockPos(minX - 1, maxY, minZ - 1));
        result.add(new BlockPos(minX - 1, maxY, maxZ + 1));
        result.add(new BlockPos(maxX + 1, maxY, maxZ + 1));
        result.add(new BlockPos(maxX + 1, maxY, minZ - 1));

        return result;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getCenterZ() {
        return centerZ;
    }

    public boolean isFinished() {
        return finished;
    }

    private void createAABB() {
        this.aabb = AABB.encapsulatingFullBlocks(stabilizerPositions.get(0).atY(-64), stabilizerPositions.get(2).above(4));
    }

    public AABB getAABB() {
        return aabb;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    private void spawnBreakingParticles(ServerLevel level) {
        BlockPos pos = getBlockPos();
        level.sendParticles(new QuarryBreakParticleData(miningState, miningAt, pos), pos.getX(),
                pos.getY(), pos.getZ(), 1, 0.25, 0.25, 0.25, 0.1);
    }

    @Override
    public boolean shouldUnload() {
        return this.finished || this.isRemoved();
    }
}

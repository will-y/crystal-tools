package dev.willyelton.crystal_tools.common.levelable.block.entity;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.client.particle.quarry.breakblock.QuarryBreakParticleData;
import dev.willyelton.crystal_tools.common.components.QuarryData;
import dev.willyelton.crystal_tools.common.components.QuarrySettings;
import dev.willyelton.crystal_tools.common.components.QuarryUpgrades;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.energy.CrystalEnergyStorage;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalQuarryContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalQuarryBlock;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.AutoOutputAction;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.AutoOutputable;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.ChunkLoader;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.ChunkLoadingAction;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.LevelableContainerData;
import dev.willyelton.crystal_tools.common.network.data.QuarryMineBlockPayload;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
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
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrystalQuarryBlockEntity extends LevelableBlockEntity implements MenuProvider, AutoOutputable, ChunkLoader {
    public static final int DATA_SIZE = 14;

    private static final int INVENTORY_SIZE = 27;
    private static final int MAX_BLOCKS_PER_TICK = 20;

    // Itemstacks for mocking fortune and silk touch
    private static final ItemStack FORTUNE_STACK = new ItemStack(Registration.CRYSTAL_AIOT);
    private static final ItemStack SILK_TOUCH_STACK = new ItemStack(Registration.CRYSTAL_AIOT);

    // Item storage
    private final NonNullList<ItemStack> storedItems;
    private final IItemHandlerModifiable itemHandler;

    // Filter Things
    private final NonNullList<ItemStack> filterItems;
    private final IItemHandlerModifiable filterItemHandler;
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

    private final AutoOutputAction autoOutputAction;
    private final ChunkLoadingAction<CrystalQuarryBlockEntity> chunkLoadingAction;

    public CrystalQuarryBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.CRYSTAL_QUARRY_BLOCK_ENTITY.get(), pos, state);
        storedItems = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

        useDirt = false;

        // Caps
        itemHandler = new ItemStackHandler(storedItems);
        energyStorage = new CrystalEnergyStorage(10000, getEnergyCost() * 2, 0, 0);

        // Filter
        filterItems = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
        filterItemHandler = new ItemStackHandler(filterItems);

        autoOutputAction = addAction(new AutoOutputAction(this));
        chunkLoadingAction = addAction(new ChunkLoadingAction<>(this));
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
        return new CrystalQuarryContainerMenu(containerId, player.level(), this.getBlockPos(), this.filterRows, playerInventory, this.dataAccess);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, this.storedItems, registries);

        if (tag.contains("FilterInventory")) {
            ContainerHelper.loadAllItems(tag.getCompound("FilterInventory"), this.filterItems, registries);
        }
        this.filterRows = tag.getInt("FilterRows");
        this.whitelist = tag.getBoolean("Whitelist");

        if (tag.contains("MiningAt")) {
            this.miningAt = BlockPos.of(tag.getLong("MiningAt"));
        }
        this.currentProgress = tag.getFloat("CurrentProgress");
        if (tag.contains("WaitingStacks")) {
            this.waitingStacks = NBTUtils.getItemStackArray(tag.getCompound("WaitingStacks"), registries);
        } else {
            this.waitingStacks = new ArrayList<>();
        }
        this.finished = tag.getBoolean("Finished");

        this.useDirt = tag.getBoolean("UseDirt");
        this.silkTouchEnabled = tag.getBoolean("SilkTouchEnabled");
        this.fortuneEnabled = tag.getBoolean("FortuneEnabled");
        this.autoOutputEnabled = tag.getBoolean("AutoOutputEnabled");

        this.minX = tag.getInt("MinX");
        this.maxX = tag.getInt("MaxX");
        this.minZ = tag.getInt("MinZ");
        this.maxZ = tag.getInt("MaxZ");
        this.minY = tag.getInt("MinY");
        this.maxY = tag.getInt("MaxY");

        this.centerX = tag.getFloat("CenterX");
        this.centerY = tag.getFloat("CenterY");
        this.centerZ = tag.getFloat("CenterZ");

        this.speedUpgrade = tag.getFloat("SpeedUpgrade");
        this.redstoneControl = tag.getBoolean("RedstoneControl");
        this.fortuneLevel = tag.getInt("FortuneLevel");
        this.silkTouch = tag.getBoolean("SilkTouch");
        this.extraEnergyCost = tag.getInt("ExtraEnergyCost");

        int energy = tag.getInt("Energy");
        energyStorage = new CrystalEnergyStorage(10000, getEnergyCost() * 2, 0, energy);

        if (tag.contains("StabilizerPositions")) {
            this.stabilizerPositions = Arrays.stream(tag.getLongArray("StabilizerPositions")).mapToObj(BlockPos::of).toList();
        }

        createAABB();
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);

        ItemContainerContents contents = componentInput.get(DataComponents.CONTAINER);
        if (contents != null) {
            contents.copyInto(this.storedItems);
        }

        ItemContainerContents filterContents = componentInput.get(dev.willyelton.crystal_tools.common.components.DataComponents.QUARRY_FILTER);
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
        }

        QuarryData quarryData = componentInput.get(dev.willyelton.crystal_tools.common.components.DataComponents.QUARRY_DATA);
        if (quarryData != null) {
            this.miningAt = quarryData.miningAt();
            this.currentProgress = quarryData.currentProgress();
            this.miningState = quarryData.miningState();
            this.finished = quarryData.finished();
            this.waitingStacks = quarryData.waitingStacks();
            this.energyStorage = new CrystalEnergyStorage(10000, getEnergyCost() * 2, 0, quarryData.currentEnergy());
        }

        QuarrySettings quarrySettings = componentInput.get(dev.willyelton.crystal_tools.common.components.DataComponents.QUARRY_SETTINGS);
        if (quarrySettings != null) {
            this.useDirt = quarrySettings.useDirt();
            this.silkTouchEnabled = quarrySettings.silkTouchEnabled();
            this.fortuneEnabled = quarrySettings.fortuneEnabled();
            this.autoOutputEnabled = quarrySettings.autoOutputEnabled();
        }

        List<BlockPos> stabilizers = componentInput.get(dev.willyelton.crystal_tools.common.components.DataComponents.QUARRY_BOUNDS);
        if (stabilizers != null) {
            this.setStabilizers(stabilizers);
        } else {
            // TODO: Some default thing?
        }

        createAABB();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.storedItems, registries);

        tag.put("FilterInventory", new CompoundTag());
        ContainerHelper.saveAllItems(tag.getCompound("FilterInventory"), this.filterItems, registries);
        tag.putInt("FilterRows", this.filterRows);
        tag.putBoolean("Whitelist", this.whitelist);

        if (this.miningAt != null) {
            tag.putLong("MiningAt", this.miningAt.asLong());
        }
        tag.putFloat("CurrentProgress", this.currentProgress);
        if (!this.waitingStacks.isEmpty()) {
            tag.put("WaitingStacks", new CompoundTag());
            NBTUtils.storeItemStackArray(tag.getCompound("WaitingStacks"), this.waitingStacks, registries);
        }
        tag.putBoolean("Finished", this.finished);

        tag.putBoolean("UseDirt", this.useDirt);
        tag.putBoolean("SilkTouchEnabled", this.silkTouchEnabled);
        tag.putBoolean("FortuneEnabled", this.fortuneEnabled);
        tag.putBoolean("AutoOutputEnabled", this.autoOutputEnabled);

        tag.putInt("MinX", this.minX);
        tag.putInt("MaxX", this.maxX);
        tag.putInt("MinZ", this.minZ);
        tag.putInt("MaxZ", this.maxZ);
        tag.putInt("MinY", this.minY);
        tag.putInt("MaxY", this.maxY);

        tag.putFloat("CenterX", this.centerX);
        tag.putFloat("CenterY", this.centerY);
        tag.putFloat("CenterZ", this.centerZ);

        tag.putInt("Energy", this.energyStorage.getEnergyStored());

        tag.putFloat("SpeedUpgrade", this.speedUpgrade);
        tag.putBoolean("RedstoneControl", this.redstoneControl);
        tag.putInt("FortuneLevel", this.fortuneLevel);
        tag.putBoolean("SilkTouch", this.silkTouch);
        tag.putInt("ExtraEnergyCost", this.extraEnergyCost);

        if (this.stabilizerPositions != null) {
            tag.putLongArray("StabilizerPositions", this.stabilizerPositions.stream().map(BlockPos::asLong).mapToLong(Long::longValue).toArray());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        ItemContainerContents contents = ItemContainerContents.fromItems(this.storedItems);
        components.set(DataComponents.CONTAINER, contents);

        ItemContainerContents filterContents = ItemContainerContents.fromItems(this.filterItems);
        components.set(dev.willyelton.crystal_tools.common.components.DataComponents.QUARRY_FILTER, filterContents);

        QuarryData quarryData = new QuarryData(miningAt == null ? BlockPos.ZERO : miningAt, currentProgress, miningState, finished, waitingStacks, energyStorage.getEnergyStored());
        components.set(dev.willyelton.crystal_tools.common.components.DataComponents.QUARRY_DATA, quarryData);

        QuarryUpgrades quarryUpgrades = new QuarryUpgrades(speedUpgrade, redstoneControl, fortuneLevel, silkTouch, extraEnergyCost);
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
        switch (key) {
            case "quarry_speed" -> {
                this.speedUpgrade += value;
                this.extraEnergyCost += Math.max(1, (int) (CrystalToolsConfig.QUARRY_SPEED_COST_INCREASE.get() * value));
                updateEnergyStorage();
            }
            case "redstone_control" -> this.redstoneControl = true;
            case "fortune" -> {
                this.fortuneLevel += (int) value;
                this.fortuneEnabled = !silkTouch || !silkTouchEnabled;
                this.enchantTempItems(level);
                this.extraEnergyCost += CrystalToolsConfig.QUARRY_FORTUNE_COST_INCREASE.get();
                updateEnergyStorage();
            }
            case "silk_touch" -> {
                this.silkTouch = true;
                this.silkTouchEnabled = fortuneLevel == 0 || !fortuneEnabled;
                this.enchantTempItems(level);
                this.extraEnergyCost += CrystalToolsConfig.QUARRY_SILK_TOUCH_COST_INCREASE.get();
                updateEnergyStorage();
            }
            case "filter_capacity" -> this.filterRows += (int) value;
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
                case 3 -> energyStorage.getEnergyStored();
                case 4 -> energyStorage.getMaxEnergyStored();
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
                case 12 -> autoOutputEnabled = value == 1;
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
                List<ItemStack> noFit = tryInsertStacks(waitingStacks);

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
        if (energyStorage.getEnergyStored() >= energyCost) {
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
                // TODO: Setting
                if (fortuneLevel > 0 && fortuneEnabled) {
                    miningStack = FORTUNE_STACK;
                } else if (silkTouch && silkTouchEnabled) {
                    miningStack = SILK_TOUCH_STACK;
                }

                List<ItemStack> drops = Block.getDrops(level.getBlockState(miningAt), (ServerLevel) level, miningAt, level.getBlockEntity(miningAt), null, miningStack);
                drops.addAll(getInventoryContents(level));
                List<ItemStack> noFit = tryInsertStacks(drops);

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

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    public IItemHandlerModifiable getFilterItemHandler() {
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
        return CrystalToolsConfig.QUARRY_BASE_ENERGY_COST.get() + this.extraEnergyCost;
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
            // TODO: Config for this 20
            return (9F + speedUpgrade * 20) / f / 30.0F;
        }
    }

    private List<ItemStack> getInventoryContents(Level level) {
        List<ItemStack> stacks = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, miningAt, miningState, level.getBlockEntity(miningAt), direction);

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
            if (matchesFilter(stack)) {
                continue;
            }
            ItemStack result = ItemHandlerHelper.insertItem(this.itemHandler, stack, false);

            if (!result.isEmpty()) {
                noFit.add(result);
            }
        }

        return noFit;
    }

    private void enchantTempItems(Level level) {
        level.registryAccess().registry(Registries.ENCHANTMENT).ifPresent((enchantments) -> {
            FORTUNE_STACK.enchant(enchantments.getHolderOrThrow(Enchantments.FORTUNE), this.fortuneLevel);
            SILK_TOUCH_STACK.enchant(enchantments.getHolderOrThrow(Enchantments.SILK_TOUCH), 1);
        });
    }

    private boolean matchesFilter(ItemStack stack) {
        for (ItemStack filterStack : filterItems) {
            // TODO: Mode matching
            if (filterStack.is(stack.getItem())) {
                return whitelist;
            }
        }

        return !whitelist;
    }

    @Override
    public Map<Integer, ItemStack> getOutputStacks() {
        HashMap<Integer, ItemStack> items = new HashMap<>();

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                items.put(i, itemHandler.getStackInSlot(i));
            }
        }
        return items;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        itemHandler.setStackInSlot(slot, stack);
    }

    @Override
    protected int getBaseExpCap() {
        return CrystalToolsConfig.QUARRY_BASE_EXPERIENCE_CAP.get();
    }

    private void updateEnergyStorage() {
        this.energyStorage.setMaxReceive(getEnergyCost() * 2);
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

package dev.willyelton.crystal_tools.common.levelable.block.entity;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.energy.CrystalEnergyStorage;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalQuarryContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.AutoOutputAction;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.AutoOutputable;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.LevelableContainerData;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrystalQuarryBlockEntity extends LevelableBlockEntity implements MenuProvider, AutoOutputable {
    public static final int DATA_SIZE = 6;

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

    // Config
    private final int baseFEUsage;
    private final boolean useDirt;

    // Upgrades
    private int speedUpgrade = 0;
    private boolean autoOutput = false;
    private boolean redstoneControl = false;
    private int fortuneLevel = 0;
    private boolean silkTouch = false;

    // Quarry things
    private int tickCounter = 0;
    private int currentSpeed = 40;
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

    private final AutoOutputAction autoOutputAction;

    public CrystalQuarryBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.CRYSTAL_QUARRY_BLOCK_ENTITY.get(), pos, state);
        storedItems = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

        // TODO: Config
        baseFEUsage = 50;
        useDirt = false;

        // Caps
        itemHandler = new ItemStackHandler(storedItems);
        energyStorage = new CrystalEnergyStorage(10000, baseFEUsage * 2, 0, 0);

        // Filter
        filterItems = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
        filterItemHandler = new ItemStackHandler(filterItems);

        autoOutputAction = new AutoOutputAction(this);
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
        return new CrystalQuarryContainerMenu(containerId, player.level(), this.getBlockPos(), this.filterRows, playerInventory, this.dataAccess);
    }

    // TODO
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, this.storedItems, registries);

        if (tag.contains("FilterInventory")) {
            ContainerHelper.loadAllItems(tag.getCompound("FilterInventory"), this.filterItems, registries);
        }
        this.filterRows = tag.getInt("FilterRows");
        this.whitelist = tag.getBoolean("Whitelist");

        this.tickCounter = tag.getInt("TickCounter");
        this.currentSpeed = tag.getInt("CurrentSpeed");
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

        this.minX = tag.getInt("MinX");
        this.maxX = tag.getInt("MaxX");
        this.minZ = tag.getInt("MinZ");
        this.maxZ = tag.getInt("MaxZ");
        this.minY = tag.getInt("MinY");
        this.maxY = tag.getInt("MaxY");

        int energy = tag.getInt("Energy");
        energyStorage = new CrystalEnergyStorage(10000, baseFEUsage * 2, 0, energy);

        this.speedUpgrade = tag.getInt("SpeedUpgrade");
        this.autoOutput = tag.getBoolean("AutoOutput");
        this.redstoneControl = tag.getBoolean("RedstoneControl");
        this.fortuneLevel = tag.getInt("FortuneLevel");
        this.silkTouch = tag.getBoolean("SilkTouch");
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

        tag.put("FilterInventory", new CompoundTag());
        ContainerHelper.saveAllItems(tag.getCompound("FilterInventory"), this.filterItems, registries);
        tag.putInt("FilterRows", this.filterRows);
        tag.putBoolean("Whitelist", this.whitelist);

        tag.putInt("TickCounter", this.tickCounter);
        tag.putInt("CurrentSpeed", this.currentSpeed);
        if (this.miningAt != null) {
            tag.putLong("MiningAt", this.miningAt.asLong());
        }
        tag.putFloat("CurrentProgress", this.currentProgress);
        if (!this.waitingStacks.isEmpty()) {
            tag.put("WaitingStacks", new CompoundTag());
            NBTUtils.storeItemStackArray(tag.getCompound("WaitingStacks"), this.waitingStacks, registries);
        }
        tag.putBoolean("Finished", this.finished);

        tag.putInt("MinX", this.minX);
        tag.putInt("MaxX", this.maxX);
        tag.putInt("MinZ", this.minZ);
        tag.putInt("MaxZ", this.maxZ);
        tag.putInt("MinY", this.minY);
        tag.putInt("MaxY", this.maxY);

        tag.putInt("Energy", this.energyStorage.getEnergyStored());

        tag.putInt("SpeedUpgrade", this.speedUpgrade);
        tag.putBoolean("AutoOutput", this.autoOutput);
        tag.putBoolean("RedstoneControl", this.redstoneControl);
        tag.putInt("FortuneLevel", this.fortuneLevel);
        tag.putBoolean("SilkTouch", this.silkTouch);
    }

    // TODO
    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        enchantTempItems(level);
    }

    // TODO
    @Override
    protected void addToExtraData(String key, float value) {
        switch (key) {
            case "quarry_speed" -> this.speedUpgrade += (int) value;
            case "auto_output" -> this.autoOutput = true;
            case "redstone_control" -> this.redstoneControl = true;
            case "fortune" -> {
                this.fortuneLevel += (int) value;
                this.enchantTempItems(level);
            }
            case "silk_touch" -> {
                this.silkTouch = true;
                this.enchantTempItems(level);
            }
            case "filter_capacity" -> this.filterRows += (int) value;
        }
    }

    // TODO
    @Override
    protected void resetExtraSkills() {
        this.speedUpgrade = 0;
        this.autoOutput = false;
        this.redstoneControl = false;
        this.fortuneLevel = 0;
        this.silkTouch = false;
        this.filterRows = 0;
        this.whitelist = true;
    }

    // TODO
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
                default -> 0;
            };
        }

        @Override
        protected void setExtra(int index, int value) {
            switch (index) {
                case 8 -> whitelist = value == 1;
            }
        }

        @Override
        protected int getExtraDataSize() {
            return CrystalQuarryBlockEntity.DATA_SIZE;
        }
    };

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        boolean hasRedstone = level.hasNeighborSignal(pos);
        if (hasRedstone && redstoneControl) {
            return;
        }

        if (finished) return;

        if (miningAt == null) return;

        autoOutputAction.tick(level, pos, state);

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

        if (energyStorage.getEnergyStored() >= 40) {
            energyStorage.removeEnergy(40);
            setChanged();
        } else {
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
                        level.setBlock(miningAt, Blocks.AIR.defaultBlockState(), 3);
                        blocksThisTick++;
                        nextPosition();
                        continue;
                    }
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

        // TODO: Maybe try to break multiple blocks in a tick later?
        if (currentProgress >= 10) {
            if (miningState.equals(level.getBlockState(miningAt))) {
                ItemStack miningStack = ItemStack.EMPTY;
                // TODO: Setting
                if (fortuneLevel > 0) {
                    miningStack = FORTUNE_STACK;
                } else if (silkTouch) {
                    miningStack = SILK_TOUCH_STACK;
                }

                List<ItemStack> drops = Block.getDrops(level.getBlockState(miningAt), (ServerLevel) level, miningAt, level.getBlockEntity(miningAt), null, miningStack);
                // TODO: Will this be too slow or is it fine to check for all blocks?
                drops.addAll(getInventoryContents(level));
                List<ItemStack> noFit = tryInsertStacks(drops);

                if (!noFit.isEmpty()) {
                    this.waitingStacks = noFit;
                }

                level.destroyBlock(miningAt, false, null);
                if (useDirt) {
                    level.setBlock(miningAt, Blocks.DIRT.defaultBlockState(), 3);
                }

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

    public void setWhitelist(boolean whitelist) {
        this.whitelist = whitelist;
    }

    public boolean getWhitelist() {
        return whitelist;
    }

    public int getFilterRows() {
        return filterRows;
    }

    // TODO: do I need to store all of the stabilizer positions to check if they are broken?
    public void setPositions(BlockPos topCorner, BlockPos bottomCorner) {
        this.minX = Math.min(topCorner.getX(), bottomCorner.getX()) + 1;
        this.maxX = Math.max(topCorner.getX(), bottomCorner.getX()) - 1;
        this.minZ = Math.min(topCorner.getZ(), bottomCorner.getZ()) + 1;
        this.maxZ = Math.max(topCorner.getZ(), bottomCorner.getZ()) - 1;
        this.minY = Math.min(topCorner.getY(), bottomCorner.getY());
        this.maxY = Math.max(topCorner.getY(), bottomCorner.getY());

        this.miningAt = new BlockPos(minX, maxY, minZ);
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
    public boolean autoOutputEnabled() {
        return autoOutput;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        itemHandler.setStackInSlot(slot, stack);
    }
}

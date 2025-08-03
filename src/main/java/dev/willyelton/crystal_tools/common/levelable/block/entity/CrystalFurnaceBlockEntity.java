package dev.willyelton.crystal_tools.common.levelable.block.entity;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.components.FurnaceData;
import dev.willyelton.crystal_tools.common.components.FurnaceUpgrades;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalFurnaceContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalFurnaceBlock;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.Action;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.AutoOutputAction;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.AutoOutputable;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.LevelableContainerData;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.*;

// TODO: Audit / comment code, don't use WordlyContainer, extract out things that will be common to a generator / other
public class CrystalFurnaceBlockEntity extends LevelableBlockEntity implements WorldlyContainer, MenuProvider, AutoOutputable {
    public static final int[] INPUT_SLOTS = new int[] {0, 1, 2, 3, 4};
    public static final int[] OUTPUT_SLOTS = new int[] {5, 6, 7, 8, 9};
    public static final int[] FUEL_SLOTS = new int[] {10, 11, 12};
    public static final int MAX_SLOTS = 5;

    public static final int SIZE = 13;
    public static final int DATA_SIZE = 14;

    public static final List<String> NBT_TAGS = new ArrayList<>(List.of("SpeedUpgrade", "FuelEfficiencyUpgrade", "Slots", "FuelSlots", "Balance", "AutoOutput", "ExpModifier", "Items"));

    static {
        NBT_TAGS.addAll(LevelableBlockEntity.NBT_TAGS);
    }

    // Config
    private final int fuelEfficiencyAddedTicks;
    private final int speedUpgradeSubtractTicks;
    private final double expBoostPercentage;

    private NonNullList<ItemStack> items;

    private final Map<Direction, IItemHandler> sidedCaps;
    private final RecipeType<? extends AbstractCookingRecipe> recipeType = RecipeType.SMELTING;

    // Furnace related fields
    private int litTime = 0;
    private int litTotalTime = 0;
    private int[] cookingProgress = new int[MAX_SLOTS];
    private int[] cookingTotalTime = new int[MAX_SLOTS];
    private float expHeld = 0;

    // Things that can be upgraded
    private float speedUpgrade = 0;
    private int fuelEfficiencyUpgrade = 0;
    private int bonusSlots = 0;
    private int bonusFuelSlots = 0;
    private boolean balance = false;
    private float expModifier;
    private boolean saveFuel = false;

    private AutoOutputAction autoOutputAction;

    public CrystalFurnaceBlockEntity(BlockPos pPos, BlockState state) {
        super(Registration.CRYSTAL_FURNACE_BLOCK_ENTITY.get(), pPos, state);
        items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

        fuelEfficiencyAddedTicks = CrystalToolsConfig.FUEL_EFFICIENCY_ADDED_TICKS.get();
        speedUpgradeSubtractTicks = CrystalToolsConfig.SPEED_UPGRADE_SUBTRACT_TICKS.get();
        expBoostPercentage = CrystalToolsConfig.EXPERIENCE_BOOST_PERCENTAGE.get();
        sidedCaps = Arrays.stream(new Direction[] {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST})
                .map(direction -> Map.entry(direction, new SidedInvWrapper(this, direction)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    protected Collection<Action> getDefaultActions() {
        autoOutputAction = new AutoOutputAction(this, this);
        return List.of(autoOutputAction);
    }

    public IItemHandler getCapForSide(Direction side) {
        return this.sidedCaps.get(side);
    }

    @Override
    public int[] getSlotsForFace(Direction face) {
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
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, Direction pDirection) {
        return canPlaceItem(pIndex, pItemStack);
    }

    public boolean canPlaceItem(int index, ItemStack stack) {
        if (ArrayUtils.arrayContains(INPUT_SLOTS, index)) {
            return index <= INPUT_SLOTS[this.bonusSlots];
        } else if (ArrayUtils.arrayContains(OUTPUT_SLOTS, index)) {
            return false;
        } else if (ArrayUtils.arrayContains(FUEL_SLOTS, index)) {
            return index <= FUEL_SLOTS[this.bonusFuelSlots] && (stack.getBurnTime(RecipeType.SMELTING) > 0);
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
        for (ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return this.items.get(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(items, pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(items, pSlot);
    }

    @Override
    public Map<Integer, ItemStack> getOutputStacks() {
        Map<Integer, ItemStack> items = new HashMap<>();

        for (int i : OUTPUT_SLOTS) {
            items.put(i, this.items.get(i));
        }

        return items;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack current = this.items.get(slot);
        this.items.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        if (ArrayUtils.arrayContains(INPUT_SLOTS, slot) && !(!stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, current))) {
            int index = ArrayUtils.indexOf(INPUT_SLOTS, slot);
            RecipeHolder<AbstractCookingRecipe> recipe = this.getRecipe(stack);
            if (recipe != null) {
                this.cookingTotalTime[index] = getTotalCookTime(recipe, index);
                this.cookingProgress[index] = 0;
                this.setChanged();
            }
        }

        if (ArrayUtils.arrayContains(FUEL_SLOTS, slot)) {
            if (this.getRecipe(stack) != null) {
                this.balanceFuel();
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (level != null && level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.crystal_tools.crystal_furnace");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CrystalFurnaceContainerMenu(containerId, player.level(), this.getBlockPos(), playerInventory, this.dataAccess);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);

        this.litTime = tag.getInt("LitTime");
        this.litTotalTime = tag.getInt("LitDuration");
        this.cookingProgress = NBTUtils.getIntArray(tag, "CookingProgress", 5);
        this.cookingTotalTime = NBTUtils.getIntArray(tag, "CookingTotalTime", 5);
        this.expHeld = tag.getFloat("ExpHeld");

        // Upgrade things
        this.speedUpgrade = tag.getFloat("SpeedUpgrade");
        this.fuelEfficiencyUpgrade = tag.getInt("FuelEfficiencyUpgrade");
        this.bonusSlots = tag.getInt("Slots");
        this.bonusFuelSlots = tag.getInt("FuelSlots");
        this.balance = tag.getBoolean("Balance");
        this.expModifier = tag.getFloat("ExpModifier");
        this.saveFuel = tag.getBoolean("SaveFuel");
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        ItemContainerContents contents = componentInput.get(DataComponents.FURNACE_INVENTORY);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        if (contents != null) {
            contents.copyInto(this.items);
        }

        FurnaceData furnaceData = componentInput.get(DataComponents.FURNACE_DATA);
        if (furnaceData != null) {
            this.litTime = furnaceData.litTime();
            this.litTotalTime = furnaceData.litDuration();
            this.cookingProgress = furnaceData.cookingProgress().stream().mapToInt(Integer::intValue).toArray();
            this.cookingTotalTime = furnaceData.cookingTime().stream().mapToInt(Integer::intValue).toArray();
            this.expHeld = furnaceData.expHeld();
        }

        FurnaceUpgrades furnaceUpgrades = componentInput.get(DataComponents.FURNACE_UPGRADES);
        if (furnaceUpgrades != null) {
            this.speedUpgrade = furnaceUpgrades.speed();
            this.fuelEfficiencyUpgrade = furnaceUpgrades.fuelEfficiency();
            this.bonusSlots = furnaceUpgrades.slots();
            this.bonusFuelSlots = furnaceUpgrades.fuelSlots();
            this.balance = furnaceUpgrades.balance();
            this.expModifier = furnaceUpgrades.expModifier();
            this.saveFuel = furnaceUpgrades.saveFuel();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.putInt("LitTime", this.litTime);
        // TODO (Breaking): Rename
        tag.putInt("LitDuration", this.litTotalTime);
        tag.putIntArray("CookingProgress", this.cookingProgress);
        tag.putIntArray("CookingTotalTime", this.cookingTotalTime);
        tag.putFloat("ExpHeld", this.expHeld);

        // Upgrade things
        tag.putFloat("SpeedUpgrade", this.speedUpgrade);
        tag.putInt("FuelEfficiencyUpgrade", this.fuelEfficiencyUpgrade);
        tag.putInt("Slots", this.bonusSlots);
        tag.putInt("FuelSlots", this.bonusFuelSlots);
        tag.putBoolean("Balance", this.balance);
        tag.putFloat("ExpModifier", this.expModifier);
        tag.putBoolean("SaveFuel", this.saveFuel);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        FurnaceData furnaceData = new FurnaceData(litTime, litTotalTime, Arrays.stream(cookingProgress).boxed().toList(),
                Arrays.stream(cookingTotalTime).boxed().toList(), expHeld);
        components.set(DataComponents.FURNACE_DATA, furnaceData);

        FurnaceUpgrades furnaceUpgrades = new FurnaceUpgrades(speedUpgrade, fuelEfficiencyUpgrade, bonusSlots,
                bonusFuelSlots, balance, expModifier, saveFuel);
        components.set(DataComponents.FURNACE_UPGRADES, furnaceUpgrades);

        ItemContainerContents contents = ItemContainerContents.fromItems(this.items);
        components.set(DataComponents.FURNACE_INVENTORY, contents);
    }

    @Override
    protected void addToExtraData(String key, float value) {
        switch (key) {
            case "speed_bonus" -> this.speedUpgrade += value;
            case "fuel_bonus" -> this.fuelEfficiencyUpgrade += (int) value;
            case "slot_bonus" -> this.bonusSlots += (int) value;
            case "fuel_slot_bonus" -> this.bonusFuelSlots += (int) value;
            case "auto_balance" -> this.balance = value == 1;
            case "exp_boost" -> this.expModifier += value;
            case "save_fuel" -> this.saveFuel = value == 1;
        }
    }

    @Override
    protected void resetExtraSkills() {
        if (this.level instanceof ServerLevel serverLevel) {
            // Drop items
            Containers.dropContents(serverLevel, this.getBlockPos(), this);
            this.popExp(serverLevel, Vec3.atCenterOf(this.getBlockPos()));

            // Set state to off
            BlockState state = level.getBlockState(this.getBlockPos());
            state = state.setValue(CrystalFurnaceBlock.LIT, false);
            level.setBlock(this.getBlockPos(), state, 3);
        }

        items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

        // Furnace things
        this.litTime = 0;
        this.litTotalTime = 0;
        this.cookingProgress = new int[MAX_SLOTS];
        this.cookingTotalTime = new int[MAX_SLOTS];
        this.expHeld = 0;

        // Upgrades
        this.speedUpgrade = 0;
        this.fuelEfficiencyUpgrade = 0;
        this.bonusSlots = 0;
        this.bonusFuelSlots = 0;
        this.balance = false;
        this.expModifier = 0;
        this.saveFuel = false;
    }

    protected final ContainerData dataAccess = new LevelableContainerData(this) {
        @Override
        protected int getExtra(int index) {
            return switch (index) {
                case 3 -> litTime;
                case 4 -> litTotalTime;
                case 5 -> bonusSlots;
                case 6 -> bonusFuelSlots;
                case 7 -> cookingProgress[0];
                case 8 -> cookingProgress[1];
                case 9 -> cookingProgress[2];
                case 10 -> cookingProgress[3];
                case 11 -> cookingProgress[4];
                case 12 -> cookingTotalTime[0];
                case 13 -> cookingTotalTime[1];
                case 14 -> cookingTotalTime[2];
                case 15 -> cookingTotalTime[3];
                case 16 -> cookingTotalTime[4];
                default -> 0;
            };
        }

        @Override
        protected void setExtra(int index, int value) {
            switch (index) {
                case 3 -> litTime = value;
                case 4 -> litTotalTime = value;
                case 5 -> bonusSlots = value;
                case 6 -> bonusFuelSlots = value;
                case 7 -> cookingProgress[0] = value;
                case 8 -> cookingProgress[1] = value;
                case 9 -> cookingProgress[2] = value;
                case 10 -> cookingProgress[3] = value;
                case 11 -> cookingProgress[4] = value;
                case 12 -> cookingTotalTime[0] = value;
                case 13 -> cookingTotalTime[1] = value;
                case 14 -> cookingTotalTime[2] = value;
                case 15 -> cookingTotalTime[3] = value;
                case 16 -> cookingTotalTime[4] = value;
            }
        }

        @Override
        protected int getExtraDataSize() {
            return CrystalFurnaceBlockEntity.DATA_SIZE;
        }
    };

    public boolean isFuel(ItemStack stack) {
        return stack.getBurnTime(this.recipeType) > 0;
    }

    public int[] getInputSlots() {
        return INPUT_SLOTS;
    }

    public int[] getOutputSLots() {
        return OUTPUT_SLOTS;
    }

    public int[] getFuelSlots() {
        return FUEL_SLOTS;
    }

    public boolean hasRecipe(ItemStack stack) {
        return getRecipe(stack) != null;
    }

    protected RecipeHolder<AbstractCookingRecipe> getRecipe(ItemStack item) {
        Optional<RecipeHolder<AbstractCookingRecipe>> recipeHolderOptional = (item.getItem() instanceof AirItem)
                ? Optional.empty()
                : this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) recipeType, new SingleRecipeInput(item), this.level);

        return recipeHolderOptional.orElse(null);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        // flag
        boolean isLit = this.isLit();
        boolean needsRebalance = false;

        // flag 1
        boolean needChange = false;

        if (isLit) {
            if (!saveFuel || (hasInputItems() && saveFuel)) {
                this.litTime--;
            }

            super.serverTick(level, pos, state);
        }

        ItemStack fuelItemStack = this.items.get(FUEL_SLOTS[0]);
        // flag 3
        boolean hasFuel = !fuelItemStack.isEmpty();

        for (int slotIndex = 0; slotIndex < this.bonusSlots + 1; slotIndex++) {
            int slot = INPUT_SLOTS[slotIndex];
            // Flag 2
            boolean hasItemToSmelt = !items.get(slot).isEmpty();

            if (this.isLit() || hasFuel && hasItemToSmelt) {
                RecipeHolder<AbstractCookingRecipe> recipe = this.getRecipe(this.getItem(slot));

                if (!this.isLit() && this.canBurn(level.registryAccess(), recipe, slot)) {
                    this.litTime = this.getBurnDuration(fuelItemStack);
                    this.litTotalTime = this.litTime;

                    if (this.isLit()) {
                        needChange = true;
                        if (fuelItemStack.hasCraftingRemainingItem()) {
                            items.set(FUEL_SLOTS[0], fuelItemStack.getCraftingRemainingItem());
                        } else {
                            fuelItemStack.shrink(1);
                            if (fuelItemStack.isEmpty()) {
                                this.items.set(FUEL_SLOTS[0], fuelItemStack.getCraftingRemainingItem());
                            }
                        }
                        // Here is where I need to re-balance the fuel slots
                        this.balanceFuel();
                    }
                }

                if (this.isLit() && this.canBurn(level.registryAccess(), recipe, slot)) {
                    this.cookingProgress[slotIndex]++;
                    if (this.cookingProgress[slotIndex] == this.cookingTotalTime[slotIndex]) {
                        this.cookingProgress[slotIndex] = 0;
                        this.cookingTotalTime[slotIndex] = this.getTotalCookTime(recipe, slot);
                        if (this.burn(level.registryAccess(), recipe, slot)) {
                            needsRebalance = true;
                        }

                        needChange = true;
                    }
                } else {
                    this.cookingProgress[slotIndex] = 0;
                }
            } else if (this.cookingProgress[slotIndex] > 0) {
                this.cookingProgress[slotIndex] = Mth.clamp(this.cookingProgress[slotIndex] - 2, 0, this.cookingTotalTime[slotIndex]);
            }
        }

        if (isLit != this.isLit()) {
            needChange = true;
            state = state.setValue(CrystalFurnaceBlock.LIT, this.isLit());
            level.setBlock(pos, state, 3);
        }

        if (needsRebalance) {
            this.balanceInputs();
            this.autoOutputAction.tickAction(level, pos, state);
        }

        if (needChange) {
            setChanged(level, pos, state);
        }
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    private boolean canBurn(RegistryAccess registryAccess, RecipeHolder<?> recipe, int slot) {
        int outputSlot = slot + MAX_SLOTS;
        if (!this.getItem(slot).isEmpty() && recipe != null) {
            ItemStack recipeOutput = ((RecipeHolder<? extends AbstractCookingRecipe>) recipe).value().assemble(new SingleRecipeInput(this.getItem(slot)), registryAccess);

            if (!recipeOutput.isEmpty()) {
                ItemStack output = this.getItem(outputSlot);
                if (output.isEmpty()) return true;
                else if (!ItemStack.isSameItem(output, recipeOutput)) return false;
                else return output.getCount() + recipeOutput.getCount() <= output.getMaxStackSize();
            }
        }
        return false;
    }

    private boolean burn(RegistryAccess registryAccess, RecipeHolder<?> recipe, int slot) {
        if (recipe != null && this.canBurn(registryAccess, recipe, slot)) {
            AbstractCookingRecipe castedRecipe = ((RecipeHolder<? extends AbstractCookingRecipe>) recipe).value();
            ItemStack input = this.items.get(slot);
            ItemStack output = this.items.get(slot + MAX_SLOTS);
            ItemStack recipeOutput = castedRecipe.assemble(new SingleRecipeInput(this.getItem(slot)), registryAccess);
            if (output.isEmpty()) {
                this.items.set(slot + MAX_SLOTS, recipeOutput.copy());
            } else if (output.is(recipeOutput.getItem())) {
                output.grow(recipeOutput.getCount());
            }

            if (input.is(Blocks.WET_SPONGE.asItem()) && !this.items.get(slot).isEmpty() && this.items.get(slot).is(Items.BUCKET)) {
                this.items.set(slot + MAX_SLOTS, new ItemStack(Items.WATER_BUCKET));
            }

            input.shrink(1);
            this.expHeld += castedRecipe.getExperience();
            int skillExp = (int) Math.ceil(castedRecipe.getExperience() * 10 * CrystalToolsConfig.FURNACE_EXPERIENCE_BOOST.get());
            this.addExp(skillExp);
            return true;
        } else {
            return false;
        }
    }

    private int getBurnDuration(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        } else {
            return stack.getBurnTime(this.recipeType) + this.fuelEfficiencyUpgrade * fuelEfficiencyAddedTicks;
        }
    }

    private int getTotalCookTime(RecipeHolder<AbstractCookingRecipe> recipe, int slot) {
        if (!this.getItem(slot).isEmpty() && recipe != null) {
            return Math.max(recipe.value().getCookingTime() - (int) (this.speedUpgrade * speedUpgradeSubtractTicks), 1);
        }

        return 0;
    }

    public void popExp(ServerPlayer player) {
        this.popExp(player.serverLevel(), player.position());
    }

    public void popExp(ServerLevel level, Vec3 pos) {
        int expAmount = (int) Math.ceil(this.expHeld * (1 + this.expModifier * expBoostPercentage));
        ExperienceOrb.award(level, pos, expAmount);
        this.expHeld = 0;
    }

    // Upgrade things
    private void balanceFuel() {
        ItemStack fuel1 = this.items.get(FUEL_SLOTS[0]);
        ItemStack fuel2 = this.items.get(FUEL_SLOTS[1]);
        ItemStack fuel3 = this.items.get(FUEL_SLOTS[2]);

        // 2 -> 1
        combineStacks(fuel1, fuel2);
        combineStacks(fuel1, fuel3);
        combineStacks(fuel2, fuel3);
    }

    private void combineStacks(ItemStack stackInto, ItemStack stackFrom) {
        if (ItemStackUtils.sameItem(stackInto, stackFrom) && stackInto.getCount() < stackInto.getMaxStackSize()) {
            int totalCount = stackInto.getCount() + stackFrom.getCount();
            if (totalCount < stackInto.getMaxStackSize()) {
                stackInto.setCount(totalCount);
                stackFrom.setCount(0);
            } else {
                stackInto.setCount(stackInto.getMaxStackSize());
                stackFrom.setCount(totalCount - stackInto.getMaxStackSize());
            }
        }
    }

    private void balanceInputs() {
        if (this.balance) {
            int[] activeInputSlots = Arrays.copyOfRange(CrystalFurnaceBlockEntity.INPUT_SLOTS, 0, this.bonusSlots + 1);

            Item[] items = new Item[activeInputSlots.length];
            Map<Item, Integer> itemMap = new HashMap<>();

            for (int i = 0; i < activeInputSlots.length; i++) {
                ItemStack stack = this.getItem(activeInputSlots[i]);
                items[i] = stack.getItem();
                if (stack.is(Items.AIR)) continue;

                if (itemMap.containsKey(stack.getItem())) {
                    itemMap.put(stack.getItem(), itemMap.get(stack.getItem()) + stack.getCount());
                } else {
                    itemMap.put(stack.getItem(), stack.getCount());
                }
            }

            for (Item item : itemMap.keySet()) {
                int[] indices = ArrayUtils.indicesOf(items, item);
                int[] emptyIndices = ArrayUtils.indicesOf(items, Items.AIR);
                int[] allIndices = ArrayUtils.combineArrays(indices, emptyIndices);
                int slots = allIndices.length;
                int totalCount = itemMap.get(item);
                int stackCount = totalCount / slots;
                int leftOver = totalCount % slots;

                for (int index : allIndices) {
                    int bonus = leftOver-- > 0 ? 1 : 0;
                    ItemStack toSet = new ItemStack(item, stackCount + bonus);
                    this.setItem(activeInputSlots[index], toSet);
                    items[index] = item;
                }
            }
        }
    }

    private boolean hasInputItems() {
        for (int i : INPUT_SLOTS) {
            if (!items.get(i).isEmpty()) {
                return true;
            }
        }

        return false;
    }
}


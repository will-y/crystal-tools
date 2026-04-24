package dev.willyelton.crystal_tools.common.levelable.block.entity;

import com.google.common.base.Predicates;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.api.common.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal_tools.api.common.block.entity.SideConfigBlockEntity;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.components.FurnaceData;
import dev.willyelton.crystal_tools.common.components.FurnaceUpgrades;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.inventory.InputOutputDelegatingResourceHandler;
import dev.willyelton.crystal_tools.common.inventory.VariableSizeItemStackHandler;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalFurnaceContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalFurnaceBlock;
import dev.willyelton.crystal_tools.api.common.block.entity.action.Action;
import dev.willyelton.crystal_tools.api.common.block.entity.action.AutoOutputAction;
import dev.willyelton.crystal_tools.api.common.block.entity.action.AutoOutputable;
import dev.willyelton.crystal_tools.api.common.block.entity.model.LevelableContainerData;
import dev.willyelton.crystal_tools.api.common.block.entity.model.SideConfigOption;
import dev.willyelton.crystal_tools.api.utils.ArrayUtils;
import dev.willyelton.crystal_tools.api.utils.NBTUtils;
import dev.willyelton.crystal_tools.api.utils.TransferUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.*;

public class CrystalFurnaceBlockEntity extends SideConfigBlockEntity implements MenuProvider, AutoOutputable {
    public static final int[] INPUT_SLOTS = new int[] {0, 1, 2, 3, 4};
    public static final int[] OUTPUT_SLOTS = new int[] {5, 6, 7, 8, 9};
    public static final int[] FUEL_SLOTS = new int[] {10, 11, 12};
    public static final int MAX_SLOTS = 5;
    public static final int MAX_FUEL_SLOTS = 3;

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

    // Caps
    private final VariableSizeItemStackHandler fuelHandler;
    private final VariableSizeItemStackHandler inputHandler;
    private final VariableSizeItemStackHandler outputHandler;

    public CrystalFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistration.CRYSTAL_FURNACE_BLOCK_ENTITY.get(), pos, state);

        fuelEfficiencyAddedTicks = CrystalToolsConfig.FUEL_EFFICIENCY_ADDED_TICKS.get();
        speedUpgradeSubtractTicks = CrystalToolsConfig.SPEED_UPGRADE_SUBTRACT_TICKS.get();
        expBoostPercentage = CrystalToolsConfig.EXPERIENCE_BOOST_PERCENTAGE.get();
        fuelHandler = new VariableSizeItemStackHandler(MAX_FUEL_SLOTS, "FUEL", this::isFuel, this);
        inputHandler = new VariableSizeItemStackHandler(MAX_SLOTS, "INPUT", this::hasRecipe, this);
        outputHandler = new VariableSizeItemStackHandler(MAX_SLOTS, "OUTPUT", Predicates.alwaysFalse(), this);
    }

    @Override
    protected Collection<Action> getDefaultActions() {
        autoOutputAction = new AutoOutputAction(this, this);
        return List.of(autoOutputAction);
    }

    @Override
    protected ResourceHandler<ItemResource> getHandlerForConfig(SideConfigOption option) {
        return switch (option) {
            case OUTPUT -> outputHandler;
            case INPUT -> inputHandler;
            case DISABLED -> null;
            case NONE -> new InputOutputDelegatingResourceHandler<>(List.of(fuelHandler, inputHandler), List.of(outputHandler));
            case FUEL_INPUT -> fuelHandler;
        };
    }

    @Override
    public Map<Integer, ItemStack> getOutputStacks() {
        Map<Integer, ItemStack> items = new HashMap<>();

        for (int i = 0; i < OUTPUT_SLOTS.length; i++) {
            items.put(OUTPUT_SLOTS[i], ItemUtil.getStack(outputHandler, i));
        }

        return items;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (ArrayUtils.arrayContains(INPUT_SLOTS, slot)) {
            ItemStack previous = ItemUtil.getStack(inputHandler, slot);
            inputHandler.set(slot, ItemResource.of(stack), stack.getCount());
            this.onInputSet(stack, previous, slot);
        } else if (ArrayUtils.arrayContains(OUTPUT_SLOTS, slot)) {
            outputHandler.set(slot - INPUT_SLOTS.length, ItemResource.of(stack), stack.getCount());
        } else if (ArrayUtils.arrayContains(FUEL_SLOTS, slot)) {
            fuelHandler.set(slot - INPUT_SLOTS.length - OUTPUT_SLOTS.length, ItemResource.of(stack), stack.getCount());
            if (this.getRecipe(stack) != null) {
                this.balanceFuel();
            }
        }

        setChanged();
    }

    @Override
    public Collection<Direction> possibleDirections() {
        return getAllSidesOfType(SideConfigOption.OUTPUT);
    }

    public void onInputSet(ItemStack newStack, ItemStack previousStack, Integer index) {
        if (!ItemStack.isSameItemSameComponents(newStack, previousStack)) {
            RecipeHolder<AbstractCookingRecipe> recipe = this.getRecipe(newStack);
            if (recipe != null) {
                this.cookingTotalTime[index] = getTotalCookTime(recipe, index);
                this.cookingProgress[index] = 0;
            }
        }
        setChanged();
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
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.inputHandler.deserialize(valueInput);
        this.outputHandler.deserialize(valueInput);
        this.fuelHandler.deserialize(valueInput);

        this.litTime = valueInput.getInt("LitTime").orElse(0);
        this.litTotalTime = valueInput.getInt("LitDuration").orElse(0);
        this.cookingProgress = NBTUtils.getIntArray(valueInput, "CookingProgress", MAX_SLOTS);
        this.cookingTotalTime = NBTUtils.getIntArray(valueInput, "CookingTotalTime", MAX_SLOTS);
        this.expHeld = valueInput.getFloatOr("ExpHeld", 0F);

        // Upgrade things
        this.speedUpgrade = valueInput.getFloatOr("SpeedUpgrade", 0F);
        this.fuelEfficiencyUpgrade = valueInput.getInt("FuelEfficiencyUpgrade").orElse(0);
        setBonusSlots(Math.min(valueInput.getInt("Slots").orElse(0), MAX_SLOTS));
        setBonusFuelSlots(Math.min(valueInput.getInt("FuelSlots").orElse(0), MAX_FUEL_SLOTS));
        this.balance = valueInput.getBooleanOr("Balance", false);
        this.expModifier = valueInput.getFloatOr("ExpModifier", 0F);
        this.saveFuel = valueInput.getBooleanOr("SaveFuel", false);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentInput) {
        super.applyImplicitComponents(componentInput);

        FurnaceData furnaceData = componentInput.get(DataComponents.FURNACE_DATA);
        if (furnaceData != null) {
            this.litTime = furnaceData.litTime();
            this.litTotalTime = furnaceData.litDuration();
            this.cookingProgress = furnaceData.cookingProgress().stream().mapToInt(Integer::intValue).toArray();
            this.cookingTotalTime = furnaceData.cookingTime().stream().mapToInt(Integer::intValue).toArray();
            this.expHeld = furnaceData.expHeld();
            this.inputHandler.load(furnaceData.inputItems());
            this.outputHandler.load(furnaceData.outputItems());
            this.fuelHandler.load(furnaceData.fuelItems());
        }

        FurnaceUpgrades furnaceUpgrades = componentInput.get(DataComponents.FURNACE_UPGRADES);
        if (furnaceUpgrades != null) {
            this.speedUpgrade = furnaceUpgrades.speed();
            this.fuelEfficiencyUpgrade = furnaceUpgrades.fuelEfficiency();
            setBonusSlots(Math.min(furnaceUpgrades.slots(), MAX_SLOTS));
            setBonusFuelSlots(Math.min(furnaceUpgrades.fuelSlots(), MAX_FUEL_SLOTS));
            this.balance = furnaceUpgrades.balance();
            this.expModifier = furnaceUpgrades.expModifier();
            this.saveFuel = furnaceUpgrades.saveFuel();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        inputHandler.serialize(valueOutput);
        outputHandler.serialize(valueOutput);
        fuelHandler.serialize(valueOutput);

        valueOutput.putInt("LitTime", this.litTime);
        valueOutput.putInt("LitDuration", this.litTotalTime);
        valueOutput.putIntArray("CookingProgress", this.cookingProgress);
        valueOutput.putIntArray("CookingTotalTime", this.cookingTotalTime);
        valueOutput.putFloat("ExpHeld", this.expHeld);

        // Upgrade things
        valueOutput.putFloat("SpeedUpgrade", this.speedUpgrade);
        valueOutput.putInt("FuelEfficiencyUpgrade", this.fuelEfficiencyUpgrade);
        valueOutput.putInt("Slots", this.bonusSlots);
        valueOutput.putInt("FuelSlots", this.bonusFuelSlots);
        valueOutput.putBoolean("Balance", this.balance);
        valueOutput.putFloat("ExpModifier", this.expModifier);
        valueOutput.putBoolean("SaveFuel", this.saveFuel);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        FurnaceData furnaceData = new FurnaceData(litTime, litTotalTime, Arrays.stream(cookingProgress).boxed().toList(),
                Arrays.stream(cookingTotalTime).boxed().toList(), expHeld, inputHandler.copyToList(),
                outputHandler.copyToList(), fuelHandler.copyToList());
        components.set(DataComponents.FURNACE_DATA, furnaceData);

        FurnaceUpgrades furnaceUpgrades = new FurnaceUpgrades(speedUpgrade, fuelEfficiencyUpgrade, bonusSlots,
                bonusFuelSlots, balance, expModifier, saveFuel);
        components.set(DataComponents.FURNACE_UPGRADES, furnaceUpgrades);
    }

    @Override
    protected void addToExtraData(String key, float value) {
        if (FURNACE_SPEED.toString().equals(key)) {
            this.speedUpgrade += value;
        } else if (FUEL_EFFICIENCY.toString().equals(key)) {
            this.fuelEfficiencyUpgrade += (int) value;
        } else if (SLOT_BONUS.toString().equals(key)) {
            if (bonusSlots >= MAX_SLOTS - 1) {
                CrystalTools.LOGGER.warn("Furnace Max Slot Size Reached");
            } else {
                setBonusSlots(this.bonusSlots + (int) value);
            }
        } else if (FUEL_SLOT_BONUS.toString().equals(key)) {
            if (bonusFuelSlots >= MAX_FUEL_SLOTS - 1) {
                CrystalTools.LOGGER.warn("Furnace Max Fuel Slot Size Reached");
            } else {
                setBonusFuelSlots(this.bonusFuelSlots += (int) value);
            }
        } else if (AUTO_BALANCE.toString().equals(key)) {
            this.balance = value == 1;
        } else if (EXP_BOOST.toString().equals(key)) {
            this.expModifier += value;
        } else if (SAVE_FUEL.toString().equals(key)) {
            this.saveFuel = value == 1;
        }
    }

    @Override
    protected void resetExtraSkills() {
        if (this.level instanceof ServerLevel serverLevel) {
            // Drop items
            this.inputHandler.dropAll(serverLevel, this.getBlockPos());
            this.outputHandler.dropAll(serverLevel, this.getBlockPos());
            this.fuelHandler.dropAll(serverLevel, this.getBlockPos());
            this.inputHandler.setCurrentSize(1);
            this.outputHandler.setCurrentSize(1);
            this.fuelHandler.setCurrentSize(1);
            this.popExp(serverLevel, Vec3.atCenterOf(this.getBlockPos()));

            // Set state to off
            BlockState state = level.getBlockState(this.getBlockPos());
            state = state.setValue(CrystalFurnaceBlock.LIT, false);
            level.setBlock(this.getBlockPos(), state, Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS);
        }

        // TODO: Do I need to clear these client side still?

        // Furnace things
        this.litTime = 0;
        this.litTotalTime = 0;
        this.cookingProgress = new int[MAX_SLOTS];
        this.cookingTotalTime = new int[MAX_SLOTS];
        this.expHeld = 0;

        // Upgrades
        this.speedUpgrade = 0;
        this.fuelEfficiencyUpgrade = 0;
        setBonusSlots(0);
        setBonusFuelSlots(0);
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
                case 5 -> setBonusSlots(Math.min(value, MAX_SLOTS));
                case 6 -> setBonusFuelSlots(Math.min(value, MAX_FUEL_SLOTS));
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
        return stack.getBurnTime(this.recipeType, this.level.fuelValues()) > 0;
    }

    public boolean hasRecipe(ItemStack stack) {
        return getRecipe(stack) != null;
    }

    protected RecipeHolder<AbstractCookingRecipe> getRecipe(ItemResource item) {
        return this.getRecipe(item.toStack());
    }

    protected RecipeHolder<AbstractCookingRecipe> getRecipe(ItemStack item) {
        if (this.level != null && this.level.isClientSide()) return null;
        Optional<RecipeHolder<AbstractCookingRecipe>> recipeHolderOptional = (item.getItem() instanceof AirItem)
                ? Optional.empty()
                : ((ServerLevel)this.level).recipeAccess().getRecipeFor((RecipeType<AbstractCookingRecipe>) recipeType, new SingleRecipeInput(item), this.level);

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

        ItemResource fuelItemResource = this.fuelHandler.getResource(0);
        // flag 3
        boolean hasFuel = !fuelItemResource.isEmpty();

        for (int slotIndex = 0; slotIndex < this.bonusSlots + 1; slotIndex++) {
            int slot = INPUT_SLOTS[slotIndex];
            // Flag 2
            ItemResource inputItemResource = inputHandler.getResource(slot);
            boolean hasItemToSmelt = !inputHandler.getResource(slot).isEmpty();

            if (this.isLit() || hasFuel && hasItemToSmelt) {
                RecipeHolder<AbstractCookingRecipe> recipe = this.getRecipe(inputItemResource);

                if (!this.isLit() && this.canBurn(level.registryAccess(), recipe, slot)) {
                    this.litTime = this.getBurnDuration(fuelItemResource);
                    this.litTotalTime = this.litTime;

                    if (this.isLit()) {
                        needChange = true;
                        ItemStack craftingRemainder = fuelItemResource.toStack(fuelHandler.getCapacityAsInt(0, fuelItemResource));
                        if (!craftingRemainder.isEmpty()) {
                            fuelHandler.set(0, ItemResource.of(craftingRemainder), craftingRemainder.getCount());
                        } else {
                            TransferUtils.shrink(fuelHandler, 0, 1);
                            if (fuelItemResource.isEmpty()) {
                                this.fuelHandler.set(0, ItemResource.of(craftingRemainder), craftingRemainder.getCount());
                            }
                        }

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

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.level instanceof ServerLevel serverlevel) {
            this.popExp(serverlevel, Vec3.atCenterOf(pos));
        }
    }

    public void setBonusSlots(int bonusSlots) {
        this.bonusSlots = bonusSlots;
        this.inputHandler.setCurrentSize(bonusSlots + 1);
        this.outputHandler.setCurrentSize(bonusSlots + 1);
        setChanged();
    }

    public void setBonusFuelSlots(int bonusFuelSlots) {
        this.bonusFuelSlots = bonusFuelSlots;
        this.fuelHandler.setCurrentSize(bonusFuelSlots + 1);
        setChanged();
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    private boolean canBurn(RegistryAccess registryAccess, RecipeHolder<?> recipe, int slot) {
        if (!this.inputHandler.getResource(slot).isEmpty() && recipe != null) {
            ItemStack recipeOutput = ((RecipeHolder<? extends AbstractCookingRecipe>) recipe).value().assemble(new SingleRecipeInput(ItemUtil.getStack(inputHandler, slot)));

            if (!recipeOutput.isEmpty()) {
                ItemResource output = this.outputHandler.getResource(slot);
                if (output.isEmpty()) {
                    return true;
                } else if (!output.is(recipeOutput.getItem())) {
                    return false;
                } else {
                    return this.outputHandler.getAmountAsInt(slot) + recipeOutput.getCount() <= output.getMaxStackSize();
                }
            }
        }
        return false;
    }

    private boolean burn(RegistryAccess registryAccess, RecipeHolder<?> recipe, int slot) {
        if (recipe != null && this.canBurn(registryAccess, recipe, slot)) {
            AbstractCookingRecipe castedRecipe = ((RecipeHolder<? extends AbstractCookingRecipe>) recipe).value();
            ItemStack recipeOutput = castedRecipe.assemble(new SingleRecipeInput(ItemUtil.getStack(inputHandler, slot)));
            this.outputHandler.set(slot, ItemResource.of(recipeOutput), recipeOutput.getCount() + outputHandler.getAmountAsInt(slot));

            TransferUtils.shrink(inputHandler, slot, 1);
            this.expHeld += castedRecipe.experience();
            int skillExp = (int) Math.ceil(castedRecipe.experience() * 10 * CrystalToolsConfig.FURNACE_EXPERIENCE_BOOST.get());
            this.addExp(skillExp);
            return true;
        } else {
            return false;
        }
    }

    private int getBurnDuration(ItemResource resource) {
        if (resource.isEmpty()) {
            return 0;
        } else {
            return resource.toStack().getBurnTime(this.recipeType, this.level.fuelValues()) + this.fuelEfficiencyUpgrade * fuelEfficiencyAddedTicks;
        }
    }

    private int getTotalCookTime(RecipeHolder<AbstractCookingRecipe> recipe, int slot) {
        if (!this.inputHandler.getResource(slot).isEmpty() && recipe != null) {
            return Math.max(recipe.value().cookingTime() - (int) (this.speedUpgrade * speedUpgradeSubtractTicks), 1);
        }

        return 0;
    }

    public void popExp(ServerPlayer player) {
        this.popExp(player.level(), player.position());
    }

    public void popExp(ServerLevel level, Vec3 pos) {
        int expAmount = (int) Math.ceil(this.expHeld * (1 + this.expModifier * expBoostPercentage));
        ExperienceOrb.award(level, pos, expAmount);
        this.expHeld = 0;
    }

    // Upgrade things
    // TODO: Check and there is probably a better way to do this
    private void balanceFuel() {
        combineStacks(fuelHandler, 0, 1);
        combineStacks(fuelHandler, 0, 2);
        combineStacks(fuelHandler, 1, 2);
    }

    private void combineStacks(ResourceHandler<ItemResource> handler, int intoIndex, int fromIndex) {
        try (Transaction tx = Transaction.openRoot()) {
            ItemStack fromStack = TransferUtils.extractAllFromSlot(handler, fromIndex, tx);
            if (fromStack.isEmpty()) {
                return;
            }
            int inserted = handler.insert(intoIndex, ItemResource.of(fromStack), fromStack.getCount(), tx);

            if (inserted != fromStack.getCount()) {
                handler.insert(fromIndex, ItemResource.of(fromStack), fromStack.getCount() - inserted, tx);
            }

            tx.commit();
        }
    }

    private void balanceInputs() {
        if (this.balance) {
            int[] activeInputSlots = Arrays.copyOfRange(CrystalFurnaceBlockEntity.INPUT_SLOTS, 0, this.bonusSlots + 1);

            Item[] items = new Item[activeInputSlots.length];
            Map<Item, Integer> itemMap = new HashMap<>();

            for (int i = 0; i < activeInputSlots.length; i++) {
                ItemStack stack = TransferUtils.extractAllFromSlotNoCommit(inputHandler, activeInputSlots[i]);
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
            if (!inputHandler.getResource(i).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public VariableSizeItemStackHandler getFuelHandler() {
        return fuelHandler;
    }

    public VariableSizeItemStackHandler getInputHandler() {
        return inputHandler;
    }

    public VariableSizeItemStackHandler getOutputHandler() {
        return outputHandler;
    }

    @Override
    protected SideConfigOption defaultForSide(Direction side) {
        return switch (side) {
            case UP -> SideConfigOption.INPUT;
            case DOWN -> SideConfigOption.OUTPUT;
            default -> SideConfigOption.FUEL_INPUT;
        };
    }
}


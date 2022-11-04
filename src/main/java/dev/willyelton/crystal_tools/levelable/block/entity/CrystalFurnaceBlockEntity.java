package dev.willyelton.crystal_tools.levelable.block.entity;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.levelable.block.CrystalFurnaceBlock;
import dev.willyelton.crystal_tools.levelable.block.ModBlocks;
import dev.willyelton.crystal_tools.levelable.block.container.CrystalFurnaceContainer;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CrystalFurnaceBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int[] INPUT_SLOTS = new int[] {0, 1, 2, 3, 4};
    public static final int[] OUTPUT_SLOTS = new int[] {5, 6, 7, 8, 9};
    public static final int[] FUEL_SLOTS = new int[] {10, 11, 12};

    private static final Direction[] POSSIBLE_INVENTORIES = new Direction[] {Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public static final int SIZE = 13;
    public static final int DATA_SIZE = 200;
    // TODO: Config
    public static final int FUEL_EFFICIENCY_ADDED_TICKS = 100;
    public static final int SPEED_UPGRADE_SUBTRACT_TICKS = 10;

    private NonNullList<ItemStack> items;

    LazyOptional<? extends IItemHandler>[] invHandlers = SidedInvWrapper.create(this, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);

    private final RecipeType<? extends AbstractCookingRecipe> recipeType = RecipeType.SMELTING;

    // Furnace related fields
    private int litTime = 0;
    private int litDuration = 0;
    private int[] cookingProgress = new int[5];
    private int[] cookingTotalTime = new int[5];
    private float expHeld = 0;

    // Levelable things
    private int skillPoints = 0;
    private int[] points = new int[100];
    private int exp = 0;
    private int expCap = CrystalToolsConfig.BASE_EXPERIENCE_CAP.get();

    // Things that can be upgraded
    private float speedUpgrade = 0;
    private int fuelEfficiencyUpgrade = 0;
    private int bonusSlots = 0;
    private int bonusFuelSlots = 0;
    private boolean balance = false;
    private boolean autoOutput = false;

    public CrystalFurnaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.CRYSTAL_FURNACE_BLOCK_ENTITY.get(), pPos, pBlockState);
        items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!this.isRemoved() && side != null && cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.DOWN)
                return invHandlers[0].cast();
            else if (side == Direction.UP)
                return invHandlers[1].cast();
            else if (side == Direction.NORTH)
                return invHandlers[2].cast();
            else if (side == Direction.SOUTH)
                return invHandlers[3].cast();
            else if (side == Direction.WEST)
                return invHandlers[4].cast();
            else
                return invHandlers[5].cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction face) {
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
    public boolean canPlaceItemThroughFace(int pIndex, @NotNull ItemStack pItemStack, @org.jetbrains.annotations.Nullable Direction pDirection) {
        return canPlaceItem(pIndex, pItemStack);
    }

    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        if (ArrayUtils.arrayContains(INPUT_SLOTS, index)) {
            return index <= INPUT_SLOTS[this.bonusSlots];
        } else if (ArrayUtils.arrayContains(OUTPUT_SLOTS, index)) {
            return false;
        } else if (ArrayUtils.arrayContains(FUEL_SLOTS, index)) {
            return index <= FUEL_SLOTS[this.bonusFuelSlots] && (ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0);
        }

        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, @NotNull ItemStack pStack, @NotNull Direction pDirection) {
        return pDirection != Direction.DOWN || pIndex != 1;
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int pSlot) {
        return this.items.get(pSlot);
    }

    @Override
    public @NotNull ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(items, pSlot, pAmount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(items, pSlot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        ItemStack current = this.items.get(slot);
        this.items.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        if (ArrayUtils.arrayContains(INPUT_SLOTS, slot) && !(!stack.isEmpty() && stack.sameItem(current) && ItemStack.tagMatches(stack, current))) {
            int index = ArrayUtils.indexOf(INPUT_SLOTS, slot);
            AbstractCookingRecipe recipe = this.getRecipe(stack).orElse(null);
            if (recipe != null) {
                this.cookingTotalTime[index] = getTotalCookTime(recipe, index);
                this.cookingProgress[index] = 0;
                this.setChanged();
            }
        }

        if (ArrayUtils.arrayContains(FUEL_SLOTS, slot)) {
            this.getRecipe(stack).ifPresent(recipe -> this.balanceFuel());
        }
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return pPlayer.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.crystal_tools.crystal_furnace");
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new CrystalFurnaceContainer(pContainerId, pPlayer.getLevel(), this.getBlockPos(), pPlayerInventory, this.dataAccess);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.items);
        this.litTime = nbt.getInt("LitTime");
        this.litDuration = nbt.getInt("LitDuration");
        this.cookingProgress = NBTUtils.getIntArray(nbt, "CookingProgress", 5);
        this.cookingTotalTime = NBTUtils.getIntArray(nbt, "CookingTotalTime", 5);
        this.expHeld = nbt.getFloat("ExpHeld");

        // Levelable things
        this.skillPoints = nbt.getInt("SkillPoints");
        this.points = NBTUtils.getIntArray(nbt, "Points", 100);
        this.exp = nbt.getInt("Exp");
        this.expCap = nbt.getInt("ExpCap");

        if (this.expCap == 0) this.expCap = CrystalToolsConfig.BASE_EXPERIENCE_CAP.get();

        // Upgrade things
        this.speedUpgrade = nbt.getFloat("SpeedUpgrade");
        this.fuelEfficiencyUpgrade = nbt.getInt("FuelEfficiencyUpgrade");
        this.bonusSlots = nbt.getInt("Slots");
        this.bonusFuelSlots = nbt.getInt("FuelSlots");
        this.balance = nbt.getBoolean("Balance");
        this.autoOutput = nbt.getBoolean("AutoOutput");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        ContainerHelper.saveAllItems(nbt, this.items);
        nbt.putInt("LitTime", this.litTime);
        nbt.putInt("LitDuration", this.litDuration);
        nbt.putIntArray("CookingProgress", this.cookingProgress);
        nbt.putIntArray("CookingTotalTime", this.cookingTotalTime);
        nbt.putFloat("ExpHeld", this.expHeld);

        // Levelable things
        nbt.putInt("SkillPoints", this.skillPoints);
        nbt.putIntArray("Points", this.points);
        nbt.putInt("Exp", this.exp);
        nbt.putInt("ExpCap", this.expCap);

        // Upgrade things
        nbt.putFloat("SpeedUpgrade", this.speedUpgrade);
        nbt.putInt("FuelEfficiencyUpgrade", this.fuelEfficiencyUpgrade);
        nbt.putInt("Slots", this.bonusSlots);
        nbt.putInt("FuelSlots", this.bonusFuelSlots);
        nbt.putBoolean("Balance", this.balance);
        nbt.putBoolean("AutoOutput", this.autoOutput);
    }

    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int dataIndex) {
            return switch (dataIndex) {
                case 0 -> CrystalFurnaceBlockEntity.this.litTime;
                case 1 -> CrystalFurnaceBlockEntity.this.litDuration;
                case 2 -> CrystalFurnaceBlockEntity.this.bonusSlots;
                case 3 -> CrystalFurnaceBlockEntity.this.bonusFuelSlots;
                case 4 -> CrystalFurnaceBlockEntity.this.cookingProgress[0];
                case 5 -> CrystalFurnaceBlockEntity.this.cookingProgress[1];
                case 6 -> CrystalFurnaceBlockEntity.this.cookingProgress[2];
                case 7 -> CrystalFurnaceBlockEntity.this.cookingProgress[3];
                case 8 -> CrystalFurnaceBlockEntity.this.cookingProgress[4];
                case 9 -> CrystalFurnaceBlockEntity.this.cookingTotalTime[0];
                case 10 -> CrystalFurnaceBlockEntity.this.cookingTotalTime[1];
                case 11 -> CrystalFurnaceBlockEntity.this.cookingTotalTime[2];
                case 12 -> CrystalFurnaceBlockEntity.this.cookingTotalTime[3];
                case 13 -> CrystalFurnaceBlockEntity.this.cookingTotalTime[4];
                case 14 -> CrystalFurnaceBlockEntity.this.skillPoints;
                case 15 -> CrystalFurnaceBlockEntity.this.exp;
                case 16 -> CrystalFurnaceBlockEntity.this.expCap;
                default -> (dataIndex >= 100 && dataIndex < 200) ? CrystalFurnaceBlockEntity.this.points[dataIndex - 100] : 0;
            };
        }

        public void set(int dataIndex, int value) {
            switch (dataIndex) {
                case 0 -> CrystalFurnaceBlockEntity.this.litTime = value;
                case 1 -> CrystalFurnaceBlockEntity.this.litDuration = value;
                case 2 -> CrystalFurnaceBlockEntity.this.bonusSlots = value;
                case 3 -> CrystalFurnaceBlockEntity.this.bonusFuelSlots = value;
                case 4 -> CrystalFurnaceBlockEntity.this.cookingProgress[0] = value;
                case 5 -> CrystalFurnaceBlockEntity.this.cookingProgress[1] = value;
                case 6 -> CrystalFurnaceBlockEntity.this.cookingProgress[2] = value;
                case 7 -> CrystalFurnaceBlockEntity.this.cookingProgress[3] = value;
                case 8 -> CrystalFurnaceBlockEntity.this.cookingProgress[4] = value;
                case 9 -> CrystalFurnaceBlockEntity.this.cookingTotalTime[0] = value;
                case 10 -> CrystalFurnaceBlockEntity.this.cookingTotalTime[1] = value;
                case 11 -> CrystalFurnaceBlockEntity.this.cookingTotalTime[2] = value;
                case 12 -> CrystalFurnaceBlockEntity.this.cookingTotalTime[3] = value;
                case 13 -> CrystalFurnaceBlockEntity.this.cookingTotalTime[4] = value;
                case 14 -> CrystalFurnaceBlockEntity.this.skillPoints = value;
                case 15 -> CrystalFurnaceBlockEntity.this.exp = value;
                case 16 -> CrystalFurnaceBlockEntity.this.expCap = value;
                default -> {
                    if (dataIndex >= 100 && dataIndex < 200) {
                        CrystalFurnaceBlockEntity.this.points[dataIndex - 100] = value;
                    }
                }
            }
        }

        public int getCount() {
            return DATA_SIZE;
        }
    };

    public boolean isFuel(ItemStack stack) {
        return net.minecraftforge.common.ForgeHooks.getBurnTime(stack, this.recipeType) > 0;
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
        return getRecipe(stack).isPresent();
    }

    protected Optional<AbstractCookingRecipe> getRecipe(ItemStack item) {
        return (item.getItem() instanceof AirItem)
                ? Optional.empty()
                : this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) recipeType, new SimpleContainer(item), this.level);
    }

    private int autoOutputCounter = 0;

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        // flag
        boolean isLit = this.isLit();
        boolean needsRebalance = false;

        // flag 1
        boolean needChange = false;

        if (isLit) {
            this.litTime--;
            this.autoOutputCounter++;
            if (autoOutputCounter > 100) {
                this.autoOutput();
                this.autoOutputCounter = 0;
            }
        }

        ItemStack fuelItemStack = this.items.get(FUEL_SLOTS[0]);
        // flag 3
        boolean hasFuel = !fuelItemStack.isEmpty();

        for (int slotIndex = 0; slotIndex < this.bonusSlots + 1; slotIndex++) {
            int slot = INPUT_SLOTS[slotIndex];
            // Flag 2
            boolean hasItemToSmelt = !items.get(slot).isEmpty();

            if (this.isLit() || hasFuel && hasItemToSmelt) {
                Optional<AbstractCookingRecipe> recipe = this.getRecipe(this.getItem(slot));

                if (!this.isLit() && this.canBurn(recipe.orElse(null), slot)) {
                    this.litTime = this.getBurnDuration(fuelItemStack);
                    this.litDuration = this.litTime;

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

                if (this.isLit() && this.canBurn(recipe.orElse(null), slot)) {
                    this.cookingProgress[slotIndex]++;
                    if (this.cookingProgress[slotIndex] == this.cookingTotalTime[slotIndex]) {
                        this.cookingProgress[slotIndex] = 0;
                        this.cookingTotalTime[slotIndex] = this.getTotalCookTime(recipe.orElse(null), slot);
                        if (this.burn(recipe.orElse(null), slot)) {
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
            this.autoOutput();
        }

        if (needChange) {
            setChanged(level, pos, state);
        }
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    private boolean canBurn(Recipe<?> recipe, int slot) {
        int outputSlot = slot + 5;
        if (!this.getItem(slot).isEmpty() && recipe != null) {
            ItemStack recipeOutput = recipe.getResultItem();
            if (!recipeOutput.isEmpty()) {
                ItemStack output = this.getItem(outputSlot);
                if (output.isEmpty()) return true;
                else if (!output.sameItem(recipeOutput)) return false;
                else return output.getCount() + recipeOutput.getCount() <= output.getMaxStackSize();
            }
        }
        return false;
    }

    private boolean burn(Recipe<?> recipe, int slot) {
        if (recipe != null && this.canBurn(recipe, slot) && recipe instanceof AbstractCookingRecipe cookingRecipe) {
            ItemStack input = this.items.get(slot);
            ItemStack output = this.items.get(slot + 5);
            ItemStack recipeOutput = cookingRecipe.getResultItem();
            if (output.isEmpty()) {
                this.items.set(slot + 5, recipeOutput.copy());
            } else if (output.is(recipeOutput.getItem())) {
                output.grow(recipeOutput.getCount());
            }

            if (input.is(Blocks.WET_SPONGE.asItem()) && !this.items.get(slot).isEmpty() && this.items.get(slot).is(Items.BUCKET)) {
                this.items.set(slot + 5, new ItemStack(Items.WATER_BUCKET));
            }

            input.shrink(1);
            this.expHeld += cookingRecipe.getExperience();
            this.addExp(cookingRecipe.getExperience());
            return true;
        } else {
            return false;
        }
    }

    private int getBurnDuration(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        } else {
            return ForgeHooks.getBurnTime(stack, this.recipeType) + this.fuelEfficiencyUpgrade * FUEL_EFFICIENCY_ADDED_TICKS;
        }
    }

    private int getTotalCookTime(AbstractCookingRecipe recipe, int slot) {
        if (!this.getItem(slot).isEmpty() && recipe != null) {
            return Math.max(recipe.getCookingTime() - (int) (this.speedUpgrade * SPEED_UPGRADE_SUBTRACT_TICKS), 1);
        }

        return 0;
    }

    public void popExp(ServerPlayer player) {
        ExperienceOrb.award(player.getLevel(), player.position(), (int) Math.ceil(this.expHeld));
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

//        this.items.set(FUEL_SLOTS[0], fuel1);
//        this.items.set(FUEL_SLOTS[1], fuel2);
//        this.items.set(FUEL_SLOTS[2], fuel3);
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

    private void autoOutput() {
        if (this.autoOutput) {
            if (this.level != null) {
                for (int slotIndex : OUTPUT_SLOTS) {
                    ItemStack outputStack = this.getItem(slotIndex);
                    for (Direction dir : POSSIBLE_INVENTORIES) {
                        BlockEntity blockEntity = this.level.getBlockEntity(this.getBlockPos().relative(dir));

                        if (blockEntity != null) {
                            Optional<IItemHandler> optional  = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite()).resolve();

                            if (optional.isPresent()) {
                                IItemHandler handler = optional.get();

                                for (int i = 0; i < handler.getSlots(); i++) {
                                    outputStack = handler.insertItem(i, outputStack, false);

                                    if (outputStack.isEmpty()) break;
                                }
                            }
                        }
                        if (outputStack.isEmpty()) break;
                    }
                    this.setItem(slotIndex, outputStack);
                }
            }
        }
    }

    // Levelable things
    public void addSkillPoints(int points) {
        this.skillPoints += points;
        this.setChanged();
    }

    public void addToData(String key, float value) {
        switch(key) {
            case "skill_points" -> this.skillPoints += value;
            case "experience" -> this.exp += value;
            case "experience_cap" -> this.expCap += value;
            case "speed_bonus" -> this.speedUpgrade += value;
            case "fuel_bonus" -> this.fuelEfficiencyUpgrade += value;
            case "slot_bonus" -> this.bonusSlots += value;
            case "fuel_slot_bonus" -> this.bonusFuelSlots += value;
            case "auto_balance" -> this.balance = value == 1;
            case "auto_output" -> this.autoOutput = value == 1;
        }
        this.setChanged();
    }

    public void addToPoints(int id, int value) {
        this.points[id] += value;
        this.setChanged();
    }

    private void addExp(float recipeExp) {
        int expToGain = (int) Math.ceil(recipeExp * 10 * CrystalToolsConfig.FURNACE_EXPERIENCE_BOOST.get());
        this.exp += expToGain;

        while (this.exp > this.expCap) {
            this.exp = this.exp - this.expCap;
            this.expCap = ToolUtils.getNewCap(this.expCap, 1);
            this.skillPoints++;
        }
    }
}


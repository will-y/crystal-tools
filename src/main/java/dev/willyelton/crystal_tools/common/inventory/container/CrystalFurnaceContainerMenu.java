package dev.willyelton.crystal_tools.common.inventory.container;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.inventory.container.slot.furnace.CrystalFurnaceFuelSlot;
import dev.willyelton.crystal_tools.common.inventory.container.slot.furnace.CrystalFurnaceInputSlot;
import dev.willyelton.crystal_tools.common.inventory.container.slot.furnace.CrystalFurnaceOutputSlot;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Arrays;

public class CrystalFurnaceContainerMenu extends LevelableContainerMenu {
    private final CrystalFurnaceBlockEntity te;

    private final int fuelSlotsX = 21;
    private final int[] fuelSlotsPos = new int[] {69, 44, 19};
    private final int inputSlotY = 58;
    private final int outputSlotY = 23;
    private final int[][] slotXValues = new int[][] {new int[] {96, 0, 0, 0, 0}, new int[] {80, 112, 0, 0, 0}, new int[] {74, 96, 118, 0, 0}, new int[] {57, 83, 109, 135, 0}, new int[] {58, 77, 96, 115, 134}};
    private final RecipePropertySet acceptedInputs;

    private static final int PLAYER_INVENTORY_START = 13;
    private static final int PLAYER_INVENTORY_END = 49;
    private static final int INPUT_START = CrystalFurnaceBlockEntity.INPUT_SLOTS[0];
    private static final int FUEL_START = CrystalFurnaceBlockEntity.FUEL_SLOTS[0];

    public CrystalFurnaceContainerMenu(int containerId, Level level, BlockPos pos, Inventory playerInventory, ContainerData data) {
        super(Registration.CRYSTAL_FURNACE_CONTAINER.get(), containerId, playerInventory, data);
        acceptedInputs = level.recipeAccess().propertySet(RecipePropertySet.FURNACE_INPUT);
        te = (CrystalFurnaceBlockEntity) level.getBlockEntity(pos);

        this.addFurnaceSlots(5, 5);
        this.addFuelSlots(3, 3);

        this.layoutPlayerInventorySlots(8, 109);
    }

    /**
     * Called when shift clicking a slot with an item in it
     * Player inventory starts at 13 goes to 48
     * My slots are as expected
     * Pretty much copied from AbstractFurnaceMenu
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();

            // Result Slot
            if (ArrayUtils.arrayContains(CrystalFurnaceBlockEntity.OUTPUT_SLOTS, index)) {
                if (!this.moveItemStackTo(itemStack1, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemStack1, itemStack);
            // Player Slots
            } else if (!ArrayUtils.arrayContains(CrystalFurnaceBlockEntity.INPUT_SLOTS, index) && !ArrayUtils.arrayContains(CrystalFurnaceBlockEntity.FUEL_SLOTS, index)) {
                if (this.canSmelt(itemStack1)) {
                    if (!this.moveItemStackTo(itemStack1, INPUT_START, INPUT_START + this.getNumActiveSlots(), false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(itemStack1)) {
                    if (!this.moveItemStackTo(itemStack1, FUEL_START, FUEL_START + this.getNumActiveFuelSlots(), false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= PLAYER_INVENTORY_START && index < PLAYER_INVENTORY_END - 9) {
                    if (!this.moveItemStackTo(itemStack1, PLAYER_INVENTORY_END - 9, PLAYER_INVENTORY_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= PLAYER_INVENTORY_END - 9 && index < PLAYER_INVENTORY_END && !this.moveItemStackTo(itemStack1, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END - 9, false)) {
                    return ItemStack.EMPTY;
                }
            // Input and Fuel Slots
            } else if (!this.moveItemStackTo(itemStack1, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStack1.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemStack1);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return te.stillValid(pPlayer);
    }

    private void addFurnaceSlots(int numSlots, int numActiveSlots) {
        int[] inputSlots = te.getInputSlots();
        int[] outputSlots = te.getOutputSLots();

        for (int i = 0; i < numSlots; i++) {
            this.addSlot(new CrystalFurnaceInputSlot(this, inputSlots[i], this.slotXValues[numActiveSlots - 1][i], this.inputSlotY));
        }

        for (int i = 0; i < numSlots; i++) {
            this.addSlot(new CrystalFurnaceOutputSlot(player, this, outputSlots[i], this.slotXValues[numActiveSlots - 1][i], this.outputSlotY));
        }
    }

    private void addFuelSlots(int numSlots, int numActiveFuelSlots) {
        int[] slots = te.getFuelSlots();
        for (int i = 0; i < numSlots; i++) {
            this.addSlot(new CrystalFurnaceFuelSlot(this, slots[i], this.fuelSlotsX, this.fuelSlotsPos[i]));
        }
    }

    public float getLitProgress() {
        if (this.data.get(4) == 0) return 0;

        return this.data.get(3) / (float) this.data.get(4);
    }

    public boolean isLit() {
        return this.data.get(3) > 0;
    }

    public float getBurnProgress(int slot) {
        int index = ArrayUtils.indexOf(CrystalFurnaceBlockEntity.INPUT_SLOTS, slot);
        int cookingProgress = this.data.get(index + 7);
        int cookingTotalTime = this.data.get(index + 12);
        return cookingTotalTime != 0 && cookingProgress != 0 ? cookingProgress / (float) cookingTotalTime : 0;
    }

    public int getNumActiveSlots() {
        return this.data.get(5) + 1;
    }

    public int getNumActiveFuelSlots() {
        return this.data.get(6) + 1;
    }

    protected boolean canSmelt(ItemStack stack) {
        return this.acceptedInputs.test(stack);
    }

    protected boolean isFuel(ItemStack stack) {
        return stack.getBurnTime(RecipeType.SMELTING, level.fuelValues()) > 0;
    }

    @Override
    public String getBlockType() {
        return "crystal_furnace";
    }

    @Override
    public CrystalFurnaceBlockEntity getBlockEntity() {
        return this.te;
    }

    public int[] getActiveInputSlots() {
        return Arrays.copyOfRange(CrystalFurnaceBlockEntity.INPUT_SLOTS, 0, this.getNumActiveSlots());
    }

    public int[] getActiveOutputSlots() {
        return Arrays.copyOfRange(CrystalFurnaceBlockEntity.OUTPUT_SLOTS, 0, this.getNumActiveSlots());
    }

    public int[] getActiveFuelSlots() {
        return Arrays.copyOfRange(CrystalFurnaceBlockEntity.FUEL_SLOTS, 0, this.getNumActiveFuelSlots());
    }
}

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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CrystalFurnaceContainerMenu extends BaseContainerMenu {
    private final CrystalFurnaceBlockEntity te;
    private final Player player;
    private final ContainerData data;
    private final Level level;

    private final int fuelSlotsX = 21;
    private final int[] fuelSlotsPos = new int[] {69, 44, 19};
    private final int inputSlotY = 58;
    private final int outputSlotY = 23;
    private final int[][] slotXValues = new int[][] {new int[] {96, 0, 0, 0, 0}, new int[] {80, 112, 0, 0, 0}, new int[] {74, 96, 118, 0, 0}, new int[] {57, 83, 109, 135, 0}, new int[] {58, 77, 96, 115, 134}};

    private static final int PLAYER_INVENTORY_START = 13;
    private static final int PLAYER_INVENTORY_END = 49;
    private static final int INPUT_START = CrystalFurnaceBlockEntity.INPUT_SLOTS[0];
    private static final int FUEL_START = CrystalFurnaceBlockEntity.FUEL_SLOTS[0];

    public CrystalFurnaceContainerMenu(int pContainerId, Level level, BlockPos pos, Inventory playerInventory, ContainerData data) {
        super(Registration.CRYSTAL_FURNACE_CONTAINER.get(), pContainerId, playerInventory);
        te = (CrystalFurnaceBlockEntity) level.getBlockEntity(pos);
        this.player = playerInventory.player;
        this.data = data;
        this.level = playerInventory.player.level();

        this.addFurnaceSlots(5, 5);
        this.addFuelSlots(3, 3);

        this.layoutPlayerInventorySlots(8, 109);
        this.addDataSlots(data);
    }

    /**
     * Called when shift clicking a slot with an item in it
     * Player inventory starts at 13 goes to 48
     * My slots are as expected
     * Pretty much copied from AbstractFurnaceMenu
     */
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
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
    public boolean stillValid(@NotNull Player pPlayer) {
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
        if (this.data.get(1) == 0) return 0;

        return this.data.get(0) / (float) this.data.get(1);
    }

    public boolean isLit() {
        return this.data.get(0) > 0;
    }

    public float getBurnProgress(int slot) {
        int index = ArrayUtils.indexOf(CrystalFurnaceBlockEntity.INPUT_SLOTS, slot);
        int i = this.data.get(index + 4);
        int j = this.data.get(index + 9);
        return j != 0 && i != 0 ? i / (float) j : 0;
    }

    public int getNumActiveSlots() {
        return this.data.get(2) + 1;
    }

    public int getNumActiveFuelSlots() {
        return this.data.get(3) + 1;
    }

    public CrystalFurnaceBlockEntity getBlockEntity() {
        return this.te;
    }

    public Player getPlayer() {
        return this.player;
    }

    protected boolean canSmelt(ItemStack pStack) {
        return this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(pStack), this.level).isPresent();
    }

    protected boolean isFuel(ItemStack stack) {
        return stack.getBurnTime(RecipeType.SMELTING) > 0;
    }

    // Levelable things

    public int getSkillPoints() {
        return this.data.get(14);
    }

    public void addSkillPoints(int points) {
        this.data.set(14, this.getSkillPoints() + points);
    }

    public void addToPoints(int nodeId, int value) {
        this.data.set(nodeId + 100, this.data.get(nodeId + 100) + value);
    }

    public int getExp() {
        return this.data.get(15);
    }

    public int getExpCap() {
        return this.data.get(16);
    }

    public int[] getPoints() {
        int[] result = new int[100];

        for (int i = 0; i < 100; i++) {
            result[i] = this.data.get(i + 100);
        }

        return result;
    }

    public String getBlockType() {
        return "crystal_furnace";
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

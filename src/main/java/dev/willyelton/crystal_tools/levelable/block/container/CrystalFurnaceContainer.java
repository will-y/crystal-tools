package dev.willyelton.crystal_tools.levelable.block.container;

import dev.willyelton.crystal_tools.levelable.block.ModBlocks;
import dev.willyelton.crystal_tools.levelable.block.container.slot.CrystalFurnaceFuelSlot;
import dev.willyelton.crystal_tools.levelable.block.container.slot.CrystalFurnaceInputSlot;
import dev.willyelton.crystal_tools.levelable.block.container.slot.CrystalFurnaceOutputSlot;
import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

public class CrystalFurnaceContainer extends AbstractContainerMenu {
    private final CrystalFurnaceBlockEntity te;
    private final InvWrapper playerInventory;
    private final Player player;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final int fuelSlotsX = 21;
    private final int[] fuelSlotsPos = new int[] {69, 44, 19};
    private final int inputSlotY = 58;
    private final int outputSlotY = 23;
    private final int[][] slotXValues = new int[][] {new int[] {96, 0, 0, 0, 0}, new int[] {80, 112, 0, 0, 0}, new int[] {74, 96, 118, 0, 0}, new int[] {57, 83, 109, 135, 0}, new int[] {58, 77, 96, 115, 134}};

    public CrystalFurnaceContainer(int pContainerId, Level level, BlockPos pos, Inventory playerInventory, ContainerData data, ContainerLevelAccess access) {
        super(ModBlocks.CRYSTAL_FURNACE_CONTAINER.get(), pContainerId);
        te = (CrystalFurnaceBlockEntity) level.getBlockEntity(pos);
        this.playerInventory = new InvWrapper(playerInventory);
        this.player = playerInventory.player;
        this.data = data;
        this.access = access;

        int numActiveSlots = this.data.get(3);
        int numActiveFuelSlots = this.data.get(4);

        this.addFurnaceSlots(5, 5);
        this.addFuelSlots(3, 3);

        this.layoutPlayerInventorySlots(8, 109);
        this.addDataSlots(data);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        // TODO: See how it is actually called
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return te.stillValid(pPlayer);
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    private void addFurnaceSlots(int numSlots, int numActiveSlots) {
        int[] inputSlots = te.getInputSlots();
        int[] outputSlots = te.getOutputSLots();

        for (int i = 0; i < numSlots; i++) {
            this.addSlot(new CrystalFurnaceInputSlot(te, inputSlots[i], this.slotXValues[numActiveSlots - 1][i], this.inputSlotY));
        }

        for (int i = 0; i < numSlots; i++) {
            this.addSlot(new CrystalFurnaceOutputSlot(player, te, outputSlots[i], this.slotXValues[numActiveSlots - 1][i], this.outputSlotY));
        }
    }

    private void addFuelSlots(int numSlots, int numActiveFuelSlots) {
        int[] slots = te.getFuelSlots();
        for (int i = 0; i < numSlots; i++) {
            this.addSlot(new CrystalFurnaceFuelSlot(te, slots[i], this.fuelSlotsX, this.fuelSlotsPos[i]));
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

    public CrystalFurnaceBlockEntity getBlockEntity() {
        return this.te;
    }

    public Player getPlayer() {
        return this.player;
    }

    // Levelable things

    public int getSkillPoints() {
        return this.data.get(14);
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

    public void addSkillPoints(int points) {
        this.setData(14, this.getSkillPoints() + points);
        this.access.execute(Level::blockEntityChanged);
//        this.data.set(14, this.getSkillPoints() + points);
        System.out.println("[" + Thread.currentThread().getName() + "] Setting skill points, value is now " + this.data.get(14));
//        this.broadcastChanges();
    }

    public void addToPoints(int index, int points) {

    }

    public void setData(int pId, int pData) {
        super.setData(pId, pData);
        this.broadcastChanges();
    }
}

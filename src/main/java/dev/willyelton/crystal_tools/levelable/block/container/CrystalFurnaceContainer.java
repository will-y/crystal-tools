package dev.willyelton.crystal_tools.levelable.block.container;

import dev.willyelton.crystal_tools.levelable.block.ModBlocks;
import dev.willyelton.crystal_tools.levelable.block.container.slot.CrystalFurnaceFuelSlot;
import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
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
    private final ContainerData data;
    private final int fuelSlotsX = 21;
    private final int[] fuelSlotsPos = new int[] {69, 44, 19};

    public CrystalFurnaceContainer(int pContainerId, Level level, BlockPos pos, Inventory playerInventory, ContainerData data) {
        super(ModBlocks.CRYSTAL_FURNACE_CONTAINER.get(), pContainerId);
        te = (CrystalFurnaceBlockEntity) level.getBlockEntity(pos);
        this.playerInventory = new InvWrapper(playerInventory);
        this.data = data;

        int numSlots = this.data.get(3);
        int numFuelSlots = this.data.get(4);

        this.addFurnaceSlots(5);
        this.addFuelSlots(3);

        this.layoutPlayerInventorySlots(8, 109);
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

    private void addFurnaceSlots(int numSlots) {
        int[] inputSlots = te.getInputSlots();
        int[] outputSlots = te.getOutputSLots();

        for (int i = 0; i < numSlots; i++) {
            this.addSlot(new CrystalFurnaceFuelSlot(te, inputSlots[i], 0, 0));
            this.addSlot(new CrystalFurnaceFuelSlot(te, outputSlots[i], 0, 0));
        }
    }

    private void addFuelSlots(int numSlots) {
        int[] slots = te.getFuelSlots();
        for (int i = 0; i < numSlots; i++) {
            this.addSlot(new CrystalFurnaceFuelSlot(te, slots[i], this.fuelSlotsX, this.fuelSlotsPos[i]));
        }
    }
}

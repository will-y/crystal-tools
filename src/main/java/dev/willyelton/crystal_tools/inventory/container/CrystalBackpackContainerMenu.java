package dev.willyelton.crystal_tools.inventory.container;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.inventory.container.slot.ReadOnlySlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class CrystalBackpackContainerMenu extends BaseContainerMenu {
    public static final int START_Y = 18;
    private static final int START_X = 8;
    private static final int SLOTS_PER_ROW = 9;
    // TODO: will be on stack or something
    private static final int ROWS = 9;
    private final CrystalBackpackInventory inventory;
    private final ItemStack stack;
    private int rows;

    // TODO: We need a server constructor and a client constructor
    // Everything will be synced via slots
    // Client constructor basically doesn't need anything and dummy data can be passed in
    // Don't think I will need to deal with the buffer?
    // The container data will sync ints
    // Will maybe need this for number of slots and other upgrades
    // Can probably store itemstack in buffer, then use that to get everything we need? (Don't need data?)

    // Client constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, new CrystalBackpackInventory(data.readInt() * 9), ItemStack.EMPTY);
    }

    // TODO: Don't let player move slot bag is in
    // Server constructor
    public CrystalBackpackContainerMenu(int containerId, Inventory playerInventory, CrystalBackpackInventory backpackInventory, ItemStack stack) {
        super(Registration.CRYSTAL_BACKPACK_CONTAINER.get(), containerId, playerInventory);
        this.inventory = backpackInventory;
        this.stack = stack;
        rows = backpackInventory.getSlots() / 9;
        this.layoutPlayerInventorySlots(START_X, START_Y + rows * 18 + 14);
        // TODO: Add slots
        this.addSlotBox(backpackInventory, 0, START_X, START_Y, SLOTS_PER_ROW, 18, rows, 18);
//        this.addDataSlots(containerData);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    protected void layoutHotbar(int leftCol, int topRow) {
        int index = 0;
        int x = leftCol;
        for (int i = 0 ; i < 9 ; i++) {
            if (playerInventory.getInv().getItem(index).is(Registration.CRYSTAL_BACKPACK.get())) {
                addSlot(new ReadOnlySlot(playerInventory, index, x, topRow));
            }
            addSlot(new SlotItemHandler(playerInventory, index, x, topRow));
            x += 18;
            index++;
        }
    }

    public int getRows() {
        return rows;
    }
}

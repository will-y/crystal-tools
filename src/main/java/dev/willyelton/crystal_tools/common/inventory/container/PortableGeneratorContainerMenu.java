package dev.willyelton.crystal_tools.common.inventory.container;

import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.inventory.ItemResourceHandlerAdapterModifiable;
import dev.willyelton.crystal_tools.common.inventory.PortableGeneratorInventory;
import dev.willyelton.crystal_tools.common.levelable.PortableGenerator;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalGeneratorBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.ILevelableContainerData;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.SimpleLevelableContainerData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;

public class PortableGeneratorContainerMenu extends AbstractGeneratorContainerMenu {
    public static final int START_X = 8;
    public static final int START_Y = 52;
    public static final int SLOT_SIZE = 18;
    public static final int SLOTS_PER_ROW = 9;

    private final Level level;
    private final PortableGeneratorInventory inventory;
    private final ItemStack stack;

    public PortableGeneratorContainerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, new SimpleLevelableContainerData(CrystalGeneratorBlockEntity.DATA_SIZE), ItemStack.OPTIONAL_STREAM_CODEC.decode(data));
    }

    public PortableGeneratorContainerMenu(int containerId, Inventory playerInventory, ILevelableContainerData data, ItemStack stack) {
        super(ModRegistration.PORTABLE_CRYSTAL_GENERATOR_CONTAINER.get(), containerId, playerInventory, data, 8, 137);

        this.level = playerInventory.player.level();
        this.inventory = PortableGenerator.getInventory(stack, level);
        this.stack = stack;

        this.addSlots(ItemResourceHandlerAdapterModifiable.of(inventory));
    }

    @Override
    public LevelableBlockEntity getBlockEntity() {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    public int getActiveSlots() {
        return inventory.size();
    }

    public ItemStack getGeneratorStack() {
        return stack;
    }

    private void addSlots(IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            int x = i % SLOTS_PER_ROW;
            int y = i / SLOTS_PER_ROW;
            this.addSlot(handler, i, START_X + x * SLOT_SIZE, START_Y + y * SLOT_SIZE);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // Player Inventory
            if (index < 36) {
                if (!this.moveItemStackTo(itemstack1, 36, 36 + this.getActiveSlots(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
}

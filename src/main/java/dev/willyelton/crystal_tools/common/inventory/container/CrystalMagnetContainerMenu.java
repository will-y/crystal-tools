package dev.willyelton.crystal_tools.common.inventory.container;

import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.inventory.container.slot.backpack.BackpackFilterSlot;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.FilterContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.FilterMenuContents;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenType;
import dev.willyelton.crystal_tools.utils.TransferUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemAccessItemHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.Objects;

// Needed to work with the filter screen I think, we will see
public class CrystalMagnetContainerMenu extends BaseContainerMenu implements SubScreenContainerMenu, FilterContainerMenu {
    public static final int START_Y = 18;
    private static final int START_X = 8;
    public static final int FILTER_SLOTS_PER_ROW = 9;

    private final ItemStack stack;
    private final int filterRows;
    private final FilterMenuContents<CrystalMagnetContainerMenu> filterMenuContents;

    public CrystalMagnetContainerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, ItemStack.OPTIONAL_STREAM_CODEC.decode(data), data.readInt());
    }

    public CrystalMagnetContainerMenu(int pContainerId, Inventory playerInventory, ItemStack stack, int slotIndex) {
        super(ModRegistration.CRYSTAL_MAGNET_CONTAINER.get(), pContainerId, playerInventory, null);

        this.stack = stack;
        this.filterRows = stack.getOrDefault(DataComponents.FILTER_CAPACITY, 0);
        this.filterMenuContents = new FilterMenuContents<>(this, filterRows * FILTER_SLOTS_PER_ROW, stack.getOrDefault(DataComponents.WHITELIST, true));

        setUpPlayerSlots();
        setUpFilterSlots();
    }

    private void setUpPlayerSlots() {
        this.layoutPlayerInventorySlots(START_X, START_Y + 3 * SLOT_SIZE + 14);
    }

    private void setUpFilterSlots() {
        if (Objects.nonNull(filterMenuContents.getInventory())) {
            int filterRows = getFilterRows();
            this.addSlotBox(filterMenuContents.getInventory(), TransferUtils.indexModifier(filterMenuContents.getInventory()),
                    0, START_X, START_Y, FILTER_SLOTS_PER_ROW, SLOT_SIZE, filterRows, SLOT_SIZE, filterMenuContents.getFilterSlots(),
                    BackpackFilterSlot::new);
        }
    }

    @Override
    public int getFilterRows() {
        return filterRows;
    }

    @Override
    public ResourceHandler<ItemResource> getFilterInventory() {
        if (filterRows == 0) {
            return null;
        }

        return new ItemAccessItemHandler(ItemAccess.forStack(stack), DataComponents.FILTER_INVENTORY.get(), filterRows * FILTER_SLOTS_PER_ROW);
    }

    @Override
    public FilterMenuContents<?> getFilterMenuContents() {
        return filterMenuContents;
    }

    @Override
    public void closeSubScreen() {

    }

    @Override
    public void openSubScreen(SubScreenType subScreenType) {

    }

    @Override
    public void setWhitelist(boolean whitelist) {
        FilterContainerMenu.super.setWhitelist(whitelist);
        stack.set(DataComponents.WHITELIST, whitelist);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 36) {
                return filterMenuContents.quickMove(itemstack);
            }
        }

        return itemstack;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0) {
            Slot slot = getSlot(slotId);

            if (slot instanceof BackpackFilterSlot filterSlot) {
                if (Objects.isNull(filterMenuContents.getInventory()) || clickType == ClickType.THROW || clickType == ClickType.CLONE) {
                    return;
                }
                filterSlot.onClicked(getCarried());
            } else {
                super.clicked(slotId, button, clickType, player);
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stack != null;
    }
}

package dev.willyelton.crystal_tools.inventory;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CrystalBackpackInventory extends ItemStackHandler {
    private ItemStack backpackStack;

    public CrystalBackpackInventory(int size) {
        super(size);
        this.backpackStack = ItemStack.EMPTY;
    }

    public CrystalBackpackInventory(ItemStack stack) {
        super((int) NBTUtils.getFloatOrAddKey(stack, "capacity", 1) * 9);
        this.backpackStack = stack;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return !stack.is(Registration.CRYSTAL_BACKPACK.get());
    }

    public ItemStack insertStack(ItemStack stack) {
        return ItemHandlerHelper.insertItem(this, stack, false);
    }

    public void sort(SortType sortType) {
        List<ItemStack> stacks = getAllStacks();
        stacks.sort(getComparator(sortType, stacks));

        InventoryUtils.clear(this);

        stacks.forEach(this::insertStack);
    }

    public int getLastStack() {
        for (int i = getSlots() - 1; i >= 0; i--) {
            if (!getStackInSlot(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    private Comparator<ItemStack> getComparator(SortType type, List<ItemStack> stacks) {
        return switch (type) {
            case QUANTITY -> quantityComparator(stacks).thenComparing(idComparator());
            case NAME -> nameComparator().thenComparing(idComparator());
            case MOD -> modComparator().thenComparing(idComparator());
            case ID -> idComparator();
        };
    }

    private List<ItemStack> getAllStacks() {
        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);

            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }

        return stacks;
    }

    private Comparator<ItemStack> quantityComparator(List<ItemStack> stacks) {
        Int2IntMap map = new Int2IntArrayMap();

        for (ItemStack stack : stacks) {
            map.merge(getItemId(stack), stack.getCount(), Integer::sum);
        }

        return Comparator.comparingInt((ItemStack stack) -> this.getItemQuantity(stack, map)).reversed();
    }

    private Comparator<ItemStack> nameComparator() {
        return Comparator.comparing(this::getItemName);
    }

    private Comparator<ItemStack> modComparator() {
        return Comparator.comparing(this::getItemMod);
    }

    private Comparator<ItemStack> idComparator() {
        return Comparator.comparing(this::getItemId);
    }

    private int getItemQuantity(ItemStack stack, Int2IntMap quantities) {
        return quantities.get(getItemId(stack));
    }

    private String getItemName(ItemStack stack) {
        return stack.getDisplayName().getString();
    }

    private String getItemMod(ItemStack stack) {
        return stack.getItem().getCreatorModId(stack);
    }

    private int getItemId(ItemStack stack) {
        return Item.getId(stack.getItem());
    }

    public enum SortType {
        QUANTITY, NAME, MOD, ID
    }
}

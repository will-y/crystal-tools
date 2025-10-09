package dev.willyelton.crystal_tools.common.inventory;

import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CrystalBackpackInventory extends ListComponentItemHandler {

    // TODO: Do I even need this constructor?
    public CrystalBackpackInventory(int size) {
        super(ItemAccess.forStack(new ItemStack(ModRegistration.CRYSTAL_BACKPACK)), DataComponents.BACKPACK_INVENTORY.get(), size);
    }

    public CrystalBackpackInventory(ItemStack stack) {
        super(ItemAccess.forStack(stack), DataComponents.BACKPACK_INVENTORY.get(), (stack.getOrDefault(DataComponents.CAPACITY, 0) + 1) * 9);
    }

    @Override
    public boolean isValid(int slot, ItemResource resource) {
        return !resource.is(ModRegistration.CRYSTAL_BACKPACK.get());
    }

    public ItemStack insertStack(ItemStack stack) {
        return ItemUtil.insertItemReturnRemaining(this, stack, false, null);
    }

    public void sort(Level level, SortType sortType) {
        List<ItemStack> stacks = getAllStacks();
        stacks.sort(getComparator(level, sortType, stacks));

        InventoryUtils.clear(ItemResourceHandlerAdapterModifiable.of(this));

        stacks.forEach(this::insertStack);
    }

    public int getLastStack() {
        for (int i = size() - 1; i >= 0; i--) {
            if (!this.getResource(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    private Comparator<ItemStack> getComparator(Level level, SortType type, List<ItemStack> stacks) {
        return switch (type) {
            case QUANTITY -> quantityComparator(stacks).thenComparing(idComparator());
            case NAME -> nameComparator().thenComparing(idComparator());
            case MOD -> modComparator(level).thenComparing(idComparator());
            case ID -> idComparator();
        };
    }

    private List<ItemStack> getAllStacks() {
        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < size(); i++) {
            ItemResource resource = getResource(i);
            if (!resource.isEmpty()) {
                resource.toStack(getAmountAsInt(i));
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

    private Comparator<ItemStack> modComparator(Level level) {
        return Comparator.comparing(stack -> getItemMod(level, stack));
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

    private String getItemMod(Level level, ItemStack stack) {
        return stack.getItem().getCreatorModId(level.registryAccess(), stack);
    }

    private int getItemId(ItemStack stack) {
        return Item.getId(stack.getItem());
    }

    public enum SortType {
        QUANTITY, NAME, MOD, ID
    }
}

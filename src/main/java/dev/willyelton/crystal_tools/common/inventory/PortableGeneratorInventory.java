package dev.willyelton.crystal_tools.common.inventory;

import com.google.common.base.Predicates;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.datamap.GeneratorFuelData;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;

import static dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalGeneratorBlockEntity.getFuelData;

public class PortableGeneratorInventory extends ListComponentItemHandler {
    // TODO: Move to BE maybe?
    public static final int MAX_SIZE = 27;

    private final ItemStack generatorStack;
    // Has to be a better way?
    private @Nullable Level level;

    public PortableGeneratorInventory(int size) {
        super(ItemAccess.forStack(new ItemStack(ModRegistration.PORTABLE_GENERATOR)), DataComponents.BACKPACK_INVENTORY.get(), Math.min(size, MAX_SIZE));
        this.generatorStack = ItemStack.EMPTY.copy();
    }

    public PortableGeneratorInventory(ItemStack stack) {
        super(ItemAccess.forStack(stack), DataComponents.BACKPACK_INVENTORY.get(), Math.min(stack.getOrDefault(DataComponents.PORTABLE_GENERATOR_SLOTS, 0) + 1, MAX_SIZE));

        this.generatorStack = stack;
    }

    @Override
    public boolean isValid(int slot, ItemResource resource) {
        if (resource.isEmpty()) {
            return true;
        }
        boolean canFit = !resource.is(ModRegistration.PORTABLE_GENERATOR) && super.isValid(slot, resource);
        if (level == null) return canFit;

        return canFit && canBurn(generatorStack, resource.toStack(), level);
    }

    public ItemStack insertStack(ItemStack stack) {
        return ItemHandlerHelper.insertItem(ItemResourceHandlerAdapterModifiable.of(this), stack, false);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public ItemStack nextItem() {
        return ItemStackUtils.nextFuelItem(ItemResourceHandlerAdapterModifiable.of(this), Predicates.alwaysTrue());
    }

    public static boolean canBurn(ItemStack generatorStack, ItemStack stack, Level level) {
        GeneratorFuelData newFuelData = getFuelData(stack, generatorStack.getOrDefault(DataComponents.FOOD_GENERATOR, false),
                generatorStack.getOrDefault(DataComponents.METAL_GENERATOR, false), generatorStack.getOrDefault(DataComponents.GEM_GENERATOR, false));
        return newFuelData != null || stack.getBurnTime(RecipeType.SMELTING, level.fuelValues()) > 0;
    }
}

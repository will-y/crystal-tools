package dev.willyelton.crystal_tools.capability;

import dev.willyelton.crystal_tools.inventory.CrystalBackpackInventory;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrystalBackpackCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    private final ItemStack stack;

    public CrystalBackpackCapabilityProvider(ItemStack stack) {
        this.stack = stack;
    }

    private CrystalBackpackInventory inventory;
    private LazyOptional<IItemHandler> lazyInitializationSupplier = LazyOptional.of(this::getCachedInventory);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (ForgeCapabilities.ITEM_HANDLER == cap) {
            return lazyInitializationSupplier.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        // TODO: Looks to be called every tick? Seems wrong. Debug to see what calls?
        CompoundTag tag = new CompoundTag();
        tag.put("inventory", getCachedInventory().serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        getCachedInventory().deserializeNBT(nbt.getCompound("inventory"));
    }

    private CrystalBackpackInventory getCachedInventory() {
        if (inventory == null) {
            inventory = new CrystalBackpackInventory(stack);
        } else {
            CrystalBackpackInventory newInventory = new CrystalBackpackInventory(stack);
            if (inventory.getSlots() != newInventory.getSlots()) {
                newInventory.copyFrom(inventory);
                inventory = newInventory;
                // This is probably not a good thing to do, but it works
                lazyInitializationSupplier = LazyOptional.of(() -> inventory);
            }
        }
        return inventory;
    }
}

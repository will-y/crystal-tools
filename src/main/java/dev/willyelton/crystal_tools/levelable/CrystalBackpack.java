package dev.willyelton.crystal_tools.levelable;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.capability.CrystalBackpackCapabilityProvider;
import dev.willyelton.crystal_tools.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.levelable.LevelableItem;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CrystalBackpack extends Item implements LevelableItem {

    public CrystalBackpack() {
        super(new Item.Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        // TODO: Open inventory
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer,
                    new CrystalBackpackMenuSupplier(this, stack),
                    friendlyByteBuf -> friendlyByteBuf.writeInt((int) NBTUtils.getFloatOrAddKey(stack, "capacity", 1)));
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack tool, Level level, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity entity) {
        // If this tool is disabled break on use
        if (this.isDisabled()) {
            tool.shrink(1);
            return false;
        }

        return true;
    }

    @Override
    public String getItemType() {
        return "backpack";
    }

    @Override
    public int getMaxDamage(ItemStack itemStack) {
        return 1;
    }

    @Override
    public boolean isDisabled() {
        // TODO: Config
        return false;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new CrystalBackpackCapabilityProvider(stack);
    }

    private record CrystalBackpackMenuSupplier(CrystalBackpack backpackItem, ItemStack stack) implements MenuProvider {
        @Override
        public Component getDisplayName() {
            return stack.getHoverName();
        }

        @Override
        public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
            // Server side constructor
            return new CrystalBackpackContainerMenu(containerId, playerInventory, getInventory(stack), stack);
        }
    }

    public static List<ItemStack> findBackpackStacks(Player player) {
        return player.getInventory().items.stream().filter(stack -> stack.is(Registration.CRYSTAL_BACKPACK.get())).toList();
    }

    public static CrystalBackpackInventory getInventory(ItemStack stack) {
        IItemHandler inventory = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseGet(() -> {
            CrystalTools.LOGGER.error("Crystal Backpack cannot find capability");
            return new CrystalBackpackInventory(0);
        });

        if (inventory instanceof CrystalBackpackInventory crystalBackpackInventory) {
            return crystalBackpackInventory;
        } else {
            CrystalTools.LOGGER.error("Different inventory capability found on crystal backpack");
            return new CrystalBackpackInventory(0);
        }
    }

    public static List<ItemStack> getFilterItems(ItemStack stack) {
        if (!stack.is(Registration.CRYSTAL_BACKPACK.get())) {
            throw new IllegalArgumentException("Cannot get filter items on  " + stack.getItem());
        }

        CompoundTag tag = stack.getTag();

        if (tag == null || tag.isEmpty()) {
            return Collections.emptyList();
        }

        CompoundTag filterTag = tag.getCompound("filter");

        if (filterTag.isEmpty()) {
            return Collections.emptyList();
        }

        int filterSize = filterTag.getInt("Size");

        ItemStackHandler filterInventory = new ItemStackHandler(filterSize);
        filterInventory.deserializeNBT(filterTag);

        List<ItemStack> results = new ArrayList<>();

        for (int i = 0; i < filterSize; i++) {
            ItemStack stackInSlot = filterInventory.getStackInSlot(i);
            if (!stackInSlot.isEmpty()) {
                results.add(stackInSlot.copy());
            }
        }

        return results;
    }
}

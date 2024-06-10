package dev.willyelton.crystal_tools.common.levelable;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.compat.curios.CuriosCompatibility;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CrystalBackpack extends Item implements LevelableItem {
    public CrystalBackpack() {
        super(new Properties().stacksTo(1).fireResistant());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (player instanceof ServerPlayer serverPlayer) {
            openBackpack(serverPlayer, stack);
        }

        return InteractionResultHolder.success(stack);
    }

    public void openBackpack(ServerPlayer serverPlayer, ItemStack backpackStack) {
        serverPlayer.openMenu(
                new CrystalBackpackMenuSupplier(this, backpackStack),
                friendlyByteBuf -> {
                    // TODO: Maybe want to actually set here?
                    friendlyByteBuf.writeInt(backpackStack.getOrDefault(DataComponents.CAPACITY, 1));
                    friendlyByteBuf.writeInt(backpackStack.getOrDefault(DataComponents.FILTER_CAPACITY, 0));
                    friendlyByteBuf.writeBoolean(backpackStack.getOrDefault(DataComponents.WHITELIST, false));
                    friendlyByteBuf.writeBoolean(backpackStack.getOrDefault(DataComponents.SORT_ENABLED, false));
                });
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        ItemStack backpackStack = context.getItemInHand();

        if (backpackStack.getOrDefault(DataComponents.INVENTORY_STORE, false)) {
            CrystalBackpackInventory backpackInventory = getInventory(backpackStack);

            if (blockEntity != null && backpackInventory != null) {
                IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, context.getClickedPos(), context.getClickedFace());
                if (itemHandler != null) {
                    for (int i = 0; i < backpackInventory.getSlots(); i++) {
                        backpackInventory.setStackInSlot(i, ItemHandlerHelper.insertItem(itemHandler, backpackInventory.getStackInSlot(i),false));
                    }
                }

                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
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
        return CrystalToolsConfig.DISABLE_BACKPACK.get();
    }

//    @Override
//    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
//        return new CrystalBackpackCapabilityProvider(stack);
//    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> components, TooltipFlag flag) {
//        components.add(Component.literal("\u00A7d\u00A7l" + "The backpack is currently a WIP. Please report any issues or make suggestions."));
        components.add(Component.literal("\u00A74\u00A7l" + "The backpack is currently broken. Will be fixed in 1.21"));
        String toolTip = "\u00A79" + "Auto Pickup " + (itemStack.getOrDefault(DataComponents.PICKUP_DISABLED, false) ? "Disabled" : "Enabled");
        components.add(Component.literal(toolTip));
        ToolUtils.appendHoverText(itemStack, components, flag, this);
    }

    // TODO: This should be a default method in the interface? New method for base experience?
    @Override
    public int getExperienceCap(ItemStack tool) {
        return tool.getOrDefault(DataComponents.EXPERIENCE_CAP, CrystalToolsConfig.BACKPACK_BASE_EXPERIENCE_CAP.get());
    }

    private record CrystalBackpackMenuSupplier(CrystalBackpack backpackItem, ItemStack stack) implements MenuProvider {
        @Override
            public Component getDisplayName() {
                return stack.getHoverName();
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                // Server side constructor
                return new CrystalBackpackContainerMenu(containerId, playerInventory, getInventory(stack), stack,
                        stack.getOrDefault(DataComponents.FILTER_CAPACITY, 0),
                        stack.getOrDefault(DataComponents.WHITELIST, true),
                        stack.getOrDefault(DataComponents.SORT_ENABLED, false));
            }
        }

    public static List<ItemStack> findBackpackStacks(Player player) {
        List<ItemStack> backpackStacks = CuriosCompatibility.getCrystalBackpacksInCurios(player);
        backpackStacks.addAll(player.getInventory().items.stream().filter(stack -> stack.is(Registration.CRYSTAL_BACKPACK.get())).toList());

        return backpackStacks;
    }

    public static CrystalBackpackInventory getInventory(ItemStack stack) {
        IItemHandler inventory = stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (inventory == null) {
            CrystalTools.LOGGER.error("Crystal Backpack cannot find capability");
            return new CrystalBackpackInventory(0);
        } else if (inventory instanceof CrystalBackpackInventory crystalBackpackInventory) {
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

        ItemContainerContents filterContents = stack.get(DataComponents.FILTER_INVENTORY);

        if (filterContents == null) {
            return Collections.emptyList();
        }

        return filterContents.stream().filter(stack1 -> !stack1.isEmpty()).toList();
    }

    /**
     * Adds exp to all backpacks in the player's inventory
     * @param player Player to search inventory and give xp to backpacks in
     * @param exp Amount to add to each backpack
     */
    public static void addXpToBackpacks(Player player, int exp) {
        findBackpackStacks(player)
                .forEach(stack -> {
                    LevelableItem item = (LevelableItem) stack.getItem();
                    item.addExp(stack, player.level(), player.blockPosition(), player, exp);
                });
    }
}

package dev.willyelton.crystal_tools.common.levelable;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.compat.curios.CuriosCompatibility;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
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

import java.util.Collections;
import java.util.List;

public class CrystalBackpack extends Item implements LevelableItem {
    public CrystalBackpack() {
        super(new Properties().stacksTo(1).fireResistant());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (player instanceof ServerPlayer serverPlayer) {
            openBackpack(serverPlayer, stack, serverPlayer.getInventory().findSlotMatchingItem(stack));
        }

        return InteractionResultHolder.success(stack);
    }

    public void openBackpack(ServerPlayer serverPlayer, ItemStack backpackStack, int slotIndex) {
        serverPlayer.openMenu(
                new CrystalBackpackMenuSupplier(this, backpackStack, slotIndex),
                friendlyByteBuf -> {
                    ItemStack.OPTIONAL_STREAM_CODEC.encode(friendlyByteBuf, backpackStack);
                    friendlyByteBuf.writeInt(slotIndex);
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
    public boolean mineBlock(ItemStack tool, Level level, BlockState blockState, BlockPos blockPos, LivingEntity entity) {
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

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> components, TooltipFlag flag) {
        if (itemStack.getOrDefault(DataComponents.BACKPACK_AUTO_PICKUP, false)) {
            String toolTip = "\u00A79" + "Auto Pickup " + (itemStack.getOrDefault(DataComponents.PICKUP_DISABLED, false) ? "Disabled" : "Enabled");
            components.add(Component.literal(toolTip));
        }

        appendLevelableHoverText(itemStack, components, this, flag);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack backpackStack, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY) return false;

        CrystalBackpackInventory inventory = getInventory(backpackStack);
        ItemStack toInsert = slot.getItem();

        if (toInsert.isEmpty()) {
            // Take item out of backpack and put in player inventory
            int lastSlotIndex = inventory.getLastStack();
            if (lastSlotIndex != -1) {
                ItemStack toGiveToPlayer = inventory.getStackInSlot(lastSlotIndex);
                ItemStack result = slot.safeInsert(toGiveToPlayer);
                inventory.setStackInSlot(lastSlotIndex, result);
                playRemoveOneSound(player);
                return true;
            }
        } else {
            // Take item from player's inventory and add it to backpack
            if (toInsert.getItem().canFitInsideContainerItems()) {
                ItemStack result = inventory.insertStack(toInsert);
                slot.set(result);
                playInsertSound(player);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack backpackStack, ItemStack toInsert, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action != ClickAction.SECONDARY) return false;

        CrystalBackpackInventory inventory = getInventory(backpackStack);

        if (toInsert.isEmpty()) {
            // Take item out of backpack
            int lastSlotIndex = inventory.getLastStack();
            if (lastSlotIndex != -1) {
                ItemStack toGiveToPlayer = inventory.getStackInSlot(lastSlotIndex);
                access.set(toGiveToPlayer);
                playRemoveOneSound(player);
                return true;
            }
        } else {
            // Put held item into backpack
            ItemStack result = inventory.insertStack(toInsert.copy());
            toInsert.setCount(result.getCount());
            playInsertSound(player);
            return true;
        }

        return false;
    }

    @Override
    // TODO: Call this when inserting into backpack
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    // TODO: This should be a default method in the interface? New method for base experience?
    @Override
    public int getExperienceCap(ItemStack tool) {
        return tool.getOrDefault(DataComponents.EXPERIENCE_CAP, CrystalToolsConfig.BACKPACK_BASE_EXPERIENCE_CAP.get());
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private record CrystalBackpackMenuSupplier(CrystalBackpack backpackItem, ItemStack stack, int slotIndex) implements MenuProvider {
        @Override
            public Component getDisplayName() {
                return stack.getHoverName();
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                // Server side constructor
                return new CrystalBackpackContainerMenu(containerId, playerInventory, stack, slotIndex);
            }
        }

    public static List<ItemStack> findBackpackStacks(Player player) {
        List<ItemStack> backpackStacks = CuriosCompatibility.getCrystalBackpacksInCurios(player);
        backpackStacks.addAll(player.getInventory().items.stream().filter(stack -> stack.is(Registration.CRYSTAL_BACKPACK.get())).toList());

        return backpackStacks;
    }

    public static int findNextBackpackSlot(Player player) {
        List<ItemStack> curiosStacks = CuriosCompatibility.getCrystalBackpacksInCurios(player);

        if (!curiosStacks.isEmpty()) {
            // TODO: Won't work if you have multiple backpacks in curios
            return -2;
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(Registration.CRYSTAL_BACKPACK.get())) {
                return i;
            }
        }

        return -1;
    }

    public static ItemStack getBackpackFromSlotIndex(Player player, int slotIndex) {
        if (slotIndex == -1) {
            return ItemStack.EMPTY;
        }

        if (slotIndex == -2) {
            List<ItemStack> curiosStacks = CuriosCompatibility.getCrystalBackpacksInCurios(player);
            if (curiosStacks.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                return curiosStacks.get(0);
            }
        }

        return player.getInventory().getItem(slotIndex);
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

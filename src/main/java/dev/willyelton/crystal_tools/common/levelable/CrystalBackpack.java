package dev.willyelton.crystal_tools.common.levelable;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.compat.curios.CuriosCompatibility;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;
import java.util.function.Consumer;

public class CrystalBackpack extends Item implements LevelableItem {
    public CrystalBackpack(Item.Properties properties) {
        super(properties.stacksTo(1).fireResistant());
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (player instanceof ServerPlayer serverPlayer) {
            openBackpack(serverPlayer, stack, serverPlayer.getInventory().findSlotMatchingItem(stack));
        }

        return InteractionResult.SUCCESS;
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
                ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, context.getClickedPos(), context.getClickedFace());

                if (handler != null) {
                    for (int i = 0; i < backpackInventory.size(); i++) {
                        ItemResource resource = backpackInventory.getResource(i);

                        if (resource.isEmpty()) continue;


                        try (Transaction tx = Transaction.open(null)) {
                            int amountExtracted = backpackInventory.extract(i, resource, 64, tx);

                            int leftOver = amountExtracted - handler.insert(resource, amountExtracted, tx);

                            if (leftOver > 0) {
                                backpackInventory.insert(i, resource, leftOver, tx);
                            }

                            tx.commit();
                        }
                    }
                }

                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }

    @Override
    public int getMaxDamage(ItemStack itemStack) {
        return 1;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag flag) {
        if (itemStack.getOrDefault(DataComponents.BACKPACK_AUTO_PICKUP, false)) {
            String toolTip = "\u00A79" + "Auto Pickup " + (itemStack.getOrDefault(DataComponents.PICKUP_DISABLED, false) ? "Disabled" : "Enabled");
            components.accept(Component.literal(toolTip));
        }
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
                ItemResource resource = inventory.getResource(lastSlotIndex);
                if (!resource.isEmpty()) {
                    try (Transaction tx = Transaction.open(null)) {
                        int extracted = inventory.extract(lastSlotIndex, resource, 64, tx);
                        ItemStack toGiveToPlayer = resource.toStack(extracted);
                        ItemStack leftOver = slot.safeInsert(toGiveToPlayer);
                        inventory.insert(lastSlotIndex, resource, leftOver.getCount(), tx);
                        playRemoveOneSound(player);
                        tx.commit();
                    }
                }
                return true;
            }
        } else {
            // Take item from player's inventory and add it to backpack
            if (toInsert.canFitInsideContainerItems()) {
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
                ItemResource resource = inventory.getResource(lastSlotIndex);
                if (!resource.isEmpty()) {
                    try (Transaction tx = Transaction.open(null)) {
                        int extracted = inventory.extract(lastSlotIndex, resource, 64, tx);
                        ItemStack toGiveToPlayer = resource.toStack(extracted);
                        access.set(toGiveToPlayer);
                        playRemoveOneSound(player);
                        tx.commit();
                    }
                }

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
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private record CrystalBackpackMenuSupplier(CrystalBackpack backpackItem, ItemStack stack,
                                               int slotIndex) implements MenuProvider {
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
        return InventoryUtils.findAll(player, stack -> stack.is(ModRegistration.CRYSTAL_BACKPACK.get()));
    }

    public static int findNextBackpackSlot(Player player) {
        List<ItemStack> curiosStacks = CuriosCompatibility.getCrystalBackpacksInCurios(player);

        if (!curiosStacks.isEmpty()) {
            // TODO: Won't work if you have multiple backpacks in curios
            return -2;
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(ModRegistration.CRYSTAL_BACKPACK.get())) {
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
        ResourceHandler<ItemResource> handler = ItemAccess.forStack(stack).getCapability(Capabilities.Item.ITEM);

        if (handler == null) {
            CrystalTools.LOGGER.error("Crystal Backpack cannot find capability");
            return new CrystalBackpackInventory(0);
        } else if (handler instanceof CrystalBackpackInventory crystalBackpackInventory) {
            return crystalBackpackInventory;
        } else {
            CrystalTools.LOGGER.error("Different inventory capability found on crystal backpack");
            return new CrystalBackpackInventory(0);
        }
    }

    /**
     * Adds exp to all backpacks in the player's inventory
     *
     * @param player Player to search inventory and give xp to backpacks in
     * @param exp    Amount to add to each backpack
     */
    public static void addXpToBackpacks(Player player, int exp) {
        findBackpackStacks(player)
                .forEach(stack -> {
                    Levelable levelable = stack.getCapability(dev.willyelton.crystal_tools.common.capability.Capabilities.ITEM_SKILL, player.level().registryAccess());
                    if (levelable != null) {
                        levelable.addExp(player.level(), player.blockPosition(), player, exp);
                    }
                });
    }
}

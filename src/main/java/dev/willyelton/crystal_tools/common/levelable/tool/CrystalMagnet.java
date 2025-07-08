package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.client.events.RegisterKeyBindingsEvent;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalMagnetContainerMenu;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CrystalMagnet extends LevelableTool {
    public CrystalMagnet(Properties properties) {
        super(properties.durability(2000)
                .component(DataComponents.WHITELIST, false));
    }

    // If this becomes a performance issue, can do something similar to the projectile tracking?
    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);

        if (!stack.getOrDefault(DataComponents.DISABLED, false)) {
            // 0.2 good max speed
            // 0.01 start?
            float speed = 0.01F + stack.getOrDefault(DataComponents.ITEM_SPEED, 0.0F);
            float size = getRange(stack);

            List<ItemStack> filterStacks = ToolUtils.getFilterItems(stack);
            AtomicInteger affected = new AtomicInteger();
            List<Entity> entities = level.getEntities(entity, AABB.ofSize(entity.position(), size * 2, size * 2, size * 2), createPredicate(stack));
            entities.forEach(e -> {
                if (e instanceof ItemEntity itemEntity) {
                    if (itemEntity.hasPickUpDelay()) {
                        return;
                    }

                    if (!ToolUtils.matchesFilter(stack, itemEntity.getItem(), filterStacks)) {
                        return;
                    }

                    if (stack.getOrDefault(DataComponents.INSTANT_PICKUP, false) && entity instanceof Player player) {
                        itemEntity.playerTouch(player);
                        stack.hurtAndBreak(1, player, slot);
                        return;
                    }
                }
                e.push(entity.position().subtract(e.position()).normalize().scale(speed));
                e.setData(Registration.MAGNET_ITEM.get(), true);
                affected.getAndIncrement();
            });


            if (affected.get() > 0 && level.getGameTime() % 5 == 0 && entity instanceof LivingEntity livingEntity) {
                stack.hurtAndBreak(affected.get(), livingEntity, slot);
            }
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player instanceof ServerPlayer serverPlayer) {
            openMagnetFilter(serverPlayer, stack, serverPlayer.getInventory().findSlotMatchingItem(stack));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag flag) {
        components.accept(Component.literal(String.format("Range: %.1f Blocks", getRange(stack))));
        if (stack.getOrDefault(DataComponents.PULL_MOBS, false)) {
            String toolTip;
            if (stack.getOrDefault(DataComponents.DISABLE_MOB_PULL, false)) {
                toolTip = "\u00A7c\u00A7l" + "Mob Pulling Disabled";
            } else {
                toolTip = "\u00A79" + "Mob Pulling Enabled";
            }

            if (RegisterKeyBindingsEvent.TOGGLE_MAGNET != null) {
                toolTip = toolTip + " (shift + " + RegisterKeyBindingsEvent.TOGGLE_MAGNET.getKey().getDisplayName().getString() + " to change)";
            }
            components.accept(Component.literal(toolTip));
        }
    }

    public void openMagnetFilter(ServerPlayer serverPlayer, ItemStack stack, int slotIndex) {
        serverPlayer.openMenu(
                new CrystalMagnet.CrystalMagnetMenuSupplier(this, stack, slotIndex),
                friendlyByteBuf -> {
                    ItemStack.OPTIONAL_STREAM_CODEC.encode(friendlyByteBuf, stack);
                    friendlyByteBuf.writeInt(slotIndex);
                });
    }

    private Predicate<Entity> createPredicate(ItemStack stack) {
        boolean includeExp = stack.getOrDefault(DataComponents.PULL_XP, false);
        boolean includeMobs = stack.getOrDefault(DataComponents.PULL_MOBS, false) && !stack.getOrDefault(DataComponents.DISABLE_MOB_PULL, false);

        return e -> {
            if (e instanceof ItemEntity) return true;

            if (includeExp && e instanceof ExperienceOrb) return true;

            return includeMobs && e instanceof LivingEntity livingEntity
                    && livingEntity.isPushable()
                    && !(livingEntity instanceof Player);
        };
    }

    private float getRange(ItemStack stack) {
        return (float) CrystalToolsConfig.MAGNET_BASE_RANGE.get().doubleValue() + stack.getOrDefault(DataComponents.MAGNET_RANGE, 0.0F);
    }

    private record CrystalMagnetMenuSupplier(CrystalMagnet magnetItem, ItemStack stack,
                                             int slotIndex) implements MenuProvider {
        @Override
        public Component getDisplayName() {
            return stack.getHoverName();
        }

        @Override
        public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
            // Server side constructor
            return new CrystalMagnetContainerMenu(containerId, playerInventory, stack, slotIndex);
        }
    }
}

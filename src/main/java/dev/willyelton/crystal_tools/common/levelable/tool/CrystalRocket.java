package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.client.events.RegisterKeyBindingsEvent;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

public class CrystalRocket extends LevelableTool {
    public CrystalRocket() {
        super(new Item.Properties(), null, "crystal_rocket", -4, 0, 100);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return use(player.getItemInHand(hand), level, player, hand);
    }

    public InteractionResultHolder<ItemStack> use(ItemStack stack, Level level, Player player, InteractionHand hand) {
        if (this.isDisabled()) {
            stack.shrink(1);
            return InteractionResultHolder.fail(stack);
        }

        if (ToolUtils.isBroken(stack)) {
            return InteractionResultHolder.pass(stack);
        }

        if (player.isFallFlying()) {
            if (!level.isClientSide) {
                int flightTime = stack.getOrDefault(DataComponents.FLIGHT_TIME, 1);
                Fireworks fireworks = new Fireworks(flightTime, Collections.emptyList());
                stack.set(net.minecraft.core.component.DataComponents.FIREWORKS, fireworks);
                FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(level, stack, player);
                level.addFreshEntity(fireworkrocketentity);

                player.awardStat(Stats.ITEM_USED.get(this));
            }

            addExp(stack, level, player.getOnPos(), player, CrystalToolsConfig.ROCKET_EXPERIENCE_BOOST.get());
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        } else {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean inHand) {
        levelableInventoryTick(itemStack, level, entity, inventorySlot, inHand, CrystalToolsConfig.ROCKET_REPAIR_MODIFIER.get());
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_ROCKET.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        tooltipComponents.add(Component.literal(String.format("Press %s while this is in your inventory to automatically use!", RegisterKeyBindingsEvent.TRIGGER_ROCKET.getKey().getDisplayName().getString())));
        super.appendHoverText(stack, context, tooltipComponents, flag);
    }
}

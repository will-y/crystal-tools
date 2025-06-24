package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.client.events.RegisterKeyBindingsEvent;
import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.function.Consumer;

public class CrystalRocket extends LevelableTool {
    public CrystalRocket(Item.Properties properties) {
        super(properties
                .repairable(CrystalToolsTags.REPAIRS_CRYSTAL)
                .durability(100));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return use(player.getItemInHand(hand), level, player, hand);
    }

    public InteractionResult use(ItemStack stack, Level level, Player player, InteractionHand hand) {
        if (this.isDisabled()) {
            stack.shrink(1);
            return InteractionResult.FAIL;
        }

        if (ToolUtils.isBroken(stack)) {
            return InteractionResult.PASS;
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

            Levelable levelable = stack.getCapability(Capabilities.ITEM_SKILL, level.registryAccess());
            if (levelable != null) {
                levelable.addExp(level, player.getOnPos(), player);
            }

            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        levelableInventoryTick(itemStack, level, entity, slot, CrystalToolsConfig.ROCKET_REPAIR_MODIFIER.get());
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_ROCKET.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        tooltipComponents.accept(Component.literal(String.format("Press %s while this is in your inventory to automatically use!", RegisterKeyBindingsEvent.TRIGGER_ROCKET.getKey().getDisplayName().getString())));
        super.appendHoverText(stack, context, display, tooltipComponents, flag);
    }
}

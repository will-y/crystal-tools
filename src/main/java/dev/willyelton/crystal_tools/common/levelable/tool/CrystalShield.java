package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.levelable.EntityTargeter;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.List;

public class CrystalShield extends ShieldItem implements LevelableItem, EntityTargeter {

    public CrystalShield() {
        super(new Item.Properties().durability(1000).component(net.minecraft.core.component.DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        refreshTarget(stack, level, player);

        return super.use(level, player, hand);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
//        if (level.getGameTime() % 10 == 0) {
            refreshTarget(stack, level, livingEntity);
//        }

        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        clearTarget(stack, entity.level());
        super.onStopUsing(stack, entity, count);
    }

    @Override
    public String getItemType() {
        return "shield";
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return super.getMaxDamage(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        appendLevelableHoverText(stack, tooltipComponents, this, tooltipFlag);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.isSameItem(oldStack, newStack);
    }
}

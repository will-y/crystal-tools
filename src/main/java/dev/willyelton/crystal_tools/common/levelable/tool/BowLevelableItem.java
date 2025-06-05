package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.events.LevelTickEvent;
import dev.willyelton.crystal_tools.common.levelable.EntityTargeter;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BowLevelableItem extends BowItem implements LevelableItem, EntityTargeter {
    public BowLevelableItem(Item.Properties properties) {
        super(properties.durability(CRYSTAL.durability()).fireResistant().repairable(CrystalToolsTags.REPAIRS_CRYSTAL));
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            boolean creative = player.getAbilities().instabuild;

            ItemStack itemstack;

            if (hasInfinity(stack, level)) {
                itemstack = new ItemStack(Items.ARROW);
            } else {
                itemstack = getProjectile(stack, player);
            }

            int timeUsed = this.getUseDuration(stack, entity) - timeLeft;

            timeUsed = EventHooks.onArrowLoose(stack, level, player, timeUsed, !itemstack.isEmpty() || creative);
            if (timeUsed < 0) return false;

            if (!itemstack.isEmpty() || creative) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.ARROW);
                }

                float power = getPower(timeUsed, stack);
                if (((double) power >= 0.1D)) {
                    boolean infinity = creative || (itemstack.getItem() instanceof ArrowItem && ((ArrowItem) itemstack.getItem()).isInfinite(itemstack, stack, player));
                    if (!level.isClientSide) {
                        ArrowItem arrowitem = (ArrowItem) (itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
                        AbstractArrow abstractarrow = arrowitem.createArrow(level, itemstack, player, stack);
                        abstractarrow = customArrow(abstractarrow);
                        //TODO: Too Random
                        float speed = power * 3.0F + stack.getOrDefault(DataComponents.ARROW_SPEED, 0F) / 4.0F;
                        abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed, 0.0F);
                        if (power == 1.0F) {
                            abstractarrow.setCritArrow(true);
                        }

                        float j = stack.getOrDefault(DataComponents.ARROW_DAMAGE.get(), 0F);
                        if (j > 0) {
                            abstractarrow.setBaseDamage(2 + (double)j + 0.5D);
                        }

                        if (hasFlame(stack, level)) {
                            abstractarrow.setRemainingFireTicks(200);
                        }

                        if (abstractarrow instanceof Arrow arrow) {
                            List<MobEffectInstance> effects = stack.getOrDefault(DataComponents.EFFECTS, Collections.emptyList());
                            effects.forEach(arrow::addEffect);
                        }

                        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                        if (infinity || hasInfinity(stack, level)) {
                            abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }

                        level.addFreshEntity(abstractarrow);
                        int target = stack.getOrDefault(DataComponents.ENTITY_TARGET, -1);
                        if (target != -1) {
                            LevelTickEvent.startTracking(level, abstractarrow.getId(), target, speed / 2);
                        }
                    }

                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
                    if (!infinity) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            player.getInventory().removeItem(itemstack);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }

        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        refreshTarget(stack, level, player);
        if (this.isDisabled()) {
            stack.shrink(1);
            return InteractionResult.FAIL;
        }
        boolean flag = !getProjectile(stack, player).isEmpty() || hasInfinity(stack, level);

        InteractionResult ret = EventHooks.onArrowNock(stack, level, player, hand, flag);
        if (ret != null) return ret;

        if (ToolUtils.isBroken(stack) || (!player.getAbilities().instabuild && !flag)) {
            return InteractionResult.FAIL;
        } else {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        refreshTarget(stack, level, livingEntity);

        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        clearTarget(stack, entity.level());
        super.onStopUsing(stack, entity, count);
    }

    public AbstractArrow customArrow(AbstractArrow arrow) {
        return arrow;
    }

    public ItemStack getProjectile(ItemStack shootable, Player player) {
        return player.getProjectile(shootable);
    }

    @Override
    public String getItemType() {
        return "bow";
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        levelableInventoryTick(itemStack, level, entity, slot, 1);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag flag) {
        appendLevelableHoverText(itemStack, components, this, flag, context);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
        int durability = this.getMaxDamage(stack) - stack.getDamageValue();

        if (durability - amount <= 0) {
            return 0;
        } else {
            return amount;
        }
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_BOW.get();
    }

    public static float getChargeTime(ItemStack stack) {
        return Math.max(1F, 20F - stack.getOrDefault(DataComponents.DRAW_SPEED, 0F));
    }

    private float getPower(int charge, ItemStack stack) {
        float f = (float) charge / getChargeTime(stack);
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    private boolean hasInfinity(ItemStack stack, Level level) {
        Optional<Holder.Reference<Enchantment>> infinityHolder = level.holderLookup(Registries.ENCHANTMENT).get(Enchantments.INFINITY);

        return infinityHolder.filter(enchantmentReference -> stack.getEnchantmentLevel(enchantmentReference) > 0).isPresent();
    }

    private boolean hasFlame(ItemStack stack, Level level) {
        Optional<Holder.Reference<Enchantment>> infinityHolder = level.holderLookup(Registries.ENCHANTMENT).get(Enchantments.FLAME);

        return infinityHolder.filter(enchantmentReference -> stack.getEnchantmentLevel(enchantmentReference) > 0).isPresent();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        // Just ignore data components for now
        return !newStack.is(oldStack.getItem());
    }
}

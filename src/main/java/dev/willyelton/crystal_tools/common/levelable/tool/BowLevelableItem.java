package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.components.EffectData;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.events.LevelTickEvent;
import dev.willyelton.crystal_tools.common.levelable.EntityTargeter;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BowLevelableItem extends BowItem implements LevelableItem, EntityTargeter {
    public BowLevelableItem() {
        super(new Properties().durability(INITIAL_TIER.durability()).fireResistant());
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            boolean creative = player.getAbilities().instabuild;

            ItemStack itemstack;

            if (stack.getOrDefault(DataComponents.INFINITY, false)) {
                itemstack = new ItemStack(Items.ARROW);
            } else {
                itemstack = getProjectile(stack, player);
            }

            int timeUsed = this.getUseDuration(stack, entity) - timeLeft;

            timeUsed = EventHooks.onArrowLoose(stack, level, player, timeUsed, !itemstack.isEmpty() || creative);
            // TODO (PORTING): Check
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
                            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double)j + 0.5D);
                        }

                        if (stack.getOrDefault(DataComponents.FLAME, false)) {
                            abstractarrow.setRemainingFireTicks(100);
                        }

                        if (abstractarrow instanceof Arrow arrow) {
                            List<EffectData> effects = stack.getOrDefault(DataComponents.EFFECTS, Collections.emptyList());

                            for (EffectData effect : effects) {
                                Optional<Holder.Reference<MobEffect>> mobEffect = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.withDefaultNamespace(effect.resourceLocation()));
                                if (mobEffect.isPresent()) {
                                    MobEffectInstance instance = new MobEffectInstance(mobEffect.get(), effect.duration() * 20, 1, false, false);
                                    arrow.addEffect(instance);
                                }
                            }
                        }

                        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                        if (infinity || stack.getOrDefault(DataComponents.INFINITY, false)) {
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

        // TODO (PORTING): What does this do now?
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
        boolean flag = !getProjectile(stack, player).isEmpty() || stack.getOrDefault(DataComponents.INFINITY, false);

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
    public int getMaxDamage(ItemStack stack) {
        int bonusDurability = stack.getOrDefault(DataComponents.DURABILITY_BONUS, 0);
        return INITIAL_TIER.durability() + bonusDurability;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    // Changing these two to what they should be @minecraft
    @Override
    public int getBarWidth(ItemStack itemStack) {
        return Math.round(13.0F - (float) itemStack.getDamageValue() * 13.0F / (float) itemStack.getMaxDamage());
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        float f = Math.max(0.0F, ((float)itemStack.getMaxDamage() - (float)itemStack.getDamageValue()) / (float) itemStack.getMaxDamage());
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean inHand) {
        levelableInventoryTick(itemStack, level, entity, inventorySlot, inHand, 1);
    }

    // TODO (PORTING): In properties now
    public boolean isValidRepairItem(ItemStack tool, ItemStack repairItem) {
        return repairItem.is(Registration.CRYSTAL.get());
    }

    // TODO (PORTING): In properties now probably;
//    public int getEnchantmentValue() {
//        return INITIAL_TIER.getEnchantmentValue();
//    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> components, TooltipFlag flag) {
        appendLevelableHoverText(itemStack, components, this, flag);
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

    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_BOW.get();
    }

    // TODO (PORTING): In properties now
    public boolean isEnchantable(ItemStack stack) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }

    public float getChargeTime(ItemStack stack) {
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

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        // Just ignore data components for now
        return !newStack.is(oldStack.getItem());
    }
}

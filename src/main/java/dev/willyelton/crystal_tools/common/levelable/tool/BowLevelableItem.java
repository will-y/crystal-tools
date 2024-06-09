package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.DataComponents;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class BowLevelableItem extends BowItem implements LevelableItem {
    public BowLevelableItem() {
        super(new Properties().durability(INITIAL_TIER.getUses()).fireResistant());
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            boolean flag = player.getAbilities().instabuild;

            ItemStack itemstack;

            if (stack.getOrDefault(DataComponents.INFINITY, false)) {
                itemstack = new ItemStack(Items.ARROW);
            } else {
                itemstack = getProjectile(stack, player);
            }

            int timeUsed = this.getUseDuration(stack) - timeLeft;

            // TODO: Forge hasn't patched this in yet, BowItem might have slightly different logic
            timeUsed = EventHooks.onArrowLoose(stack, level, player, timeUsed, !itemstack.isEmpty() || flag);
            if (timeUsed < 0) return;

            if (!itemstack.isEmpty() || flag) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.ARROW);
                }

                float power = getPower(timeUsed, stack);
                if (((double) power >= 0.1D)) {
                    boolean flag1 = player.getAbilities().instabuild || (itemstack.getItem() instanceof ArrowItem && ((ArrowItem)itemstack.getItem()).isInfinite(itemstack, stack, player));
                    if (!level.isClientSide) {
                        ArrowItem arrowitem = (ArrowItem) (itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
                        AbstractArrow abstractarrow = arrowitem.createArrow(level, itemstack, player);
                        abstractarrow = customArrow(abstractarrow);
                        //TODO: TOO Random
                        abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F + stack.getOrDefault(DataComponents.ARROW_SPEED, 0F) / 4.0F, 0.0F);
                        if (power == 1.0F) {
                            abstractarrow.setCritArrow(true);
                        }

                        float j = stack.getOrDefault(DataComponents.ARROW_DAMAGE.get(), 0F);
                        if (j > 0) {
                            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double)j + 0.5D);
                        }

                        int k = stack.getOrDefault(DataComponents.ARROW_KNOCKBACK, 0);
                        if (k > 0) {
                            abstractarrow.setKnockback(k);
                        }

                        if (stack.getOrDefault(DataComponents.FLAME, false)) {
                            abstractarrow.setRemainingFireTicks(100);
                        }

                        // TODO: Effects here and on apple will have to be redone
                        // List<Integer> dataComponent to store all of them?
                        // Packet handler will have to change to specifically allow that but here should be similar
//                        if (abstractarrow instanceof Arrow arrow && stack.getTag() != null) {
//                            for (int i = 1; i < 34; i++) {
//                                if (stack.getTag().contains("effect_" + i)) {
//                                    MobEffect effect = MobEffect.byId(i);
//                                    if (effect != null) {
//                                        MobEffectInstance instance = new MobEffectInstance(effect, (int) NBTUtils.getFloatOrAddKey(stack, "effect_" + i) * 20, 1, false, true);
//                                        arrow.addEffect(instance);
//                                    }
//                                }
//                            }
//                        }

                        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                        if (flag1 || player.getAbilities().instabuild && (itemstack.is(Items.SPECTRAL_ARROW) || itemstack.is(Items.TIPPED_ARROW)) || stack.getOrDefault(DataComponents.INFINITY, false)) {
                            abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }

                        level.addFreshEntity(abstractarrow);
                    }

                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
                    if (!flag1 && !player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            player.getInventory().removeItem(itemstack);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.isDisabled()) {
            itemstack.shrink(1);
            return InteractionResultHolder.fail(itemstack);
        }
        boolean flag = !getProjectile(itemstack, pPlayer).isEmpty() || itemstack.getOrDefault(DataComponents.INFINITY, false);

        InteractionResultHolder<ItemStack> ret = EventHooks.onArrowNock(itemstack, pLevel, pPlayer, pHand, flag);
        // TODO: I don't think this is actually always true?
        if (ret != null) return ret;

        if (ToolUtils.isBroken(itemstack) || (!pPlayer.getAbilities().instabuild && !flag)) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            pPlayer.startUsingItem(pHand);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.BOW;
    }

    public @NotNull AbstractArrow customArrow(@NotNull AbstractArrow arrow) {
        return arrow;
    }

    public ItemStack getProjectile(ItemStack shootable, Player player) {
        if (!(shootable.getItem() instanceof BowLevelableItem)) {
            return ItemStack.EMPTY;
        } else {
            Predicate<ItemStack> predicate = getAllSupportedProjectiles();
            ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(player, predicate);
            if (!itemstack.isEmpty()) {
                return CommonHooks.getProjectile(player, shootable, itemstack);
            } else {
                for(int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                    ItemStack itemstack1 = player.getInventory().getItem(i);
                    if (predicate.test(itemstack1)) {
                        return CommonHooks.getProjectile(player, shootable, itemstack1);
                    }
                }

                return CommonHooks.getProjectile(player, shootable, player.getAbilities().instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY);
            }
        }
    }

    @Override
    public String getItemType() {
        return "bow";
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int bonusDurability = stack.getOrDefault(DataComponents.DURABILITY_BONUS, 0);
        return INITIAL_TIER.getUses() + bonusDurability;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
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
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int inventorySlot, boolean inHand) {
        ToolUtils.inventoryTick(itemStack, level, entity, inventorySlot, inHand);
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack tool, @NotNull ItemStack repairItem) {
        return repairItem.is(Registration.CRYSTAL.get());
    }

    @Override
    public int getEnchantmentValue() {
        return INITIAL_TIER.getEnchantmentValue();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, Item.TooltipContext context, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        ToolUtils.appendHoverText(itemStack, components, flag, this);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Runnable onBroken) {
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

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get() &&
                (super.canApplyAtEnchantingTable(stack, enchantment) || stack.is(enchantment.getSupportedItems()));
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
}

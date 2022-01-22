package dev.willyelton.crystal_tools.tool;

import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

import static net.minecraft.world.item.BowItem.getPowerForTime;
import static net.minecraft.world.item.ProjectileWeaponItem.ARROW_ONLY;

public class BowLevelableTool extends LevelableItem {
    public static final int BASE_DRAW_DURATION = 20;
    public static final int BASE_RANGE = 15;

    public BowLevelableTool() {
        super(new Properties(), "bow");
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        if (pEntityLiving instanceof Player player) {
            boolean flag = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, pStack) > 0;
            ItemStack itemstack = getProjectile(pStack, player);

            int i = this.getUseDuration(pStack) - pTimeLeft;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(pStack, pLevel, player, i, !itemstack.isEmpty() || flag);
            if (i < 0) return;

            if (!itemstack.isEmpty() || flag) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.ARROW);
                }

                float f = getPowerForTime(i);
                System.out.println("Power: " + f);
                if (!((double)f < 0.1D)) {
                    boolean flag1 = player.getAbilities().instabuild || (itemstack.getItem() instanceof ArrowItem && ((ArrowItem)itemstack.getItem()).isInfinite(itemstack, pStack, player));
                    if (!pLevel.isClientSide) {
                        ArrowItem arrowitem = (ArrowItem)(itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
                        AbstractArrow abstractarrow = arrowitem.createArrow(pLevel, itemstack, player);
                        abstractarrow = customArrow(abstractarrow);
                        //TODO: TOO Random
                        abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F + NBTUtils.getFloatOrAddKey(pStack, "arrow_speed_bonus") / 4.0F, 0.0F);
                        if (f == 1.0F) {
                            abstractarrow.setCritArrow(true);
                        }

//                        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, pStack);
                        float j = NBTUtils.getFloatOrAddKey(pStack, "arrow_damage");
                        if (j > 0) {
                            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double)j + 0.5D);
                        }

//                        int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, pStack);
                        int k = (int) NBTUtils.getFloatOrAddKey(pStack, "arrow_knockback");
                        if (k > 0) {
                            abstractarrow.setKnockback(k);
                        }

                        if (NBTUtils.getFloatOrAddKey(pStack, "flame") > 0) {
                            abstractarrow.setSecondsOnFire(100);
                        }

                        pStack.hurtAndBreak(1, player, (p_40665_) -> {
                            p_40665_.broadcastBreakEvent(player.getUsedItemHand());
                        });
                        if (flag1 || player.getAbilities().instabuild && (itemstack.is(Items.SPECTRAL_ARROW) || itemstack.is(Items.TIPPED_ARROW))) {
                            abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }

                        pLevel.addFreshEntity(abstractarrow);
                    }

                    pLevel.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
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
        boolean flag = !getProjectile(itemstack, pPlayer).isEmpty();

//        InteractionResultHolder<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, pLevel, pPlayer, pHand, flag);
//        if (ret != null) return ret;

        if (!pPlayer.getAbilities().instabuild && !flag) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            pPlayer.startUsingItem(pHand);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    // TODO: Change this by level
    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    public AbstractArrow customArrow(AbstractArrow arrow) {
        return arrow;
    }

    public ItemStack getProjectile(ItemStack pShootable, Player player) {
        if (!(pShootable.getItem() instanceof BowLevelableTool)) {
            return ItemStack.EMPTY;
        } else {
            Predicate<ItemStack> predicate = getAllSupportedProjectiles();
            ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(player, predicate);
            if (!itemstack.isEmpty()) {
                return itemstack;
            } else {
                for(int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                    ItemStack itemstack1 = player.getInventory().getItem(i);
                    if (predicate.test(itemstack1)) {
                        return itemstack1;
                    }
                }

                return player.getAbilities().instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
            }
        }
    }

    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }
}

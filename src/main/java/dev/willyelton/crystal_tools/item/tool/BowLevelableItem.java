package dev.willyelton.crystal_tools.item.tool;

import dev.willyelton.crystal_tools.CreativeTabs;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.item.LevelableItem;
import dev.willyelton.crystal_tools.item.ModItems;
import dev.willyelton.crystal_tools.utils.NBTUtils;
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
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BowLevelableItem extends BowItem implements LevelableItem {
    public BowLevelableItem() {
        super(new Properties().defaultDurability(tier.getUses()).fireResistant().tab(CreativeTabs.CRYSTAL_TOOLS_TAB));
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        if (pEntityLiving instanceof Player player) {
            boolean flag = player.getAbilities().instabuild;

            ItemStack itemstack;

            if (NBTUtils.getFloatOrAddKey(pStack, "infinity") > 0) {
                itemstack = new ItemStack(Items.ARROW);
            } else {
                itemstack = getProjectile(pStack, player);
            }

            int i = this.getUseDuration(pStack) - pTimeLeft;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(pStack, pLevel, player, i, !itemstack.isEmpty() || flag);
            if (i < 0) return;

            if (!itemstack.isEmpty() || flag) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.ARROW);
                }

                float f = getPowerForTime(i);
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
                        if (flag1 || player.getAbilities().instabuild && (itemstack.is(Items.SPECTRAL_ARROW) || itemstack.is(Items.TIPPED_ARROW)) || NBTUtils.getFloatOrAddKey(pStack, "infinity") > 0) {
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
        if (this.isDisabled()) {
            itemstack.shrink(1);
            return InteractionResultHolder.fail(itemstack);
        }

        boolean flag = !getProjectile(itemstack, pPlayer).isEmpty() || NBTUtils.getFloatOrAddKey(itemstack, "infinity") > 0;

        InteractionResultHolder<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, pLevel, pPlayer, pHand, flag);
        if (ret != null) return ret;

        if (ToolUtils.isBroken(itemstack) || (!pPlayer.getAbilities().instabuild && !flag)) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            pPlayer.startUsingItem(pHand);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    // TODO: Change this by level
    // Doesn't work, try something else later
    @Override
    public int getUseDuration(ItemStack stack) {
//        return Math.max(72000 - (int) (5000 * NBTUtils.getFloatOrAddKey(stack, "draw_speed")), 20000);
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
        if (!(pShootable.getItem() instanceof BowLevelableItem)) {
            return ItemStack.EMPTY;
        } else {
            Predicate<ItemStack> predicate = getSupportedHeldProjectiles();
            ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(player, predicate);
            if (!itemstack.isEmpty()) {
                return ForgeHooks.getProjectile(player, pShootable, itemstack);
            } else {
                predicate = getAllSupportedProjectiles();

                for(int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                    ItemStack itemstack1 = player.getInventory().getItem(i);
                    if (predicate.test(itemstack1)) {
                        return ForgeHooks.getProjectile(player, pShootable, itemstack1);
                    }
                }

                return ForgeHooks.getProjectile(player, pShootable, player.getAbilities().instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY);
            }
        }
    }

    @Override
    public String getItemType() {
        return "bow";
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int bonusDurability = (int) NBTUtils.getFloatOrAddKey(stack, "durability_bonus");
        return tier.getUses() + bonusDurability;
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
        ToolUtils.inventoryTick(itemStack, level, entity, inventorySlot, inHand);
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack tool, @NotNull ItemStack repairItem) {
        return repairItem.is(ModItems.CRYSTAL.get());
    }

    @Override
    public int getEnchantmentValue() {
        return tier.getEnchantmentValue();
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        ToolUtils.appendHoverText(itemStack, level, components, flag, this);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        int durability = this.getMaxDamage(stack) - (int) NBTUtils.getFloatOrAddKey(stack, "Damage");

        if (durability - amount <= 0) {
            return 0;
        } else {
            return amount;
        }
    }

    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_BOW.get();
    }
}

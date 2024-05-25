package dev.willyelton.crystal_tools.levelable.tool;


import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.entity.CrystalTridentEntity;
import dev.willyelton.crystal_tools.renderer.CrystalTridentBlockEntityWithoutLevelRenderer;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CrystalTrident extends SwordLevelableTool {
    // TODO: This attack and attack speed things don't do anything
    // Change levelable tool to do base and sword to do bonus only probably
    public CrystalTrident() {
        super("trident", 3, -2.9F);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack pStack, @NotNull BlockState pState) {
        return 1.0F;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState block) {
        return false;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return false;
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_TRIDENT.get();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get() && enchantment.category.equals(EnchantmentCategory.TRIDENT);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return CrystalTridentBlockEntityWithoutLevelRenderer.INSTANCE;
            }
        });
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(itemstack);
        } else if (EnchantmentHelper.getRiptide(itemstack) > 0 && !player.isInWaterOrRain()) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            int timeUsed = this.getUseDuration(stack) - timeLeft;
            if (timeUsed >= 10) {
                int riptideLevel = (int) NBTUtils.getFloatOrAddKey(stack, "riptide");
                if (riptideLevel <= 0 || canRiptide(stack, player)) {
                    if (!level.isClientSide) {
                        stack.hurtAndBreak(1, player, (p_43388_) -> {
                            p_43388_.broadcastBreakEvent(entityLiving.getUsedItemHand());
                        });
                        // TODO: Mode switch stuff
                        if (riptideLevel == 0) {
                            CrystalTridentEntity tridentEntity = new CrystalTridentEntity(level, player, stack);
                            // TODO: Speed + riptide + more
                            tridentEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F + (float)riptideLevel * 0.5F, 1.0F);
                            if (player.getAbilities().instabuild) {
                                // TODO: Infinite Throw
                                tridentEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                            }

                            level.addFreshEntity(tridentEntity);
                            level.playSound(null, tridentEntity, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                            // TODO: Infinite Throw
                            if (!player.getAbilities().instabuild) {
                                player.getInventory().removeItem(stack);
                            }
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    // Do riptide things
                    if (riptideLevel > 0) {
                        float playerYRot = player.getYRot();
                        float playerXRot = player.getXRot();
                        float playerPushX = -Mth.sin(playerYRot * ((float)Math.PI / 180F)) * Mth.cos(playerXRot * ((float)Math.PI / 180F));
                        float playerPushY = -Mth.sin(playerXRot * ((float)Math.PI / 180F));
                        float playerPushZ = Mth.cos(playerYRot * ((float)Math.PI / 180F)) * Mth.cos(playerXRot * ((float)Math.PI / 180F));
                        float playerSpeed = Mth.sqrt(playerPushX * playerPushX + playerPushY * playerPushY + playerPushZ * playerPushZ);
                        float playerPushMagnitude = 3.0F * ((1.0F + (float) riptideLevel) / 4.0F);
                        playerPushX *= playerPushMagnitude / playerSpeed;
                        playerPushY *= playerPushMagnitude / playerSpeed;
                        playerPushZ *= playerPushMagnitude / playerSpeed;
                        player.push(playerPushX, playerPushY, playerPushZ);
                        // TODO: Level this?
                        player.startAutoSpinAttack(20);
                        if (player.onGround()) {
                            player.move(MoverType.SELF, new Vec3(0.0D, 1.1999999F, 0.0D));
                        }

                        SoundEvent soundevent;
                        if (riptideLevel >= 3) {
                            soundevent = SoundEvents.TRIDENT_RIPTIDE_3;
                        } else if (riptideLevel == 2) {
                            soundevent = SoundEvents.TRIDENT_RIPTIDE_2;
                        } else {
                            soundevent = SoundEvents.TRIDENT_RIPTIDE_1;
                        }

                        level.playSound(null, player, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    private boolean canRiptide(ItemStack stack, Player player) {
        return player.isInWaterOrRain();
    }
}

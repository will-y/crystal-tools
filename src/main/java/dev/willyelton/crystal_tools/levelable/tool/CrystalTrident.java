package dev.willyelton.crystal_tools.levelable.tool;


import dev.willyelton.crystal_tools.DataComponents;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.entity.CrystalTridentEntity;
import dev.willyelton.crystal_tools.renderer.CrystalTridentBlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.ToolAction;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CrystalTrident extends SwordLevelableTool {
    public CrystalTrident() {
        super("trident", 4, -2.9F);
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
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return false;
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_TRIDENT.get();
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
        if (this.isDisabled()) {
            itemstack.shrink(1);
        }

        if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(itemstack);
        } else if (riptideEnabled(itemstack) && !canRiptide(itemstack, player)) {
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
                int riptideLevel = stack.getOrDefault(DataComponents.RIPTIDE, 0);
                if (!level.isClientSide) {
                    stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(entityLiving.getUsedItemHand()));

                    if (!riptideEnabled(stack)) {
                        CrystalTridentEntity tridentEntity = new CrystalTridentEntity(level, player, stack);
                        float velocity = 2.5F + stack.getOrDefault(DataComponents.PROJECTILE_SPEED, 0F) * 0.5F;
                        tridentEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity, 1.0F);
                        if (player.getAbilities().instabuild) {
                            tridentEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }

                        level.addFreshEntity(tridentEntity);
                        level.playSound(null, tridentEntity, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                        if (!player.getAbilities().instabuild) {
                            player.getInventory().removeItem(stack);
                        }
                    }
                }

                player.awardStat(Stats.ITEM_USED.get(this));
                // Do riptide things
                if (canRiptide(stack, player)) {
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

    @Override
    protected double getExperienceBoost() {
        return CrystalToolsConfig.TRIDENT_EXPERIENCE_BOOST.get();
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack tool, Level level, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity entity) {
        return false;
    }

    private boolean canRiptide(ItemStack stack, Player player) {
        if (stack.getOrDefault(DataComponents.RIPTIDE_DISABLED, false)) return false;

        if (stack.getOrDefault(DataComponents.ALWAYS_RIPTIDE, false)) return true;

        return stack.getOrDefault(DataComponents.RIPTIDE, 0) > 0 && player.isInWaterOrRain();
    }

    private boolean riptideEnabled(ItemStack stack) {
        if (stack.getOrDefault(DataComponents.RIPTIDE_DISABLED, false)) return false;

        if (stack.getOrDefault(DataComponents.ALWAYS_RIPTIDE, false)) return true;

        return stack.getOrDefault(DataComponents.RIPTIDE, 0) > 0;
    }
}

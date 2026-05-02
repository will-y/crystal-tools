package dev.willyelton.crystal.tools.common.entity;

import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.core.common.capability.Capabilities;
import dev.willyelton.crystal.core.common.capability.LevelableStack;
import dev.willyelton.crystal.tools.common.components.DataComponents;
import dev.willyelton.crystal.tools.common.config.CrystalToolsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

// TODO: Mixin instead of overwriting everything
public class CrystalTridentEntity extends ThrownTrident {
    public static final String CRYSTAL_TOOLS_TRIDENT_LIGHTNING_TAG = "crystal_tools.trident.lightning";

    protected ItemStack tridentStack = new ItemStack(ModRegistration.CRYSTAL_TRIDENT.get());
    protected boolean dealtDamage;
    protected int clientSideReturnTridentTickCount;

    public CrystalTridentEntity(EntityType<? extends CrystalTridentEntity> entityType, Level level) {
        super(entityType, level);
    }

    public CrystalTridentEntity(Level level, LivingEntity shooter, ItemStack stack) {
        super(level, shooter, stack);
        this.tridentStack = stack;
    }

    @Override
    public EntityType<?> getType() {
        return ModRegistration.CRYSTAL_TRIDENT_ENTITY.get();
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        Entity entity = this.getOwner();
        int loyaltyLevel = this.entityData.get(ID_LOYALTY);
        // If loyalty
        if (loyaltyLevel > 0 && (this.dealtDamage || this.isNoPhysics()) && entity != null) {
            if (!this.isAcceptableReturnOwner()) {
                // Drop if player with loyalty dies while it is returning
                if (!this.level().isClientSide() && this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation((ServerLevel) this.level(), this.getPickupItem(), 0.1F);
                }

                this.discard();
            } else {
                if (tridentStack.getOrDefault(DataComponents.INSTANT_LOYALTY, false) && entity instanceof Player player) {
                    if (player.getInventory().add(this.getPickupItem())) {
                        this.discard();
                    }
                }
                this.setNoPhysics(true);
                Vec3 moveDirection = entity.getEyePosition().subtract(this.position());
                this.setPosRaw(this.getX(), this.getY() + moveDirection.y * 0.015D * (double) loyaltyLevel, this.getZ());
                if (this.level().isClientSide()) {
                    this.yOld = this.getY();
                }

                double d0 = 0.05D * (double) loyaltyLevel;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(moveDirection.normalize().scale(d0)));
                if (this.clientSideReturnTridentTickCount == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                }

                this.clientSideReturnTridentTickCount++;
            }
        }
        super.tick();
    }

    @Override
    protected ItemStack getPickupItem() {
        return tridentStack.copy();
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModRegistration.CRYSTAL_TRIDENT.get());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity hitEntity = result.getEntity();
        float damage = 8.0F + tridentStack.getOrDefault(DataComponents.PROJECTILE_DAMAGE, 0F);
        DamageSource damagesource = this.damageSources().trident(this, (this.getOwner() == null ? this : this.getOwner()));
        if (level() instanceof ServerLevel level) {
            damage += EnchantmentHelper.modifyDamage(level, tridentStack, hitEntity, damagesource, damage);
        }

        Entity damagingEntity = this.getOwner();
        this.dealtDamage = true;
        if (hitEntity.hurtOrSimulate(damagesource, damage)) {
            if (hitEntity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (this.level() instanceof ServerLevel serverlevel1) {
                EnchantmentHelper.doPostAttackEffectsWithItemSourceOnBreak(
                        serverlevel1, hitEntity, damagesource, this.getWeaponItem(), p_478671_ -> this.kill(serverlevel1)
                );
            }

            if (hitEntity instanceof LivingEntity livingentity) {
                this.doKnockback(livingentity, damagesource);
                this.doPostHurtEffects(livingentity);
            }

            if (damagingEntity instanceof Player player) {
                LevelableStack levelable = tridentStack.getCapability(Capabilities.ITEM_SKILL, level().registryAccess());
                if (levelable != null && levelable.allowDamageXp()) {
                    levelable.addExp(level(), damagingEntity.getOnPos(), player, damage);
                }
            }
        }

        if (!this.summonLightning(hitEntity.blockPosition())) {
            this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
        }

        this.deflect(ProjectileDeflection.REVERSE, hitEntity, this.owner, false);
        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (CrystalToolsConfig.ALWAYS_CHANNEL.get()) {
            this.summonLightning(result.getBlockPos());
        }
        super.onHitBlock(result);
    }

    private boolean isChanneling() {
        return tridentStack.getOrDefault(DataComponents.CHANNELING, 0) > 0;
    }

    private boolean isAcceptableReturnOwner() {
        Entity entity = this.getOwner();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayer) || !entity.isSpectator();
        } else {
            return false;
        }
    }

    private boolean summonLightning(BlockPos blockPos) {
        if (!this.level().isClientSide() && this.isChanneling()) {
            blockPos = blockPos.above();
            if (this.level().canSeeSky(blockPos)) {
                LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(this.level(), EntitySpawnReason.TRIGGERED);
                if (lightningbolt != null) {
                    int damage = 5 + tridentStack.getOrDefault(DataComponents.CHANNELING, 0);
                    lightningbolt.setDamage(damage);
                    lightningbolt.snapTo(Vec3.atBottomCenterOf(blockPos));
                    lightningbolt.setCause(this.getOwner() instanceof ServerPlayer ? (ServerPlayer) this.getOwner() : null);
                    lightningbolt.addTag(CRYSTAL_TOOLS_TRIDENT_LIGHTNING_TAG);
                    this.level().addFreshEntity(lightningbolt);
                    this.playSound(SoundEvents.TRIDENT_THUNDER.value(), 5.0F, 1.0F);
                    return true;
                }
            }
        }

        return false;
    }
}

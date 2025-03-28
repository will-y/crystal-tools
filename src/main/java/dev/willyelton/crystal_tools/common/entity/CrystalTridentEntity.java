package dev.willyelton.crystal_tools.common.entity;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.tool.CrystalTrident;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class CrystalTridentEntity extends AbstractArrow {
    public static final String CRYSTAL_TOOLS_TRIDENT_LIGHTNING_TAG = "crystal_tools.trident.lightning";
    private static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.defineId(CrystalTridentEntity.class, EntityDataSerializers.BYTE);

    protected ItemStack tridentStack = new ItemStack(Registration.CRYSTAL_TRIDENT.get());
    protected CrystalTrident tridentItem = Registration.CRYSTAL_TRIDENT.get();
    protected boolean dealtDamage;
    protected int clientSideReturnTridentTickCount;

    public CrystalTridentEntity(EntityType<? extends CrystalTridentEntity> entityType, Level level) {
        super(entityType, level);
    }

    public CrystalTridentEntity(Level level, LivingEntity shooter, ItemStack stack) {
        super(Registration.CRYSTAL_TRIDENT_ENTITY.get(), shooter, level, stack, null);
        this.tridentStack = stack;
        this.entityData.set(ID_LOYALTY, stack.getOrDefault(DataComponents.LOYALTY, 0).byteValue());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_LOYALTY, (byte) 0);
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
                if (!this.level().isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
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
                if (this.level().isClientSide) {
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
        return new ItemStack(Registration.CRYSTAL_TRIDENT.get());
    }

    @Override
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return this.dealtDamage ? null : super.findHitEntity(pStartVec, pEndVec);
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

            if (hitEntity instanceof LivingEntity livingEntity) {
                if (damagingEntity instanceof LivingEntity && level() instanceof ServerLevel level) {
                    EnchantmentHelper.doPostAttackEffectsWithItemSource(level, livingEntity, damagesource, tridentStack);
                }

                this.doPostHurtEffects(livingEntity);
            }

            if (damagingEntity instanceof Player player) {
                tridentItem.addExp(tridentStack, level(), damagingEntity.getOnPos(), player, (int) damage);
            }
        }

        if (!this.summonLightning(hitEntity.blockPosition())) {
            this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (CrystalToolsConfig.ALWAYS_CHANNEL.get()) {
            this.summonLightning(result.getBlockPos());
        }
        super.onHitBlock(result);
    }

    @Override
    protected boolean tryPickup(Player player) {
        return super.tryPickup(player) || this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public void playerTouch(Player entity) {
        if (this.ownedBy(entity) || this.getOwner() == null) {
            super.playerTouch(entity);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.dealtDamage = pCompound.getBoolean("DealtDamage").orElse(false);
        this.entityData.set(ID_LOYALTY, tridentStack.getOrDefault(DataComponents.LOYALTY, 0).byteValue());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    public void tickDespawn() {
        int loyaltyLevel = this.entityData.get(ID_LOYALTY);
        if (this.pickup != AbstractArrow.Pickup.ALLOWED || loyaltyLevel <= 0) {
            super.tickDespawn();
        }
    }

    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
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
        if (!this.level().isClientSide && this.isChanneling()) {
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

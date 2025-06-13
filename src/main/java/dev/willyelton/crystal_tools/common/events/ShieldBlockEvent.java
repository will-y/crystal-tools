package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class ShieldBlockEvent {

    @SubscribeEvent
    public static void handleShieldEvent(LivingShieldBlockEvent event) {
        LivingEntity blockingEntity = event.getEntity();
        Entity attackingEntity = event.getDamageSource().getEntity();
        Entity directEntity = event.getDamageSource().getDirectEntity();
        ItemStack stack = blockingEntity.getUseItem();
        Levelable levelable = stack.getCapability(Capabilities.ITEM_SKILL, blockingEntity.level());

        if (levelable != null && event.getBlocked() && event.getBlockedDamage() > 0) {
            levelable.addExp(blockingEntity.level(), blockingEntity.getOnPos(), blockingEntity, (int) Math.ceil(event.getBlockedDamage()));

            if (attackingEntity instanceof LivingEntity targetEntity) {
                handleTargetEffects(targetEntity, stack, blockingEntity, blockingEntity.level());
            }

            if (directEntity instanceof Projectile projectile) {
                int target = stack.getOrDefault(DataComponents.ENTITY_TARGET, -1);
                if (target != -1) {
                    LevelTickEvent.startTracking(attackingEntity.level(), projectile.getId(), target, projectile.getDeltaMovement().length());
                }
            }
        }
    }

    private static void handleTargetEffects(LivingEntity target, ItemStack stack, LivingEntity blocker, Level level) {
        // Fire
        if (stack.getOrDefault(DataComponents.FLAMING_SHIELD, false)) {
            target.igniteForTicks(100);
        }

        if (stack.getOrDefault(DataComponents.SHIELD_KNOCKBACK, false)) {
            if (blocker instanceof ServerPlayer player) {
                player.connection.send(new ClientboundSoundPacket(Holder.direct(SoundEvents.WIND_CHARGE_BURST.value()), SoundSource.PLAYERS, blocker.getX(), blocker.getY(), blocker.getZ(), 2F, (level.random.nextFloat() - level.random.nextFloat()) * 1.4F + 2.0F, level.getRandom().nextLong()));
            }
            target.push(target.getPosition(0).subtract(blocker.getPosition(0)).add(0, 0.5, 0));
        }

        int thorns = stack.getOrDefault(DataComponents.SHIELD_THORNS, 0);

        if (thorns > 0 && Math.random() < thorns * 0.25) {
            target.hurt(level.damageSources().thorns(blocker), 3);
        }

        // Mob Effects
        List<MobEffectInstance> effects = stack.getOrDefault(DataComponents.EFFECTS, Collections.emptyList());
        effects.forEach(target::addEffect);
    }
}

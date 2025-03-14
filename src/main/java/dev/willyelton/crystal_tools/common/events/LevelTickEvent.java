package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// TODO: Timeout and also the movement is very buggy sometimes
@EventBusSubscriber(modid = CrystalTools.MODID)
public class LevelTickEvent {
    public static ConcurrentHashMap<ResourceKey<Level>, Set<TrackingProjectileData>> TRACKING_PROJECTILES = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void handleLevelTick(net.neoforged.neoforge.event.tick.LevelTickEvent.Pre event) {
        Level level = event.getLevel();

        if (level.getGameTime() % 5 == 0 && level instanceof ServerLevel) {
            Set<TrackingProjectileData> projectiles = TRACKING_PROJECTILES.get(level.dimension());
            if (projectiles == null) return;

            Iterator<TrackingProjectileData> iterator = projectiles.iterator();
            while (iterator.hasNext()) {
                TrackingProjectileData projectileData = iterator.next();

                Entity projectile = level.getEntity(projectileData.projectileId);
                Entity target = level.getEntity(projectileData.targetId);

                if (projectile != null && target != null && !projectile.isRemoved()
                        && !target.isRemoved() && projectile instanceof Projectile projectileEntity &&
                        !(projectileEntity instanceof AbstractArrow arrow && arrow.isInGround())) {
                    double speed = Math.max(projectileData.initialSpeed, 1);
                    projectileEntity.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
                    projectileEntity.setNoGravity(true);
                    projectileEntity.setDeltaMovement(target.trackingPosition().subtract(projectileEntity.position()).normalize().scale(speed));
                } else {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Causes the projectile to start tracking the target entity.
     * Should only be called on the SERVER.
     * @param level: The level the entities are on
     * @param projectileId: The entity id of the projectile
     * @param targetId: The entity id of then target
     * @param initialSpeed: The speed of the tracking projectile
     */
    public static void startTracking(Level level, int projectileId, int targetId, double initialSpeed) {
        Set<TrackingProjectileData> data = TRACKING_PROJECTILES.getOrDefault(level.dimension(), new HashSet<>());
        data.add(new TrackingProjectileData(projectileId, targetId, initialSpeed));
        TRACKING_PROJECTILES.put(level.dimension(), data);
    }

    public record TrackingProjectileData(int projectileId, int targetId, double initialSpeed) {}
}

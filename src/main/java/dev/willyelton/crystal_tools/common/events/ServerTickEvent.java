package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class ServerTickEvent {
    public static ConcurrentHashMap<ResourceKey<Level>, List<TrackingProjectileData>> TRACKING_PROJECTILES = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void handleServerTick(LevelTickEvent.Pre event) {
        Level level = event.getLevel();

        if (level.getGameTime() % 5 == 0 && level instanceof ServerLevel) {
            List<TrackingProjectileData> projectiles = TRACKING_PROJECTILES.get(level.dimension());
            if (projectiles == null) return;

            Iterator<TrackingProjectileData> iterator = projectiles.iterator();
            while (iterator.hasNext()) {
                TrackingProjectileData projectileData = iterator.next();

                Entity projectile = level.getEntity(projectileData.projectileId);
                Entity target = level.getEntity(projectileData.targetId);

                if (projectile != null && target != null && !projectile.isRemoved()
                        && !target.isRemoved() && projectile instanceof Projectile projectileEntity) {
                    projectileEntity.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
                    projectileEntity.setDeltaMovement(target.getEyePosition().subtract(projectileEntity.position()).normalize().scale(2));
                } else {
                    iterator.remove();
                }
            }
        }
    }

    public static void startTracking(Level level, int projectileId, int targetId) {
        List<TrackingProjectileData> data = TRACKING_PROJECTILES.getOrDefault(level.dimension(), new ArrayList<>());
        data.add(new TrackingProjectileData(projectileId, targetId));
        TRACKING_PROJECTILES.put(level.dimension(), data);
    }

    public record TrackingProjectileData(int projectileId, int targetId) {}
}

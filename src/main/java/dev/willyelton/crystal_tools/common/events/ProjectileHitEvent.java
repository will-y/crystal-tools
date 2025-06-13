package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class ProjectileHitEvent {
    @SubscribeEvent
    public static void handleProjectileHit(ProjectileImpactEvent event) {
        Entity owner = event.getProjectile().getOwner();

        if (owner instanceof Player player) {
            if (event.getRayTraceResult().getType() == HitResult.Type.ENTITY &&
                    event.getRayTraceResult() instanceof EntityHitResult hitResult) {
                if (hitResult.getEntity() instanceof LivingEntity target && ToolUtils.isValidEntity(target)) {
                    ItemStack heldItem = player.getMainHandItem();
                    Levelable levelable = heldItem.getCapability(Capabilities.ITEM_SKILL, player.level());
                    if (levelable != null) {
                        if (event.getProjectile() instanceof AbstractArrow arrow) {
                            float f = (float) arrow.getDeltaMovement().length();
                            // This is how they get damage, ignore crits for now
                            int damage = Mth.ceil(Mth.clamp((double) f * 2, 0.0D, 2.147483647E9D));

                            levelable.addExp(player.level(), player.getOnPos(), player, (float) (damage * CrystalToolsConfig.BOW_EXPERIENCE_BOOST.get()));
                        }
                    }
                }
            }
        }
    }
}

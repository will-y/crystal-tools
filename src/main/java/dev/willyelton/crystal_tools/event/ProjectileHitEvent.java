package dev.willyelton.crystal_tools.event;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.tool.LevelableItem;
import dev.willyelton.crystal_tools.tool.ModTools;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= CrystalTools.MODID)
public class ProjectileHitEvent {
    @SubscribeEvent
    public static void handleProjectileHit(ProjectileImpactEvent event) {
        Entity owner = event.getProjectile().getOwner();

        if (owner instanceof Player player) {
            if (event.getRayTraceResult().getType() == HitResult.Type.ENTITY) {
                ItemStack heldItem = player.getMainHandItem();

                if (heldItem.is(ModTools.CRYSTAL_BOW.get())) {
                    if (event.getProjectile() instanceof AbstractArrow arrow) {
                        float f = (float) arrow.getDeltaMovement().length();
                        // This is how they get damage, ignore crits for now
                        int damage = Mth.ceil(Mth.clamp((double) f * arrow.getBaseDamage(), 0.0D, 2.147483647E9D));

                        LevelableItem item = (LevelableItem) ModTools.CRYSTAL_BOW.get();
                        item.addExp(heldItem, player.getLevel(), player.getOnPos(), damage);
                    }
                }
            }
        }
    }
}

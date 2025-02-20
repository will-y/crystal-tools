package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.tool.CrystalShield;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class ShieldBlockEvent {

    @SubscribeEvent
    public static void handleShieldEvent(LivingShieldBlockEvent event) {
        LivingEntity blockingEntity = event.getEntity();
        Entity attackingEntity = event.getDamageSource().getEntity();
        Entity directEntity = event.getDamageSource().getDirectEntity();
        ItemStack stack = blockingEntity.getUseItem();

        if (stack.getItem() instanceof CrystalShield crystalShield) {
            if (event.getBlocked() && event.getBlockedDamage() > 0) {
                crystalShield.addExp(blockingEntity.getUseItem(), blockingEntity.level(), blockingEntity.getOnPos(), blockingEntity, (int) Math.ceil(event.getBlockedDamage()));

                if (attackingEntity instanceof LivingEntity targetEntity) {
//                    handleTargetEffects(targetEntity, crystalShield, blockingEntity);
                }

                if (directEntity instanceof Projectile projectile) {
                    int target = stack.getOrDefault(DataComponents.ENTITY_TARGET, -1);
                    if (target != -1) {
                        ServerTickEvent.startTracking(attackingEntity.level(), projectile.getId(), target);
                    }
                }
            }
        }
    }

    private static void handleTargetEffects(LivingEntity target, CrystalShield shieldItem, LivingEntity player) {
        // Fire
        target.igniteForTicks(100);

        // Push back (add a little vertical here)
        target.push(target.getPosition(0).subtract(player.getPosition(0)).add(0, 0.5, 0));

        // Mob Effects

        // Thorns
        // Vanilla is level * .15 chance to do 1-4 damage
    }
}

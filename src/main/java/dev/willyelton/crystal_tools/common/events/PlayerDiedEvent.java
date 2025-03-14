package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

// TODO: This can be replaced by the DEATH_PROTECTION data component
@EventBusSubscriber(modid = CrystalTools.MODID)
public class PlayerDiedEvent {
    @SubscribeEvent
    public static void handlePlayerDying(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (trySave(player.getMainHandItem())) {
                event.setCanceled(true);
                triggerTotem(player);
            } else if (trySave(player.getOffhandItem())) {
                event.setCanceled(true);
                triggerTotem(player);
            }
        }
    }

    private static boolean trySave(ItemStack stack) {
        if (stack.is(Registration.CRYSTAL_SHIELD)) {
            int charges = stack.getOrDefault(DataComponents.FILLED_TOTEM_SLOTS, 0);

            if (charges > 0) {
                DataComponents.addToComponent(stack, DataComponents.FILLED_TOTEM_SLOTS, -1);
                return true;
            }
        }

        return false;
    }

    private static void triggerTotem(Player player) {
        player.setHealth(1.0F);
//        player.removeEffectsCuredBy(EffectCures.PROTECTED_BY_TOTEM);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
        player.level().broadcastEntityEvent(player, (byte) 35);
    }
}

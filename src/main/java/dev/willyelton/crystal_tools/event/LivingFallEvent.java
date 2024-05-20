package dev.willyelton.crystal_tools.event;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID)
public class LivingFallEvent {
    @SubscribeEvent
    public static void handleLivingFallEvent(net.minecraftforge.event.entity.living.LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            for (ItemStack stack : player.getArmorSlots()) {
                if (stack.is(Registration.CRYSTAL_BOOTS.get()) && NBTUtils.getBoolean(stack, "no_fall_damage")) {
                    event.setCanceled(true);
                }
            }
        }
    }
}

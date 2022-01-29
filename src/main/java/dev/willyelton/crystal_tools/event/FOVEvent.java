package dev.willyelton.crystal_tools.event;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.item.tool.ModTools;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.FOVModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class FOVEvent {
    @SubscribeEvent
    public static void handleFOVEvent(FOVModifierEvent event) {
        Player player = event.getEntity();
        ItemStack itemStack = player.getUseItem();
        float f = event.getFov();

        if (player.isUsingItem()) {
            if (itemStack.is(ModTools.CRYSTAL_BOW.get())) {
                int i = player.getTicksUsingItem();
                float f1 = (float)i / 20.0F;
                if (f1 > 1.0F) {
                    f1 = 1.0F;
                } else {
                    f1 *= f1;
                }

                f *= 1.0F - f1 * 0.15F;

                event.setNewfov(f);
            }
        }
    }
}

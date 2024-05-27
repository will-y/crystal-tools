package dev.willyelton.crystal_tools.event.client;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.levelable.tool.BowLevelableItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class FOVEvent {
    @SubscribeEvent
    public static void handleFOVEvent(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getUseItem();
        float f = event.getFovModifier();

        if (player.isUsingItem()) {
            if (itemStack.getItem() instanceof BowLevelableItem item) {
                int i = player.getTicksUsingItem();
                float f1 = (float) i / item.getChargeTime(itemStack);
                if (f1 > 1.0F) {
                    f1 = 1.0F;
                } else {
                    f1 *= f1;
                }

                f *= 1.0F - f1 * 0.15F;

                event.setNewFovModifier(f);
            }
        }
    }
}

package dev.willyelton.crystal_tools.client.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.tool.BowLevelableItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class FOVEvent {
    @SubscribeEvent
    public static void handleFOVEvent(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getUseItem();
        float f = event.getFovModifier();

        if (player.isUsingItem()) {
            if (itemStack.getItem() instanceof BowLevelableItem item) {
                int i = player.getTicksUsingItem();
                float f1 = (float) i / BowLevelableItem.getChargeTime(itemStack);
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

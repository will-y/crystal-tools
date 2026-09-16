package dev.willyelton.crystal.tools.common.events;

import dev.willyelton.crystal.core.common.levelable.ItemClickHandler;
import dev.willyelton.crystal.tools.CrystalTools;
import dev.willyelton.crystal.tools.common.network.data.ItemClickEmptyPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class PlayerClickEvents {
    @SubscribeEvent
    public static void handleLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START) {
            return;
        }

        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof ItemClickHandler item) {
            if (item.leftClickBlock(stack, event.getLevel(), event.getEntity(), event.getPos(), event.getFace())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void handleLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof ItemClickHandler) {
            ClientPacketDistributor.sendToServer(new ItemClickEmptyPayload(event.getHand(), event.getEntity().isShiftKeyDown()));
        }
    }
}

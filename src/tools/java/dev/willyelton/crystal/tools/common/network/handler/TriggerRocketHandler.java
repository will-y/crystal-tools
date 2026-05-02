package dev.willyelton.crystal.tools.common.network.handler;

import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.tools.common.levelable.tool.CrystalRocket;
import dev.willyelton.crystal.tools.common.network.data.TriggerRocketPayload;
import dev.willyelton.crystal.core.utils.InventoryUtils;
import dev.willyelton.crystal.core.utils.ToolUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class TriggerRocketHandler {
    public static TriggerRocketHandler INSTANCE = new TriggerRocketHandler();

    public void handle(final TriggerRocketPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            ItemStack rocketStack = InventoryUtils.findItem(player.getInventory(), stack -> stack.is(ModRegistration.CRYSTAL_ROCKET) && !ToolUtils.isBroken(stack));

            if (rocketStack.getItem() instanceof CrystalRocket crystalRocket) {
                crystalRocket.use(rocketStack, player.level(), player, InteractionHand.OFF_HAND);
            }
        });
    }
}

package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.levelable.tool.CrystalRocket;
import dev.willyelton.crystal_tools.common.network.data.TriggerRocketPayload;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class TriggerRocketHandler {
    public static TriggerRocketHandler INSTANCE = new TriggerRocketHandler();

    public void handle(final TriggerRocketPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            ItemStack rocketStack = InventoryUtils.findItem(player.getInventory(), stack -> stack.is(Registration.CRYSTAL_ROCKET) && !ToolUtils.isBroken(stack));

            if (rocketStack.getItem() instanceof CrystalRocket crystalRocket) {
                crystalRocket.use(rocketStack, player.level(), player, InteractionHand.OFF_HAND);
            }
        });
    }
}

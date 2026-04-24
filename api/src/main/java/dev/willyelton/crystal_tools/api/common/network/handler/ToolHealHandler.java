package dev.willyelton.crystal_tools.api.common.network.handler;

import dev.willyelton.crystal_tools.api.common.datacomponent.DataComponents;
import dev.willyelton.crystal_tools.api.common.network.payload.ToolHealPayload;
import dev.willyelton.crystal_tools.api.utils.ItemStackUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ToolHealHandler {
    public static ToolHealHandler INSTANCE = new ToolHealHandler();

    public void handle(final ToolHealPayload payload, final IPayloadContext context) {
        Player player = context.player();
        ItemStack heldTool = ItemStackUtils.getHeldLevelableTool(player);

        if (!heldTool.isEmpty()) {
            heldTool.setDamageValue(0);
            DataComponents.addToComponent(heldTool, DataComponents.SKILL_POINTS, -1);
        }
    }
}

package dev.willyelton.crystal_tools.api.common.network.handler;

import dev.willyelton.crystal_tools.api.common.network.payload.ResetSkillsPayload;
import dev.willyelton.crystal_tools.api.utils.ItemStackUtils;
import dev.willyelton.crystal_tools.api.utils.ToolUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ResetSkillsHandler {
    public static ResetSkillsHandler INSTANCE = new ResetSkillsHandler();

    public void handle(final ResetSkillsPayload payload, final IPayloadContext context) {
        Player player = context.player();

        ItemStack heldTool = ItemStackUtils.getHeldLevelableTool(player);

        if (!heldTool.isEmpty()) {
            ToolUtils.resetPoints(heldTool, player);
        }
    }
}

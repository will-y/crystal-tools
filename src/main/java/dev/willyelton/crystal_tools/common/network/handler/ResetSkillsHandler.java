package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.network.data.ResetSkillsPayload;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ResetSkillsHandler {
    public static ResetSkillsHandler INSTANCE = new ResetSkillsHandler();

    public void handle(final ResetSkillsPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            ItemStack heldTool = ItemStackUtils.getHeldLevelableTool(player);

            if (!heldTool.isEmpty()) {
                ToolUtils.resetPoints(heldTool);
            }
        });
    }
}

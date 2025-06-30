package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.network.data.BlockStripPayload;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class BlockStripHandler {
    public static BlockStripHandler INSTANCE = new BlockStripHandler();

    public void handle(final BlockStripPayload payload, final IPayloadContext context) {
        Player player = context.player();

        ItemStack tool = player.getItemInHand(payload.hand());
        Levelable levelable = tool.getCapability(Capabilities.ITEM_SKILL, player.level().registryAccess());
        if (levelable != null) {
            ToolUseUtils.stripBlock(player.level(), tool, player, payload.blockPos(),
                    InteractionHand.MAIN_HAND, payload.strippedState(), levelable);
        }
    }
}

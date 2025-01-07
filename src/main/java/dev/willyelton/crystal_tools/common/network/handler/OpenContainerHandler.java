package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.levelable.block.CrystalToolsMenuProvider;
import dev.willyelton.crystal_tools.common.network.data.OpenContainerPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OpenContainerHandler {
    public static OpenContainerHandler INSTANCE = new OpenContainerHandler();

    public void handle(final OpenContainerPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            BlockPos pos = BlockPos.of(payload.packedBlockPos());
            Level level = player.level();
            Block block = level.getBlockState(pos).getBlock();

            if (block instanceof CrystalToolsMenuProvider menuProvider) {
                menuProvider.openContainer(level, pos, player);
            }
        });
    }
}

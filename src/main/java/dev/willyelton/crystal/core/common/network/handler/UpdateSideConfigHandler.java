package dev.willyelton.crystal.core.common.network.handler;

import dev.willyelton.crystal.core.common.block.entity.SideConfigBlockEntity;
import dev.willyelton.crystal.core.common.network.payload.UpdateSideConfigPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class UpdateSideConfigHandler {
    public static UpdateSideConfigHandler INSTANCE = new UpdateSideConfigHandler();

    public void handle(final UpdateSideConfigPayload payload, final IPayloadContext context) {
        BlockEntity blockEntity = context.player().level().getBlockEntity(payload.pos());

        if (blockEntity instanceof SideConfigBlockEntity sideConfigBlockEntity) {
            sideConfigBlockEntity.setSideConfig(payload.direction(), payload.option());
            blockEntity.invalidateCapabilities();

        }
    }
}

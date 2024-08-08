package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.inventory.container.LevelableContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal_tools.common.network.data.BlockAttributePayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class BlockAttributeHandler {
    public static final BlockAttributeHandler INSTANCE = new BlockAttributeHandler();

    public void handle(final BlockAttributePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            AbstractContainerMenu container = player.containerMenu;

            if (container instanceof LevelableContainerMenu levelableContainerMenu) {
                LevelableBlockEntity blockEntity = levelableContainerMenu.getBlockEntity();

                blockEntity.addToData(payload.key(), payload.value());
                if (payload.id() != -1) {
                    blockEntity.addToPoints(payload.id(), 1);
                }
            }
        });
    }
}

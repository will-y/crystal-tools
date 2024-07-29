package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.inventory.container.ScrollableMenu;
import dev.willyelton.crystal_tools.common.network.data.ContainerRowsPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ContainerRowsHandler {
    public static ContainerRowsHandler INSTANCE = new ContainerRowsHandler();

    public void handle(final ContainerRowsPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.containerMenu instanceof ScrollableMenu menu) {
                menu.setMaxRows(payload.rows());
            }
        });
    }
}

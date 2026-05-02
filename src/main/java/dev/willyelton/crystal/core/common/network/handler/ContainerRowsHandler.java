package dev.willyelton.crystal.core.common.network.handler;

import dev.willyelton.crystal.core.common.inventory.container.ScrollableMenu;
import dev.willyelton.crystal.core.common.network.payload.ContainerRowsPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ContainerRowsHandler {
    public static ContainerRowsHandler INSTANCE = new ContainerRowsHandler();

    public void handle(final ContainerRowsPayload payload, final IPayloadContext context) {
        Player player = context.player();
        if (player.containerMenu instanceof ScrollableMenu menu) {
            menu.setMaxRows(payload.rows());
        }
    }
}

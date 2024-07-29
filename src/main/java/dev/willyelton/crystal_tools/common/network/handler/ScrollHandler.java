package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.inventory.container.ScrollableMenu;
import dev.willyelton.crystal_tools.common.network.data.ScrollPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ScrollHandler {
    public static ScrollHandler INSTANCE = new ScrollHandler();

    public void handle(final ScrollPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            if (player.containerMenu instanceof ScrollableMenu menu) {
                menu.scrollTo(payload.row());
            }
        });
    }
}

package dev.willyelton.crystal.core.common.network.handler;

import dev.willyelton.crystal.core.common.inventory.container.ScrollableMenu;
import dev.willyelton.crystal.core.common.network.payload.ScrollPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ScrollHandler {
    public static ScrollHandler INSTANCE = new ScrollHandler();

    public void handle(final ScrollPayload payload, final IPayloadContext context) {
        Player player = context.player();

        if (player.containerMenu instanceof ScrollableMenu menu) {
            menu.scrollTo(payload.row());
        }
    }
}

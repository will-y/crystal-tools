package dev.willyelton.crystal.core.common.network.handler;

import dev.willyelton.crystal.core.common.inventory.container.SubScreenType;
import dev.willyelton.crystal.core.common.inventory.container.subscreen.SubScreenContainerMenu;
import dev.willyelton.crystal.core.common.network.payload.OpenSubScreenPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OpenSubScreenHandler {
    public static final OpenSubScreenHandler INSTANCE = new OpenSubScreenHandler();

    public void handle(final OpenSubScreenPayload payload, final IPayloadContext context) {
        Player player = context.player();

        if (player.containerMenu instanceof SubScreenContainerMenu menu) {
            switch (payload.subScreenType()) {
                case SubScreenType.COMPRESS -> menu.openSubScreen(SubScreenType.COMPRESS);
                case SubScreenType.FILTER -> menu.openSubScreen(SubScreenType.FILTER);
                case SubScreenType.QUARRY_SETTINGS -> menu.openSubScreen(SubScreenType.QUARRY_SETTINGS);
                case SubScreenType.SIDE_SETTINGS -> menu.openSubScreen(SubScreenType.SIDE_SETTINGS);
                case SubScreenType.NONE -> menu.closeSubScreen();
            }
        }
    }
}

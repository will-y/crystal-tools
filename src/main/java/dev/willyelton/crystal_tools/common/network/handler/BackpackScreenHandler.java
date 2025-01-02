package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.FilterContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenType;
import dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class BackpackScreenHandler {
    public static BackpackScreenHandler INSTANCE = new BackpackScreenHandler();

    public void handle(final BackpackScreenPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            // Check for filter things
            if (player.containerMenu instanceof FilterContainerMenu menu) {
                switch (payload.pickupType()) {
                    case PICKUP_WHITELIST -> menu.setWhitelist(true);
                    case PICKUP_BLACKLIST -> menu.setWhitelist(false);
                    case CLEAR_FILTERS -> menu.clearFilters();
                }
            }

            // Then check for subscreen things
            if (player.containerMenu instanceof SubScreenContainerMenu menu) {
                switch (payload.pickupType()) {
                    case OPEN_COMPRESSION -> menu.openSubScreen(SubScreenType.COMPRESS);
                    case OPEN_FILTER -> menu.openSubScreen(SubScreenType.FILTER);
                    case CLOSE_SUB_SCREEN -> menu.closeSubScreen();
                }
            }

            // Finally do backpack specific things
            if (player.containerMenu instanceof CrystalBackpackContainerMenu menu) {
                switch (payload.pickupType()) {
                    case SORT -> menu.sort();
                    case COMPRESS -> menu.compress();
                    case MATCH_CONTENTS -> menu.matchContentsFilter(payload.hasShiftDown());
                    case REOPEN_BACKPACK -> menu.reopenBackpack();
                }
            }
        });
    }
}

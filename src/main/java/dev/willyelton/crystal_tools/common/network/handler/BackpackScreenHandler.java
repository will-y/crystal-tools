package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class BackpackScreenHandler {
    public static BackpackScreenHandler INSTANCE = new BackpackScreenHandler();

    public void handle(final BackpackScreenPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            if (player.containerMenu instanceof CrystalBackpackContainerMenu menu) {
                switch (payload.pickupType()) {
                    case PICKUP_WHITELIST -> menu.setWhitelist(true);
                    case PICKUP_BLACKLIST -> menu.setWhitelist(false);
                    case SORT -> menu.sort();
                    case COMPRESS -> menu.compress();
                    case OPEN_COMPRESSION -> menu.openCompressionScreen();
                    case OPEN_FILTER -> menu.openFilterScreen();
                    case MATCH_CONTENTS -> menu.matchContentsFilter(payload.hasShiftDown());
                    case CLEAR_FILTERS -> menu.clearFilters();
                    case CLOSE_SUB_SCREEN -> menu.closeSubScreen();
                    case REOPEN_BACKPACK -> menu.reopenBackpack();
                }
            }
        });
    }
}

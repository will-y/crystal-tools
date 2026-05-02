package dev.willyelton.crystal.tools.common.network.handler;

import dev.willyelton.crystal.core.common.inventory.container.subscreen.FilterContainerMenu;
import dev.willyelton.crystal.core.common.network.model.BackpackAction;
import dev.willyelton.crystal.tools.common.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal.tools.common.network.data.BackpackScreenPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class BackpackScreenHandler {
    public static final BackpackScreenHandler INSTANCE = new BackpackScreenHandler();

    public void handle(final BackpackScreenPayload payload, final IPayloadContext context) {
        Player player = context.player();

        // Check for filter things
        if (player.containerMenu instanceof FilterContainerMenu menu) {
            switch (payload.pickupType()) {
                case BackpackAction.PICKUP_WHITELIST -> menu.setWhitelist(true);
                case BackpackAction.PICKUP_BLACKLIST -> menu.setWhitelist(false);
                case BackpackAction.CLEAR_FILTERS -> menu.clearFilters();
            }
        }

        // Finally do backpack specific things
        if (player.containerMenu instanceof CrystalBackpackContainerMenu menu) {
            switch (payload.pickupType()) {
                case BackpackAction.SORT -> menu.sort();
                case BackpackAction.COMPRESS -> menu.compress();
                case BackpackAction.MATCH_CONTENTS -> menu.matchContentsFilter(payload.hasShiftDown());
                case BackpackAction.REOPEN_BACKPACK -> menu.reopenBackpack();
            }
        }
    }
}

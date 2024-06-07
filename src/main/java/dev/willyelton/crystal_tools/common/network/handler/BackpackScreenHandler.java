package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.inventory.container.CrystalBackpackContainerMenu;
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
                }
            }
        });
    }
}

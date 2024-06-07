package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.network.data.RemoveXpPayload;
import dev.willyelton.crystal_tools.utils.XpUtils;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RemoveXpHandler {
    public static RemoveXpHandler INSTANCE = new RemoveXpHandler();

    public void handle(final RemoveXpPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            if (XpUtils.getPlayerTotalXp(player) >= payload.xp()) {
                player.giveExperiencePoints(-payload.xp());
            }
        });
    }
}

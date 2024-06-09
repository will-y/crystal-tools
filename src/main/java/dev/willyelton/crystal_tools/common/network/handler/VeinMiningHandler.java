package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.VeinMiners;
import dev.willyelton.crystal_tools.common.network.data.VeinMiningPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class VeinMiningHandler {
    public static VeinMiningHandler INSTANCE = new VeinMiningHandler();

    public void handle(final VeinMiningPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            if (player instanceof ServerPlayer serverPlayer) {
                if (payload.start()) {
                    VeinMiners.startVeinMining(serverPlayer);
                } else {
                    VeinMiners.stopVeinMining(serverPlayer);
                }
            }
        });
    }
}
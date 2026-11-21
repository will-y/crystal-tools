package dev.willyelton.crystal.tools;

import dev.willyelton.crystal.tools.client.events.RegisterKeyBindingsEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// TODO: Add something to remove players that have been vein mining for a while
public class VeinMiners {
    private static final Set<UUID> VEIN_MINING_PLAYERS = new HashSet<>();

    private VeinMiners() {}

    public static void startVeinMining(ServerPlayer player) {
        VEIN_MINING_PLAYERS.add(player.getUUID());
    }

    public static void stopVeinMining(ServerPlayer player) {
        VEIN_MINING_PLAYERS.remove(player.getUUID());
    }

    public static boolean isVeinMining(Player player) {
        if (player.level().isClientSide()) {
            return RegisterKeyBindingsEvent.VEIN_MINE.isDown();
        } else {
            return VEIN_MINING_PLAYERS.contains(player.getUUID());
        }
    }
}

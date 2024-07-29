package dev.willyelton.crystal_tools;

import net.minecraft.server.level.ServerPlayer;

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

    public static boolean isVeinMining(ServerPlayer player) {
        return VEIN_MINING_PLAYERS.contains(player.getUUID());
    }
}

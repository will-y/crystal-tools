package dev.willyelton.crystal_tools.utils;

import net.minecraft.world.entity.player.Player;

public class XpUtils {
    /**
     * Gets the total experience to get to a duration.
     * Formula: <a href="https://minecraft.fandom.com/wiki/Experience#Leveling_up">Minecraft Wiki</a>
     * @param level Experience Level
     * @return Total number of experience points to get to that duration
     */
    public static int getXPForLevel(int level) {
        if (level <= 15) {
            return level * level + 6 * level;
        } else if (level <= 30) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            return (int) (4.5 * level * level - 162.5 * level + 2220);
        }
    }

    public static int getPlayerTotalXp(Player player) {
        return getXPForLevel(player.experienceLevel) + (int)Math.floor(player.experienceProgress * player.getXpNeededForNextLevel());
    }
}

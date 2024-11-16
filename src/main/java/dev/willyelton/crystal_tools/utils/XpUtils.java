package dev.willyelton.crystal_tools.utils;

import net.minecraft.world.entity.player.Player;

public class XpUtils {
    /**
     * Gets the total experience to get to a level.
     * Formula: <a href="https://minecraft.fandom.com/wiki/Experience#Leveling_up">Minecraft Wiki</a>
     * @param level Experience Level
     * @return Total number of experience points to get to that level
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

    public static int getLevelForXp(int xp) {
        if (xp <= 352) {
            return (int) Math.ceil(Math.sqrt(xp + 9) - 3);
        } else if (xp <= 1507) {
            return (int) Math.ceil(81D / 10D + Math.sqrt((2D / 5D) * (xp - (7839D / 40D))));
        } else {
            return (int) Math.ceil(325D / 18D + Math.sqrt((2D / 9D) * (xp - (54215D / 72D))));
        }
    }

    public static int getPlayerTotalXp(Player player) {
        return getXPForLevel(player.experienceLevel) + (int) Math.floor(player.experienceProgress * player.getXpNeededForNextLevel());
    }
}

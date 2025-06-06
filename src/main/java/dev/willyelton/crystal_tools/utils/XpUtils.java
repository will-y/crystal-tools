package dev.willyelton.crystal_tools.utils;

import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import net.minecraft.world.entity.player.Player;

public class XpUtils {
    /**
     * Gets the total experience to get to a level.
     * Formula: <a href="https://minecraft.fandom.com/wiki/Experience#Leveling_up">Minecraft Wiki</a>
     * @param level Experience Level
     * @return Total number of experience points to get to that level
     */
    public static long getXPForLevel(long level) {
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

    public static long getPlayerTotalXp(Player player) {
        return getXPForLevel(player.experienceLevel) + (long) Math.floor(player.experienceProgress * player.getXpNeededForNextLevel());
    }

    public static int getXpCost(int pointsToGain, int totalPoints) {
        int xpLevelCost = CrystalToolsConfig.EXPERIENCE_PER_SKILL_LEVEL.get();
        int levelScaling = CrystalToolsConfig.EXPERIENCE_LEVELING_SCALING.get();

        if (levelScaling > 0) {
            long totalCost = 0;

            int pointsAtLowerLevelLeft = Math.min(levelScaling - totalPoints % levelScaling, pointsToGain);

            // TODO: Look at this 500 value, could be bad if the xp level cost configs are changed
            int pointCost1 = Math.min(totalPoints / levelScaling, 500);
            totalCost += pointsAtLowerLevelLeft * XpUtils.getXPForLevel(xpLevelCost + pointCost1);

            int pointsLeft = pointsToGain - pointsAtLowerLevelLeft;
            int i = pointsAtLowerLevelLeft == 0 ? 0 : 1;
            while (pointsLeft > 0) {
                int pointsToSpend = Math.min(levelScaling, pointsLeft);
                int pointCost = Math.min(totalPoints / levelScaling, 500);
                totalCost += pointsToSpend * XpUtils.getXPForLevel(xpLevelCost + pointCost + i);
                i++;
                pointsLeft -= pointsToSpend;
            }

            // Overflow
            if (totalCost >= Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            } else {
                return (int) totalCost;
            }
        } else {
            int totalCost = pointsToGain * xpLevelCost;

            if (totalCost < 0) {
                return Integer.MAX_VALUE;
            } else {
                return totalCost;
            }
        }
    }
}

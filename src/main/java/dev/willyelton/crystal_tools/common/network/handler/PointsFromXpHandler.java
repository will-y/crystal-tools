package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.inventory.container.LevelableContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.common.network.data.PointsFromXpPayload;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import dev.willyelton.crystal_tools.utils.XpUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

public class PointsFromXpHandler {
    public static PointsFromXpHandler INSTANCE = new PointsFromXpHandler();

    public void handle(final PointsFromXpPayload payload, final IPayloadContext context) {
        Player player = context.player();

        int totalPoints = getTotalPoints(payload.item(), player);
        int xpCost = getXpCost(payload.points(), totalPoints);

        if (XpUtils.getPlayerTotalXp(player) >= xpCost) {
            player.giveExperiencePoints(-xpCost);
            addPoints(payload.item(), player, payload.points());
        }
    }

    private int getTotalPoints(boolean item, Player player) {
        if (item) {
            ItemStack heldTool = ItemStackUtils.getHeldLevelableTool(player);

            int spentPoints = heldTool.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints()).getTotalPoints();
            int unspentPoints = heldTool.getOrDefault(DataComponents.SKILL_POINTS, 0);

            return spentPoints + unspentPoints;
        } else {
            LevelableBlockEntity blockEntity = getBlockEntity(player);
            if (blockEntity != null) {
                return blockEntity.getSkillPoints() + blockEntity.getPoints().getTotalPoints();
            }

            return -1;
        }
    }

    private int getXpCost(int pointsToGain, int totalPoints) {
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

    private void addPoints(boolean item, Player player, int points) {
        if (item) {
            ItemStack heldTool = ItemStackUtils.getHeldLevelableTool(player);

            if (!heldTool.isEmpty()) {
                DataComponents.addToComponent(heldTool, DataComponents.SKILL_POINTS, points);
            }
        } else {
            LevelableBlockEntity blockEntity = getBlockEntity(player);
            if (blockEntity != null) {
                blockEntity.addSkillPoints(points);
            }
        }
    }

    private @Nullable LevelableBlockEntity getBlockEntity(Player player) {
        AbstractContainerMenu container = player.containerMenu;

        if (container instanceof LevelableContainerMenu levelableContainerMenu) {
            return levelableContainerMenu.getBlockEntity();
        }

        return null;
    }

}

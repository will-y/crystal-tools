package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.components.DataComponents;
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
        int xpCost = XpUtils.getXpCost(payload.points(), totalPoints);

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

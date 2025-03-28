package dev.willyelton.crystal_tools.common.levelable.block;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.components.QuarryData;
import dev.willyelton.crystal_tools.common.components.QuarryUpgrades;
import dev.willyelton.crystal_tools.utils.StringUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

public class CrystalQuarryBlockItem extends LevelableBlockItem {
    public CrystalQuarryBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        ItemStack stack = context.getItemInHand();
        List<BlockPos> bounds = stack.get(DataComponents.QUARRY_BOUNDS);

        if (bounds != null && bounds.size() == 4) {
            if (isOutside(bounds, context.getClickedPos()) && isOneAway(bounds, context.getClickedPos())) {
                return true;
            } else {
                Player player = context.getPlayer();
                if (player != null && !player.level().isClientSide()) {
                    player.displayClientMessage(Component.literal("\u00A7c\u00A7lQuarry must be placed next to the stabilizer boundary"), true);
                }

                return false;
            }
        } else {
            Player player = context.getPlayer();
            if (player != null && !player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("\u00A7c\u00A7lQuarry must be linked to stabilizers to place"), true);
            }
            return false;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
        components.accept(Component.literal("Quarry is currently still a WIP. Please report any issues or comment suggestions!").withStyle(ChatFormatting.RED));

        super.appendHoverText(stack, context, display, components, tooltipFlag);

        if (tooltipFlag.hasShiftDown()) {
            QuarryUpgrades quarryUpgrades = stack.get(DataComponents.QUARRY_UPGRADES);
            if (quarryUpgrades != null) {
                if (quarryUpgrades.speedUpgrade() > 0) {
                    components.accept(Component.literal(String.format("\u00A76     %s: %s", "Speed", StringUtils.formatFloat(quarryUpgrades.speedUpgrade()))));
                }
                if (quarryUpgrades.redstoneControl()) {
                    components.accept(Component.literal("\u00A76     Redstone Control"));
                }
                if (quarryUpgrades.fortuneLevel() > 0) {
                    components.accept(Component.literal(String.format("\u00A76     %s %s", "Fortune", StringUtils.formatFloat(quarryUpgrades.speedUpgrade()))));
                }
                if (quarryUpgrades.silkTouch()) {
                    components.accept(Component.literal("\u00A76     Silk Touch"));
                }
                if (quarryUpgrades.extraEnergyCost() > 0) {
                    components.accept(Component.literal(String.format("\u00A7c     +%s Extra Energy", StringUtils.formatFloat(quarryUpgrades.extraEnergyCost()))));
                }
            }
        }

        List<BlockPos> stabilizers = stack.get(DataComponents.QUARRY_BOUNDS);
        if (stabilizers != null && !stabilizers.isEmpty()) {
            components.accept(Component.literal("\u00A7bLinked Stabilizer Positions"));
            for (BlockPos stabilizer : stabilizers) {
                components.accept(Component.literal(String.format(" \u00A7b  - [%s, %s, %s]", stabilizer.getX(), stabilizer.getY(), stabilizer.getZ())));
            }
        } else {
            components.accept(Component.literal("No Linked Stabilizers. Right click a square of stabilizers to link!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        }

        QuarryData quarryData = stack.get(DataComponents.QUARRY_DATA);
        if (quarryData != null) {
            components.accept(Component.literal(String.format("%d FE Stored", quarryData.currentEnergy())));
        }
    }

    private boolean isOutside(List<BlockPos> corners, BlockPos placePosition) {
        List<Integer> xValues = corners.stream().map(BlockPos::getX).distinct().toList();
        List<Integer> zValues = corners.stream().map(BlockPos::getZ).distinct().toList();

        if (xValues.size() == 2 && zValues.size() == 2) {
            return (xValues.get(0) > placePosition.getX() && xValues.get(1) > placePosition.getX()) || (xValues.get(0) < placePosition.getX() && xValues.get(1) < placePosition.getX()) ||
                    (zValues.get(0) > placePosition.getZ() && zValues.get(1) > placePosition.getZ()) || (zValues.get(0) < placePosition.getZ() && zValues.get(1) < placePosition.getZ());
        } else {
            return false;
        }

    }

    private boolean isOneAway(List<BlockPos> corners, BlockPos placePosition) {
        for (int i = 0; i < 4; i++) {
            if (isOnLine(corners, placePosition.relative(Direction.from2DDataValue(i)))) {
                return true;
            }
        }

        return false;
    }

    private boolean isOnLine(List<BlockPos> corners, BlockPos placePosition) {
        List<Integer> xValues = corners.stream().map(BlockPos::getX).distinct().toList();
        List<Integer> zValues = corners.stream().map(BlockPos::getZ).distinct().toList();
        return (zValues.contains(placePosition.getZ()) && isInBetween(xValues, placePosition.getX())) ||
                (xValues.contains(placePosition.getX()) && isInBetween(zValues, placePosition.getZ()));
    }

    private boolean isInBetween(List<Integer> ints, int value) {
        return (ints.get(0) >= value && ints.get(1) <= value) || (ints.get(0) <= value && ints.get(1) >= value);
    }
}

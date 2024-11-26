package dev.willyelton.crystal_tools.common.levelable.block;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.renderer.QuarryLaserRenderer;
import dev.willyelton.crystal_tools.utils.Colors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static dev.willyelton.crystal_tools.client.renderer.QuarryLaserRenderer.LASER_SPEED_MODIFIER;

// TODO: Probably want to do a cool model and not extend torch block
public class QuarryStabilizer extends TorchBlock {
    private static final int RED = Colors.fromRGB(255, 0, 0);
    private static final int GREEN = Colors.fromRGB(0, 255, 0);

    // TODO: Server config
    public static int QUARRY_MAX_SIZE = 16;

    private static final List<Direction> DIRECTIONS = List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    public QuarryStabilizer() {
        super(ParticleTypes.GLOW, BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel((state) -> 14).sound(SoundType.STONE));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        List<BlockPos> stabilizerPositions = findStabilizerSquare(pos, level);
        CrystalTools.LOGGER.info(stabilizerPositions);

        // TODO: Going to need to order these in some way. Start is position clicked, not sure how that isn't already the case...
        // Only happens when there are already renderers in progress, should be overwritten with the correct value...
        if (level.isClientSide) {
            int color = stabilizerPositions.size() == 4 ? GREEN : RED;
            if (stabilizerPositions.size() > 1) {
                QuarryLaserRenderer.startTemporaryLaser(level.getGameTime(), level.getGameTime() + 200, stabilizerPositions.get(0), stabilizerPositions.get(1), color);
            }

            if (stabilizerPositions.size() > 2) {
                QuarryLaserRenderer.startTemporaryLaser(level.getGameTime() + timeBetweenBlocks(stabilizerPositions.get(0), stabilizerPositions.get(1)), level.getGameTime() + 200, stabilizerPositions.get(1), stabilizerPositions.get(2), color);
            }

            if (stabilizerPositions.size() > 3) {
                QuarryLaserRenderer.startTemporaryLaser(level.getGameTime() + timeBetweenBlocks(stabilizerPositions.get(0), stabilizerPositions.get(1), stabilizerPositions.get(2)), level.getGameTime() + 200, stabilizerPositions.get(2), stabilizerPositions.get(3), color);
                QuarryLaserRenderer.startTemporaryLaser(level.getGameTime() + timeBetweenBlocks(stabilizerPositions.get(0), stabilizerPositions.get(1), stabilizerPositions.get(2), stabilizerPositions.get(3)), level.getGameTime() + 200, stabilizerPositions.get(3), stabilizerPositions.get(0), color);
            }
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    private int timeBetweenBlocks(BlockPos... positions) {
        float distance = 0;
        for (int i = 0; i < positions.length - 1; i++) {
            distance += (float) Math.sqrt(positions[i].distSqr(positions[i + 1]));
        }
        return (int) (distance * LASER_SPEED_MODIFIER);
    }

    public static List<BlockPos> findStabilizerSquare(BlockPos startingPos, Level level) {
        List<BlockPos> foundBlocks = new ArrayList<>();
        foundBlocks.add(startingPos);

        List<BlockPos> bestFoundBlocks = null;

        for (Direction direction : DIRECTIONS) {
            BlockPos nextStabilizer = findStabilizerInDirection(startingPos, direction, level);
            if (nextStabilizer == null) {
                continue;
            }
            foundBlocks.add(nextStabilizer);

            BlockPos tryRight = findStabilizerInDirection(nextStabilizer, direction.getClockWise(), level);

            if (tryRight != null) {
                foundBlocks.add(tryRight);
                BlockPos finalPosition;
                if (direction.getAxis() == Direction.Axis.X) {
                    finalPosition = new BlockPos(startingPos.getX(), startingPos.getY(), tryRight.getZ());
                } else {
                    finalPosition = new BlockPos(tryRight.getX(), startingPos.getY(), startingPos.getZ());
                }

                if (level.getBlockState(finalPosition).getBlock() instanceof QuarryStabilizer) {
                    return List.of(startingPos, nextStabilizer, tryRight, finalPosition);
                }
            } else {
                BlockPos tryLeft = findStabilizerInDirection(nextStabilizer, direction.getCounterClockWise(), level);
                if (tryLeft != null) {
                    foundBlocks.add(tryLeft);
                    BlockPos finalPosition;
                    if (direction.getAxis() == Direction.Axis.X) {
                        finalPosition = new BlockPos(startingPos.getX(), startingPos.getY(), tryLeft.getZ());
                    } else {
                        finalPosition = new BlockPos(tryLeft.getX(), startingPos.getY(), startingPos.getZ());
                    }

                    if (level.getBlockState(finalPosition).getBlock() instanceof QuarryStabilizer) {
                        return List.of(startingPos, nextStabilizer, tryLeft, finalPosition);
                    }
                }
            }

            if (bestFoundBlocks == null || bestFoundBlocks.size() < foundBlocks.size()) {
                bestFoundBlocks = foundBlocks;
            }
            foundBlocks = new ArrayList<>();
        }

        return bestFoundBlocks;
    }

    private static @Nullable BlockPos findStabilizerInDirection(BlockPos startingPos, Direction direction, Level level) {
        for (int i = 1; i < QUARRY_MAX_SIZE; i++) {
            if (level.getBlockState(startingPos.relative(direction, i)).getBlock() instanceof QuarryStabilizer) {
                return startingPos.relative(direction, i);
            }
        }

        return null;
    }
}

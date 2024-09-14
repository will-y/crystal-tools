package dev.willyelton.crystal_tools.common.levelable.block;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.renderer.QuarryLaserRenderer;
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

// TODO: Probably want to do a cool model and not extend torch block
public class QuarryStabilizer extends TorchBlock {
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

        if (level.isClientSide) {
            QuarryLaserRenderer.startTemporaryLaser(1000, stabilizerPositions.get(0), stabilizerPositions.get(1));
            QuarryLaserRenderer.startTemporaryLaser(1000, stabilizerPositions.get(1), stabilizerPositions.get(2));
            QuarryLaserRenderer.startTemporaryLaser(1000, stabilizerPositions.get(2), stabilizerPositions.get(3));
            QuarryLaserRenderer.startTemporaryLaser(1000, stabilizerPositions.get(3), stabilizerPositions.get(0));
        }
        // TODO: Put in some static helper, will need other places
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    // Go out direction
    // If found, go right and look
    // If not found go left and look
    // If left or right found, we know where the 4th block needs to be
    // Try next direction

    public static List<BlockPos> findStabilizerSquare(BlockPos startingPos, Level level) {
        List<BlockPos> foundBlocks = new ArrayList<>();

        for (Direction direction : DIRECTIONS) {
            BlockPos nextStabilizer = findStabilizerInDirection(startingPos, direction, level);
            if (nextStabilizer == null) {
                continue;
            }

            BlockPos tryRight = findStabilizerInDirection(nextStabilizer, direction.getClockWise(), level);

            if (tryRight != null) {
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
        }

        return foundBlocks;
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

package dev.willyelton.crystal_tools.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.function.Predicate;

public class BlockCollectors {
    public static List<BlockPos> collect3x3(BlockPos pos, Direction direction) {
        List<BlockPos> result = new ArrayList<>();
        result.add(pos);
        switch(direction) {
            case UP, DOWN -> {
                result.add(pos.north());
                result.add(pos.south());
                result.add(pos.east());
                result.add(pos.west());
                result.add(pos.north().east());
                result.add(pos.north().west());
                result.add(pos.south().east());
                result.add(pos.south().west());
            }
            case NORTH, SOUTH -> {
                result.add(pos.above());
                result.add(pos.below());
                result.add(pos.east());
                result.add(pos.west());
                result.add(pos.east().above());
                result.add(pos.west().above());
                result.add(pos.east().below());
                result.add(pos.west().below());
            }
            case EAST, WEST -> {
                result.add(pos.above());
                result.add(pos.below());
                result.add(pos.north());
                result.add(pos.south());
                result.add(pos.north().above());
                result.add(pos.south().above());
                result.add(pos.north().below());
                result.add(pos.south().below());
            }
        }
        return result;
    }

    public static List<BlockPos> collect3x3Hoe(BlockPos pos) {
        List<BlockPos> result = new ArrayList<>();
        result.add(pos);

        result.add(pos.north());
        result.add(pos.south());
        result.add(pos.east());
        result.add(pos.west());
        result.add(pos.north().east());
        result.add(pos.north().west());
        result.add(pos.south().east());
        result.add(pos.south().west());

        return result;
    }

    public static Collection<BlockPos> collectVeinMine(BlockPos pos, Level level, Predicate<BlockState> canHarvest, int maxBlocks) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toVisit = new LinkedList<>();
        int blocks = 0;

        // First one is guaranteed to be correct
        toVisit.add(pos);

        while(!toVisit.isEmpty() && blocks < maxBlocks) {
            BlockPos next = toVisit.poll();
            boolean added = visited.add(next);

            if (added) {
                // add children, all 8 around, up, only if correct state
                toVisit.addAll(getNextBlocks(next, level, canHarvest));
                blocks++;
            }
        }
        return visited;
    }

    private static List<BlockPos> getNextBlocks(BlockPos pos, Level level, Predicate<BlockState> canHarvest) {
        List<BlockPos> result = new ArrayList<>(getAdjacentPositions(pos));
        result.add(pos.above());
        result.add(pos.below());
        result.addAll(getAdjacentPositions(pos.above()));
        result.addAll(getAdjacentPositions(pos.below()));

//        List<BlockPos> result = List.of(miningPos.above(), miningPos.north(), miningPos.north().east(), miningPos.east(), miningPos.south().east(), miningPos.south(), miningPos.south().west(), miningPos.west(), miningPos.north().west(), miningPos.below());

        return result.stream().filter(x -> canHarvest.test(level.getBlockState(x))).toList();
    }

    /**
     * Gets all 8 positions around the given position
     * @param pos Input position
     * @return List of all blocks
     */
    private static List<BlockPos> getAdjacentPositions(BlockPos pos) {
        return List.of(pos.north(), pos.north().east(), pos.east(), pos.south().east(), pos.south(), pos.south().west(), pos.west(), pos.north().west());
    }
}

package dev.willyelton.crystal_tools.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

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
}

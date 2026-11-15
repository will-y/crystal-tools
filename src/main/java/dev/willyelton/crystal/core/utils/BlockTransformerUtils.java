package dev.willyelton.crystal.core.utils;

import net.minecraft.core.component.BlockTransformer;

import java.util.Arrays;
import java.util.Collection;

public class BlockTransformerUtils {
    private BlockTransformerUtils() {}

    public static BlockTransformer combinedBlockTransformer(BlockTransformer... blockTransformers) {
        return new BlockTransformer(Arrays.stream(blockTransformers).map(BlockTransformer::transforms).flatMap(Collection::stream).toList());
    }
}

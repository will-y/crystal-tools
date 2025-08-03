package dev.willyelton.crystal_tools.common.levelable.block.entity.action;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public interface ChunkLoader {
    boolean shouldUnload();

    BlockPos getBlockPos();

    /**
     * Determines if a chunk should be loaded as fully ticking.
     * Default implementation is to fully tick the chunk that the chunk loader is in
     * and not fully tick other chunks.
     * @param chunkPos The chunk position to check
     * @return true if the chunk should be forced as ticking
     */
    default boolean shouldTickChunk(ChunkPos chunkPos) {
        return chunkPos.equals(new ChunkPos(getBlockPos()));
    }
}

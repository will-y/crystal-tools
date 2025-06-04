package dev.willyelton.crystal_tools.common.levelable.block.entity.action;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.block.entity.LevelableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.world.chunk.LoadingValidationCallback;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import net.neoforged.neoforge.common.world.chunk.TicketSet;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.CHUNK_LOADING;

public class ChunkLoadingAction<T extends LevelableBlockEntity & ChunkLoader> extends Action {
    public static final TicketController TICKET_CONTROLLER = new TicketController(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "chunk_loader"), ChunkLoadingValidationCallback.INSTANCE);


    private final T blockEntity;
    private boolean chunkLoadingEnabled;
    private final Set<Long> chunkSet = new HashSet<>();

    public ChunkLoadingAction(T blockEntity) {
        super(20);

        this.blockEntity = blockEntity;
    }

    @Override
    public void tickAction(@NotNull Level level, BlockPos pos, BlockState state) {

    }

    @Override
    public ActionType getActionType() {
        return ActionType.CHUNK_LOAD;
    }

    @Override
    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        this.chunkLoadingEnabled = tag.getBoolean("ChunkLoading").orElse(false);
        chunkSet.addAll(Arrays.stream(tag.getLongArray("ChunkSet").orElse(new long[0])).boxed().toList());
    }

    @Override
    public void save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putBoolean("ChunkLoading", this.chunkLoadingEnabled);
        tag.putLongArray("ChunkSet", this.chunkSet.stream().mapToLong(Long::longValue).toArray());
    }

    @Override
    public boolean addToExtra(String key, float value) {
        if (CHUNK_LOADING.toString().equals(key)) {
            this.chunkLoadingEnabled = true;
            this.loadChunk((ServerLevel) blockEntity.getLevel(), new ChunkPos(blockEntity.getBlockPos()));
            return true;
        }

        return false;
    }

    @Override
    public void onRemove() {
        if (blockEntity.getLevel() instanceof ServerLevel serverLevel) {
            this.unloadAll(serverLevel);
        }
    }

    @Override
    public void applyComponents(DataComponentGetter componentInput) {
        super.applyComponents(componentInput);
        this.chunkLoadingEnabled = componentInput.getOrDefault(DataComponents.CHUNKLOADING, false);
    }

    @Override
    public void collectComponents(DataComponentMap.Builder components) {
        super.collectComponents(components);
        components.set(DataComponents.CHUNKLOADING, this.chunkLoadingEnabled);
    }

    @Override
    public void resetExtra() {
        this.chunkLoadingEnabled = false;
    }

    @Override
    public boolean isActive() {
        return chunkLoadingEnabled;
    }

    public void loadChunk(ServerLevel level, ChunkPos chunkPos) {
        boolean ticking = blockEntity.shouldTickChunk(chunkPos);
        chunkSet.add(chunkPos.toLong());
        blockEntity.setChanged();
        TICKET_CONTROLLER.forceChunk(level, blockEntity.getBlockPos(), chunkPos.x, chunkPos.z, true, ticking);
    }

    public void unloadChunk(ServerLevel level, ChunkPos chunkPos) {
        boolean ticking = blockEntity.shouldTickChunk(chunkPos);
        chunkSet.remove(chunkPos.toLong());
        blockEntity.setChanged();
        TICKET_CONTROLLER.forceChunk(level, blockEntity.getBlockPos(), chunkPos.x, chunkPos.z, false, ticking);
    }

    public void unloadAll(ServerLevel serverLevel) {
        for (Long chunkLong : chunkSet) {
            ChunkPos chunkPos = new ChunkPos(chunkLong);

            // Try to remove the ticking and non ticking version for each chunk because
            // We don't store which one it was registered to
            TICKET_CONTROLLER.forceChunk(serverLevel, blockEntity.getBlockPos(), chunkPos.x, chunkPos.z, false, true);
            TICKET_CONTROLLER.forceChunk(serverLevel, blockEntity.getBlockPos(), chunkPos.x, chunkPos.z, false, false);
        }

        chunkSet.clear();
        blockEntity.setChanged();
    }

    private static class ChunkLoadingValidationCallback implements LoadingValidationCallback {
        static final ChunkLoadingValidationCallback INSTANCE = new ChunkLoadingValidationCallback();

        @Override
        public void validateTickets(ServerLevel level, TicketHelper ticketHelper) {
            for (Map.Entry<BlockPos, TicketSet> entries : ticketHelper.getBlockTickets().entrySet()) {
                BlockPos pos = entries.getKey();
                BlockEntity blockEntity = level.getBlockEntity(pos);

                if (blockEntity instanceof ChunkLoader chunkLoader) {
                    if (chunkLoader.shouldUnload()) {
                        ticketHelper.removeAllTickets(pos);
                    }
                } else {
                    ticketHelper.removeAllTickets(pos);
                }
            }
        }
    }
}

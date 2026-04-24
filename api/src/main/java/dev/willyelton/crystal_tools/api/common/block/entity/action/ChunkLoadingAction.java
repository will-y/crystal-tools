package dev.willyelton.crystal_tools.api.common.block.entity.action;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal_tools.api.Registration;
import dev.willyelton.crystal_tools.api.common.block.entity.ActionBlockEntity;
import dev.willyelton.crystal_tools.api.common.datacomponent.DataComponents;
import dev.willyelton.crystal_tools.api.utils.constants.ApiConstants;
import dev.willyelton.crystal_tools.api.utils.constants.BlockEntityIdentifiers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.world.chunk.LoadingValidationCallback;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import net.neoforged.neoforge.common.world.chunk.TicketSet;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChunkLoadingAction extends Action {
    public static final TicketController TICKET_CONTROLLER = new TicketController(Identifier.fromNamespaceAndPath(ApiConstants.CORE_MOD_ID, "chunk_loader"), ChunkLoadingValidationCallback.INSTANCE);

    private final ChunkLoader chunkLoader;
    private boolean chunkLoadingEnabled;
    private final Set<Long> chunkSet = new HashSet<>();

    public ChunkLoadingAction(ActionBlockEntity blockEntity, @Nullable ActionParameters params, ChunkLoader chunkLoader) {
        super(blockEntity, params);

        this.chunkLoader = chunkLoader;
    }

    public ChunkLoadingAction(ActionBlockEntity blockEntity, ChunkLoader chunkLoader) {
        this(blockEntity, null, chunkLoader);
    }

    @Override
    public Identifier getActionType() {
        return Registration.CHUNK_LOAD_ACTION.getId();
    }

    @Override
    public ActionParameters getDefaultParameters() {
        return new ActionParameters(20);
    }

    @Override
    public void load(ValueInput valueInput) {
        this.chunkLoadingEnabled = valueInput.getBooleanOr("ChunkLoading", false);
        chunkSet.addAll(valueInput.read("ChunkSet", Codec.LONG.listOf()).orElse(List.of()));
    }

    @Override
    public void save(ValueOutput valueOutput) {
        valueOutput.putBoolean("ChunkLoading", this.chunkLoadingEnabled);
        valueOutput.store("ChunkSet", Codec.LONG.listOf(), chunkSet.stream().toList());
    }

    @Override
    public boolean addToExtra(String key, float value) {
        if (BlockEntityIdentifiers.CHUNK_LOADING.toString().equals(key)) {
            this.chunkLoadingEnabled = true;
            this.loadChunk((ServerLevel) blockEntity.getLevel(), ChunkPos.containing(blockEntity.getBlockPos()));
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
        boolean ticking = chunkLoader.shouldTickChunk(chunkPos);
        chunkSet.add(chunkPos.pack());
        blockEntity.setChanged();
        TICKET_CONTROLLER.forceChunk(level, blockEntity.getBlockPos(), chunkPos.x(), chunkPos.z(), true, ticking);
    }

    public void unloadChunk(ServerLevel level, ChunkPos chunkPos) {
        boolean ticking = chunkLoader.shouldTickChunk(chunkPos);
        chunkSet.remove(chunkPos.pack());
        blockEntity.setChanged();
        TICKET_CONTROLLER.forceChunk(level, blockEntity.getBlockPos(), chunkPos.x(), chunkPos.z(), false, ticking);
    }

    public void unloadAll(ServerLevel serverLevel) {
        for (Long chunkLong : chunkSet) {
            ChunkPos chunkPos = ChunkPos.unpack(chunkLong);

            // Try to remove the ticking and non ticking version for each chunk because
            // We don't store which one it was registered to
            TICKET_CONTROLLER.forceChunk(serverLevel, blockEntity.getBlockPos(), chunkPos.x(), chunkPos.z(), false, true);
            TICKET_CONTROLLER.forceChunk(serverLevel, blockEntity.getBlockPos(), chunkPos.x(), chunkPos.z(), false, false);
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

package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalQuarryBlockEntity;
import dev.willyelton.crystal_tools.common.network.data.QuarryMineBlockPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class QuarryMineBlockHandler {
    public static QuarryMineBlockHandler INSTANCE = new QuarryMineBlockHandler();

    public void handle(final QuarryMineBlockPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            BlockEntity be = level.getBlockEntity(payload.quarryPos());

            if (be instanceof CrystalQuarryBlockEntity crystalQuarryBlockEntity) {
                crystalQuarryBlockEntity.setMiningAt(payload.miningPos());
                crystalQuarryBlockEntity.setMiningState(payload.state());
            }
        });
    }
}

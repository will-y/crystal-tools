package dev.willyelton.crystal.core.common.network.handler;

import dev.willyelton.crystal.core.common.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal.core.common.network.payload.ResetSkillsBlockPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ResetSkillsBlockHandler {
    public static ResetSkillsBlockHandler INSTANCE = new ResetSkillsBlockHandler();

    public void handle(final ResetSkillsBlockPayload payload, final IPayloadContext context) {
        Level level = context.player().level();

        BlockEntity blockEntity = level.getBlockEntity(payload.blockPos());

        if (blockEntity instanceof LevelableBlockEntity levelableBlockEntity) {
            levelableBlockEntity.resetSkills();
        }
    }
}

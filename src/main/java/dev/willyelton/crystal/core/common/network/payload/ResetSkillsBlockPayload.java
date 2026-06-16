package dev.willyelton.crystal.core.common.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static dev.willyelton.crystal.core.utils.constants.ApiConstants.baseRl;

public record ResetSkillsBlockPayload(BlockPos blockPos) implements CustomPacketPayload {
    public static final Type<ResetSkillsBlockPayload> TYPE = new Type<>(baseRl("reset_skills_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResetSkillsBlockPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ResetSkillsBlockPayload::blockPos,
            ResetSkillsBlockPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

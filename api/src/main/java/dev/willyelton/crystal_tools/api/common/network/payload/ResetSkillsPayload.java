package dev.willyelton.crystal_tools.api.common.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static dev.willyelton.crystal_tools.api.utils.constants.ApiConstants.baseRl;

public record ResetSkillsPayload() implements CustomPacketPayload {
    public static final ResetSkillsPayload INSTANCE = new ResetSkillsPayload();
    public static final Type<ResetSkillsPayload> TYPE = new Type<>(baseRl("reset_skills"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResetSkillsPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

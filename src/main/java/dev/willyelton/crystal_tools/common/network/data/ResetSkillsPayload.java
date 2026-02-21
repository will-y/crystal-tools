package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ResetSkillsPayload() implements CustomPacketPayload {
    public static final ResetSkillsPayload INSTANCE = new ResetSkillsPayload();
    public static final Type<ResetSkillsPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CrystalTools.MODID, "reset_skills"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResetSkillsPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

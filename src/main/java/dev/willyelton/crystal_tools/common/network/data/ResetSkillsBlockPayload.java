package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ResetSkillsBlockPayload(BlockPos blockPos) implements CustomPacketPayload {
    public static final Type<ResetSkillsBlockPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "reset_skills_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResetSkillsBlockPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ResetSkillsBlockPayload::blockPos,
            ResetSkillsBlockPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

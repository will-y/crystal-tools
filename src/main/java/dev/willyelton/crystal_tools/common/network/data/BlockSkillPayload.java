package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record BlockSkillPayload(int nodeId, ResourceKey<SkillData> key, int pointsToSpend, BlockPos pos) implements CustomPacketPayload {
    public static final Type<BlockSkillPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "block_skill"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockSkillPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, BlockSkillPayload::nodeId,
            ResourceKey.streamCodec(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_BLOCKS), BlockSkillPayload::key,
            ByteBufCodecs.INT, BlockSkillPayload::pointsToSpend,
            BlockPos.STREAM_CODEC, BlockSkillPayload::pos,
            BlockSkillPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

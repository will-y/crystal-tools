package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record ToolSkillPayload(int nodeId, ResourceKey<SkillData> key, int pointsToSpend) implements CustomPacketPayload {
    public static final Type<ToolSkillPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "tool_skill"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolSkillPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ToolSkillPayload::nodeId,
            ResourceKey.streamCodec(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY), ToolSkillPayload::key,
            ByteBufCodecs.INT, ToolSkillPayload::pointsToSpend,
            ToolSkillPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

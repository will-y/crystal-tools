package dev.willyelton.crystal_tools.api.common.network.payload;

import dev.willyelton.crystal_tools.api.common.event.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.api.common.skill.SkillData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;

import static dev.willyelton.crystal_tools.api.utils.constants.ApiConstants.baseRl;

public record ToolSkillPayload(int nodeId, ResourceKey<SkillData> key, int pointsToSpend) implements CustomPacketPayload {
    public static final Type<ToolSkillPayload> TYPE = new Type<>(baseRl("tool_skill"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolSkillPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ToolSkillPayload::nodeId,
            ResourceKey.streamCodec(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS), ToolSkillPayload::key,
            ByteBufCodecs.INT, ToolSkillPayload::pointsToSpend,
            ToolSkillPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

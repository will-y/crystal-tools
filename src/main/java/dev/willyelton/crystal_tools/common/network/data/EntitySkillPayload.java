package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

public record EntitySkillPayload(int nodeId, ResourceKey<SkillData> key, int pointsToSpend,
                                 int entityId) implements CustomPacketPayload {

    public static final Type<EntitySkillPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CrystalTools.MODID, "entity_skill"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EntitySkillPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, EntitySkillPayload::nodeId,
            ResourceKey.streamCodec(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ENTITIES), EntitySkillPayload::key,
            ByteBufCodecs.INT, EntitySkillPayload::pointsToSpend,
            ByteBufCodecs.INT, EntitySkillPayload::entityId,
            EntitySkillPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SkillCacheUpdatePayload(String tool, SkillData data) implements CustomPacketPayload {
    public static final Type<SkillCacheUpdatePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "skill_cache_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SkillCacheUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SkillCacheUpdatePayload::tool,
            SkillData.STREAM_CODEC, SkillCacheUpdatePayload::data,
            SkillCacheUpdatePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

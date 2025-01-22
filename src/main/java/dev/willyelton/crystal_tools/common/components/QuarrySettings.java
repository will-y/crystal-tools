package dev.willyelton.crystal_tools.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record QuarrySettings(boolean useDirt, boolean silkTouchEnabled,
                             boolean fortuneEnabled, boolean autoOutputEnabled) {
    public static final Codec<QuarrySettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("useDirt").forGetter(QuarrySettings::useDirt),
            Codec.BOOL.fieldOf("silkTouchEnabled").forGetter(QuarrySettings::silkTouchEnabled),
            Codec.BOOL.fieldOf("fortuneEnabled").forGetter(QuarrySettings::fortuneEnabled),
            Codec.BOOL.fieldOf("autoOutputEnabled").forGetter(QuarrySettings::autoOutputEnabled)
    ).apply(instance, QuarrySettings::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuarrySettings> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, QuarrySettings::useDirt,
            ByteBufCodecs.BOOL, QuarrySettings::silkTouchEnabled,
            ByteBufCodecs.BOOL, QuarrySettings::fortuneEnabled,
            ByteBufCodecs.BOOL, QuarrySettings::autoOutputEnabled,
            QuarrySettings::new);
}

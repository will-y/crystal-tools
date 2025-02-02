package dev.willyelton.crystal_tools.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record GeneratorData(int litTime, int litTotalTime, ItemStack burnedItem, int energy) {
    public static final Codec<GeneratorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("litTime").forGetter(GeneratorData::litTime),
            Codec.INT.fieldOf("litTotalTime").forGetter(GeneratorData::litTotalTime),
            ItemStack.OPTIONAL_CODEC.fieldOf("burnedItem").forGetter(GeneratorData::burnedItem),
            Codec.INT.fieldOf("energy").forGetter(GeneratorData::energy)
    ).apply(instance, GeneratorData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GeneratorData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, GeneratorData::litTime,
            ByteBufCodecs.INT, GeneratorData::litTotalTime,
            ItemStack.OPTIONAL_STREAM_CODEC, GeneratorData::burnedItem,
            ByteBufCodecs.INT, GeneratorData::energy,
            GeneratorData::new);

    public GeneratorData() {
        this(0, 0, ItemStack.EMPTY, 0);
    }
}

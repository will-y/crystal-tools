package dev.willyelton.crystal.tools.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

import static net.minecraft.network.codec.ByteBufCodecs.list;

public record FurnaceData(int litTime, int litDuration, List<Integer> cookingProgress, List<Integer> cookingTime, float expHeld,
                          List<ItemStack> inputItems, List<ItemStack> outputItems, List<ItemStack> fuelItems) {
    public static final Codec<FurnaceData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("litTime").forGetter(FurnaceData::litTime),
            Codec.INT.fieldOf("litDuration").forGetter(FurnaceData::litDuration),
            Codec.INT.listOf().fieldOf("cookingProgress").forGetter(FurnaceData::cookingProgress),
            Codec.INT.listOf().fieldOf("cookingTime").forGetter(FurnaceData::cookingTime),
            Codec.FLOAT.fieldOf("expHeld").forGetter(FurnaceData::expHeld),
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("inputItems").forGetter(FurnaceData::inputItems),
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("outputItems").forGetter(FurnaceData::outputItems),
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("fuelItems").forGetter(FurnaceData::fuelItems)
    ).apply(instance, FurnaceData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FurnaceData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, FurnaceData::litTime,
            ByteBufCodecs.INT, FurnaceData::litDuration,
            ByteBufCodecs.INT.apply(list()), FurnaceData::cookingProgress,
            ByteBufCodecs.INT.apply(list()), FurnaceData::cookingTime,
            ByteBufCodecs.FLOAT, FurnaceData::expHeld,
            ItemStack.OPTIONAL_STREAM_CODEC.apply(list()), FurnaceData::inputItems,
            ItemStack.OPTIONAL_STREAM_CODEC.apply(list()), FurnaceData::outputItems,
            ItemStack.OPTIONAL_STREAM_CODEC.apply(list()), FurnaceData::fuelItems,
            FurnaceData::new);

    public FurnaceData() {
        this(0, 0, Collections.emptyList(), Collections.emptyList(), 0F, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }
}

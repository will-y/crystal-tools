package dev.willyelton.crystal_tools.utils;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class CodecUtils {
    public static StreamCodec<RegistryFriendlyByteBuf, ResourceLocation> RESOURCE_LOCATION_STREAM_CODEC =
            StreamCodec.of(RegistryFriendlyByteBuf::writeResourceLocation, RegistryFriendlyByteBuf::readResourceLocation);

    // Json doesn't like integer key maps
    public static final Codec<Integer> STRINT = Codec.STRING.xmap(Integer::parseInt, String::valueOf);
}

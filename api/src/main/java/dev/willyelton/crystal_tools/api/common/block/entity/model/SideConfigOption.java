package dev.willyelton.crystal_tools.api.common.block.entity.model;

import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum SideConfigOption implements StringRepresentable {
    OUTPUT(0, "Output"),
    INPUT(20, "Input"),
    DISABLED(40, "Disabled"),
    NONE(60, "Default"),
    FUEL_INPUT(80, "Fuel Input");

    public static final Codec<SideConfigOption> CODEC = StringRepresentable.fromEnum(SideConfigOption::values);
    public static final StreamCodec<RegistryFriendlyByteBuf, SideConfigOption> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(SideConfigOption.class);
    public static final Codec<Map<Direction, SideConfigOption>> DIRECTION_MAP_CODEC = Codec.unboundedMap(Direction.CODEC, CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Map<Direction, SideConfigOption>> DIRECTION_MAP_STREAM_CODEC = ByteBufCodecs.map(i -> new HashMap<>(), Direction.STREAM_CODEC, STREAM_CODEC);


    public final int textureXOffset;
    private final String readableName;

    SideConfigOption(int textureXOffset, String readableName) {
        this.textureXOffset = textureXOffset;
        this.readableName = readableName;
    }

    public SideConfigOption next() {
        return switch (this) {
            case OUTPUT -> INPUT;
            case INPUT -> FUEL_INPUT;
            case FUEL_INPUT -> DISABLED;
            case DISABLED -> NONE;
            case NONE -> OUTPUT;
        };
    }

    public SideConfigOption next(Set<SideConfigOption> options) {
        SideConfigOption next = this.next();
        while (!options.contains(next)) {
            next = next.next(options);
        }

        return next;
    }

    public String readableName() {
        return readableName;
    }

    public static Set<SideConfigOption> all() {
        return Set.of(OUTPUT, INPUT, FUEL_INPUT, DISABLED, NONE);
    }

    @Override
    public String getSerializedName() {
        return name();
    }
}

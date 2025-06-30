package dev.willyelton.crystal_tools.common.levelable.tool;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum UseMode implements StringRepresentable {
    HOE,
    SHOVEL,
    AXE,
    TORCH;

    public static Codec<UseMode> CODEC = StringRepresentable.fromEnum(UseMode::values);

    public static StreamCodec<FriendlyByteBuf, UseMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(UseMode.class);

    public static UseMode fromString(String string) {
        if (string.equalsIgnoreCase("hoe")) {
            return HOE;
        } else if (string.equalsIgnoreCase("shovel")) {
            return SHOVEL;
        } else if (string.equalsIgnoreCase("axe")) {
            return AXE;
        } else if (string.equalsIgnoreCase("torch")) {
            return TORCH;
        } else {
            return HOE;
        }
    }

    public static UseMode nextMode(ItemStack tool, UseMode mode) {
        switch (mode) {
            case HOE -> {
                return SHOVEL;
            }
            case SHOVEL -> {
                return AXE;
            }
            case AXE -> {
                if (tool.getOrDefault(DataComponents.TORCH, false)) {
                    return TORCH;
                } else {
                    return HOE;
                }
            }
            case TORCH -> {
                return HOE;
            }
        }

        return HOE;
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}

package dev.willyelton.crystal.tools.common.levelable.tool;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal.core.common.datacomponent.DataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public enum UseMode implements StringRepresentable {
    HOE(BlockTransformers.HOE),
    SHOVEL(BlockTransformers.SHOVEL),
    AXE(BlockTransformers.AXE),
    TORCH(null);

    private final @Nullable ResourceKey<BlockTransformer> blockTransformer;

    public static Codec<UseMode> CODEC = StringRepresentable.fromEnum(UseMode::values);

    public static StreamCodec<FriendlyByteBuf, UseMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(UseMode.class);

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

    UseMode(ResourceKey<BlockTransformer> blockTransformer) {
        this.blockTransformer = blockTransformer;
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }

    public Optional<Holder.Reference<BlockTransformer>> blockTransformer(Level level) {
        if (this.blockTransformer == null) {
            return Optional.empty();
        }

        return level.registryAccess().get(this.blockTransformer);
    }
}

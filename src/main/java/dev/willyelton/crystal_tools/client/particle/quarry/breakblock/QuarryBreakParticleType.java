package dev.willyelton.crystal_tools.client.particle.quarry.breakblock;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class QuarryBreakParticleType extends ParticleType<QuarryBreakParticleData> {
    public QuarryBreakParticleType(boolean overrideLimitter) {
        super(overrideLimitter);
    }

    @Override
    public MapCodec<QuarryBreakParticleData> codec() {
        return QuarryBreakParticleData.MAP_CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, QuarryBreakParticleData> streamCodec() {
        return QuarryBreakParticleData.STREAM_CODEC;
    }
}

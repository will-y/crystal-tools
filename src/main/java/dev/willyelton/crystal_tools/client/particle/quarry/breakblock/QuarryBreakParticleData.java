package dev.willyelton.crystal_tools.client.particle.quarry.breakblock;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.ModRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record QuarryBreakParticleData(BlockState blockState, BlockPos startPos, BlockPos endPos) implements ParticleOptions {
    public static final MapCodec<QuarryBreakParticleData> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BlockState.CODEC.fieldOf("blockState").forGetter(QuarryBreakParticleData::blockState),
                    BlockPos.CODEC.fieldOf("startPos").forGetter(QuarryBreakParticleData::startPos),
                    BlockPos.CODEC.fieldOf("endPos").forGetter(QuarryBreakParticleData::endPos)
            ).apply(instance, QuarryBreakParticleData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuarryBreakParticleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), QuarryBreakParticleData::blockState,
            BlockPos.STREAM_CODEC, QuarryBreakParticleData::startPos,
            BlockPos.STREAM_CODEC, QuarryBreakParticleData::endPos,
            QuarryBreakParticleData::new);

    @Override
    public ParticleType<?> getType() {
        return ModRegistration.QUARRY_BREAK_PARTICLE.get();
    }
}

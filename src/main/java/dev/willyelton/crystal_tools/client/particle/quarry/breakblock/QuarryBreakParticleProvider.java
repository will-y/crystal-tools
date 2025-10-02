package dev.willyelton.crystal_tools.client.particle.quarry.breakblock;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public class QuarryBreakParticleProvider implements ParticleProvider<QuarryBreakParticleData> {
    @Override
    public @Nullable Particle createParticle(QuarryBreakParticleData data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource p_447205_) {
        // Send the start position in the data because we want the server to spawn the particles when
        // the player is near the quarry not near the block being mined
        return new QuarryBreakParticle(level, data.startPos().getX() + 0.5, data.startPos().getY() + 1,
                data.startPos().getZ() + 0.5, data.endPos().getX(), data.endPos().getY(),
                data.endPos().getZ(), data.blockState());
    }
}

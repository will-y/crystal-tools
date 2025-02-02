package dev.willyelton.crystal_tools.client.particle.quarry.breakblock;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class QuarryBreakParticle extends TerrainParticle {
    private final double xf;
    private final double yf;
    private final double zf;

    private final double initialX;
    private final double initialY;
    private final double initialZ;

    public QuarryBreakParticle(ClientLevel level, double x, double y, double z, double xf, double yf, double zf, BlockState state) {
        super(level, x, y, z, 0, 0, 0, state);

        gravity = 0;
        this.xf = xf + 0.5 + random.nextGaussian() * 0.1;
        this.yf = yf + 0.5;
        this.zf = zf + 0.5 + random.nextGaussian() * 0.1;

        this.initialX = x;
        this.initialY = y;
        this.initialZ = z;

        this.lifetime = (int) Mth.length(xf - x, yf - y, zf - z) * 4 + 1;

        quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F);

//        this.lifetime = (int)(40.0F + (this.random.nextFloat() * 10));
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            double timeFraction = this.age / (double) this.lifetime;
            move(dx(timeFraction) - xo, dy(timeFraction) - yo, dz(timeFraction) - zo);
        }
    }

    @Override
    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().move(x, y, z));
        this.setLocationFromBoundingbox();
    }

    private double dx(double time) {
        return Mth.lerp(time, initialX, xf);
    }

    private double dy(double time) {
        return (time - 12.5) * (time - 1.36) * (time + 3.5) * (time + 0.9) * time * (yf - initialY) / 35.6 + initialY;
//        return (time - 10) * (time - 2.3) * (time - 2.4) * (time - 4.7) * time * (yf - initialY) / 60.0 + initialY;
    }

    private double dz(double time) {
        return Mth.lerp(time, initialZ, zf);
    }
}

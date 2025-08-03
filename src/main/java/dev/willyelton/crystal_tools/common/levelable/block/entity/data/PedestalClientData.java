package dev.willyelton.crystal_tools.common.levelable.block.entity.data;

import net.minecraft.util.Mth;

public class PedestalClientData {
    private static final float ROTATION_SPEED = 3.0F;

    private float currentRot;
    private float previousRot;
    private float currentHeight;
    private float previousHeight;
    private int age;

    public float currentRot() {
        return currentRot;
    }

    public float previousRot() {
        return previousRot;
    }

    public float currentHeight() {
        return currentHeight;
    }

    public float previousHeight() {
        return previousHeight;
    }

    public void update() {
        this.previousRot = this.currentRot;
        this.currentRot = Mth.wrapDegrees(this.currentRot + ROTATION_SPEED);

        this.previousHeight = this.currentHeight;

        this.currentHeight = Mth.sin(age++ / 10.0F) * 0.1F + 0.1F;
    }
}

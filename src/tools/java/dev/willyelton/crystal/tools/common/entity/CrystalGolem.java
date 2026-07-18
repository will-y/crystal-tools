package dev.willyelton.crystal.tools.common.entity;

import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.tools.common.entity.ai.CrystalGolemAi;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.level.Level;

import java.util.List;

public class CrystalGolem extends CopperGolem {
    private static final Brain.Provider<CopperGolem> BRAIN_PROVIDER = Brain.provider(
            List.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY), CrystalGolemAi::getActivities);

    public CrystalGolem(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
    }

    @Override
    public EntityType<?> getType() {
        return ModRegistration.CRYSTAL_GOLEM_ENTITY.get();
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        this.getBrain().tick(level, this);
        CrystalGolemAi.updateActivity(this);
    }

    @Override
    protected Brain<CopperGolem> makeBrain(Brain.Packed packedBrain) {
        return BRAIN_PROVIDER.makeBrain(this, packedBrain);
    }
}

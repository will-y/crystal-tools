package dev.willyelton.crystal.tools.common.entity;

import dev.willyelton.crystal.core.common.capability.LevelableEntity;
import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.tools.common.entity.ai.CrystalGolemAi;
import dev.willyelton.crystal.tools.utils.constants.EntitySkills;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static dev.willyelton.crystal.core.utils.ListUtils.firstN;

public class CrystalGolem extends CopperGolem {
    private static final Brain.Provider<CopperGolem> BRAIN_PROVIDER = Brain.provider(
            List.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY), CrystalGolemAi::getActivities);

    private static final EntityDataAccessor<List<BlockPosDirection>> SOURCE_POSITIONS = SynchedEntityData.defineId(
            CrystalGolem.class, ModRegistration.BLOCK_POSITION_DIRECTION_SERIALIZER.get());
    private static final EntityDataAccessor<List<BlockPosDirection>> DESTINATION_POSITIONS = SynchedEntityData.defineId(
            CrystalGolem.class, ModRegistration.BLOCK_POSITION_DIRECTION_SERIALIZER.get());

    private @Nullable LevelableEntity levelableEntity;

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

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);

        entityData.define(SOURCE_POSITIONS, List.of());
        entityData.define(DESTINATION_POSITIONS, List.of());
    }

    public Set<BlockPosDirection> getSourcePositions() {
        return new HashSet<>(this.entityData.get(SOURCE_POSITIONS));
    }

    public Set<BlockPosDirection> getDestinationPositions() {
        return new HashSet<>(this.entityData.get(DESTINATION_POSITIONS));
    }

    public int setSourcePositions(List<BlockPosDirection> sourcePositions) {
        List<BlockPosDirection> positionsToAdd = firstN(sourcePositions, this.getMaxSourcePositions());
        this.entityData.set(SOURCE_POSITIONS, positionsToAdd);

        return positionsToAdd.size();
    }

    public int setDestinationPositions(List<BlockPosDirection> destinationPositions) {
        List<BlockPosDirection> positionsToAdd = firstN(destinationPositions, this.getMaxDestinationPositions());
        this.entityData.set(DESTINATION_POSITIONS, positionsToAdd);

        return positionsToAdd.size();
    }

    private @Nullable LevelableEntity getLevelableEntity() {
        if (this.levelableEntity == null) {
            this.levelableEntity = LevelableEntity.of(this, this.level().registryAccess());
        }

        return this.levelableEntity;
    }

    public int getMaxSourcePositions() {
        LevelableEntity levelableEntity = this.getLevelableEntity();
        if (levelableEntity != null) {
            return (int) levelableEntity.getEntitySkillData().getSkillValue(EntitySkills.SOURCE_SELECTION);
        }

        return 0;
    }

    public int getMaxDestinationPositions() {
        LevelableEntity levelableEntity = this.getLevelableEntity();
        if (levelableEntity != null) {
            return (int) levelableEntity.getEntitySkillData().getSkillValue(EntitySkills.DESTINATION_SELECTION);
        }

        return 0;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        output.store("source_positions", BlockPosDirection.CODEC.listOf(), this.getSourcePositions().stream().toList());
        output.store("destination_positions", BlockPosDirection.CODEC.listOf(), this.getDestinationPositions().stream().toList());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        this.setSourcePositions(input.read("source_positions", BlockPosDirection.CODEC.listOf()).orElse(List.of()));
        this.setDestinationPositions(input.read("destination_positions", BlockPosDirection.CODEC.listOf()).orElse(List.of()));
    }
}

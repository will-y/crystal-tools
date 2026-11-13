package dev.willyelton.crystal.tools.common.entity;

import dev.willyelton.crystal.core.common.capability.LevelableEntity;
import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.tools.common.entity.ai.CrystalGolemAi;
import dev.willyelton.crystal.tools.common.inventory.CrystalGolemItemStackHandler;
import dev.willyelton.crystal.tools.utils.constants.EntitySkills;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static dev.willyelton.crystal.core.utils.ListUtils.firstN;

public class CrystalGolem extends CopperGolem {
    private static final Brain.Provider<CrystalGolem> BRAIN_PROVIDER = Brain.provider(
            List.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY), CrystalGolemAi::getActivities);

    private static final EntityDataAccessor<List<BlockPosDirection>> SOURCE_POSITIONS = SynchedEntityData.defineId(
            CrystalGolem.class, ModRegistration.BLOCK_POSITION_DIRECTION_SERIALIZER.get());
    private static final EntityDataAccessor<List<BlockPosDirection>> DESTINATION_POSITIONS = SynchedEntityData.defineId(
            CrystalGolem.class, ModRegistration.BLOCK_POSITION_DIRECTION_SERIALIZER.get());

    private @Nullable LevelableEntity levelableEntity;
    private final CrystalGolemItemStackHandler itemStackHandler;

    public CrystalGolem(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);

        itemStackHandler = new CrystalGolemItemStackHandler(1, 16);
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
    @SuppressWarnings("unchecked")
    protected Brain<CopperGolem> makeBrain(Brain.Packed packedBrain) {
        return (Brain<CopperGolem>) ((Brain<?>) BRAIN_PROVIDER.makeBrain(this, packedBrain));
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

    public CrystalGolemItemStackHandler getInventoryCapability() {
        return itemStackHandler;
    }

    public ItemStack firstItem() {
        return itemStackHandler.firstItem();
    }

    // TODO: Options here. 1: update renderer to use inventory? or 2: always make sure there is an item in the main hand
    public ItemStack addToInventory(ItemStack stack) {
        ItemStack mainHandStack = this.getItemBySlot(EquipmentSlot.MAINHAND);
        if (mainHandStack.isEmpty() || ItemStack.isSameItemSameComponents(mainHandStack, stack)) {
            int totalCount = stack.count() + mainHandStack.count();

            ItemStack newMainHandStack = new ItemStack(stack.getItem());

            if (totalCount > stack.getMaxStackSize()) {
                stack.setCount(totalCount - stack.getMaxStackSize());
                newMainHandStack.setCount(stack.getMaxStackSize());
            } else {
                stack.setCount(0);
                newMainHandStack.setCount(totalCount);
            }

            this.setItemSlot(EquipmentSlot.MAINHAND, newMainHandStack);
            setGuaranteedDrop(EquipmentSlot.MAINHAND);
        }

        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            return stack;
        }
    }

    // TODO: Shouldn't need this
    public void setInventory(List<ItemStack> stacks) {
        this.setItemSlot(EquipmentSlot.MAINHAND, stacks.getFirst());
    }

    public boolean canAcceptStack(ItemStack stack) {
        return getMainHandItem().isEmpty() || (ItemStack.isSameItem(getMainHandItem(), stack)
                && getMainHandItem().getMaxStackSize() > getMainHandItem().count());
    }

    // TODO: Increase
    public double getInteractionRange() {
        return getNavigation().getPath() != null && getNavigation().getPath().isDone() ? 1.0 : 0.5;
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

        itemStackHandler.serialize(output);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        this.setSourcePositions(input.read("source_positions", BlockPosDirection.CODEC.listOf()).orElse(List.of()));
        this.setDestinationPositions(input.read("destination_positions", BlockPosDirection.CODEC.listOf()).orElse(List.of()));

        itemStackHandler.deserialize(input);
    }
}

package dev.willyelton.crystal.tools.common.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import dev.willyelton.crystal.tools.common.entity.BlockPosDirection;
import dev.willyelton.crystal.tools.common.entity.CrystalGolem;
import dev.willyelton.crystal.tools.common.entity.ai.behavior.TransportItemsBetweenHandlers;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.animal.golem.CopperGolemState;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CrystalGolemAi {
    // TODO: These will change to either my tags, or if overwritten by entity, that logic
    private static final Predicate<TransportItemsBetweenHandlers.TransportItemTarget> DEFAULT_TRANSPORT_ITEM_SOURCE_BLOCK = block -> block.state().is(BlockTags.COPPER_CHESTS);
    private static final Predicate<TransportItemsBetweenHandlers.TransportItemTarget> DEFAULT_TRANSPORT_ITEM_DESTINATION_BLOCK = block -> block.state().is(Blocks.CHEST) || block.state().is(Blocks.TRAPPED_CHEST);

    public static void updateActivity(CrystalGolem body) {
        body.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }

    public static List<ActivityData<CopperGolem>> getActivities(CopperGolem body) {
        if (body instanceof CrystalGolem crystalGolem) {
            return List.of(initCoreActivity(), initIdleActivity(crystalGolem));
        }

        return List.of();

    }

    private static ActivityData<CopperGolem> initCoreActivity() {
        return ActivityData.create(
                Activity.CORE,
                0,
                ImmutableList.of(
                        new AnimalPanic<>(1.5F),
                        new LookAtTargetSink(45, 90),
                        new MoveToTargetSink(),
                        InteractWithDoor.create(),
                        new CountDownCooldownTicks(MemoryModuleType.GAZE_COOLDOWN_TICKS),
                        new CountDownCooldownTicks(MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS)
                )
        );
    }

    private static ActivityData<CopperGolem> initIdleActivity(CrystalGolem crystalGolem) {
        return ActivityData.create(
                Activity.IDLE,
                ImmutableList.of(
                        Pair.of(
                                0,
                                new TransportItemsBetweenHandlers(
                                        1.0F,
                                        // TODO: Going to have to probably be a bi-predicate
                                        sourcePredicate(crystalGolem),
                                        destinationPredicate(crystalGolem),
                                        32,
                                        8,
                                        getTargetReachedInteractions(),
                                        onTravelling(),
                                        shouldQueueForTarget()
                                )
                        ),
                        Pair.of(1, SetEntityLookTargetSometimes.create(EntityTypes.PLAYER, 6.0F, UniformInt.of(40, 80))),
                        Pair.of(
                                2,
                                new RunOne<>(
                                        ImmutableMap.of(
                                                MemoryModuleType.WALK_TARGET,
                                                MemoryStatus.VALUE_ABSENT,
                                                MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS,
                                                MemoryStatus.VALUE_PRESENT
                                        ),
                                        ImmutableList.of(Pair.of(RandomStroll.stroll(1.0F, 2, 2), 1), Pair.of(new DoNothing(30, 60), 1))
                                )
                        )
                )
        );
    }

    private static Supplier<Predicate<TransportItemsBetweenHandlers.TransportItemTarget>> sourcePredicate(CrystalGolem crystalGolem) {
        return () -> {
            Set<BlockPosDirection> sourcePositions = crystalGolem.getSourcePositions();

            if (sourcePositions.isEmpty()) {
                return DEFAULT_TRANSPORT_ITEM_SOURCE_BLOCK;
            } else {
                return transportItemTarget -> sourcePositions.contains(new BlockPosDirection(transportItemTarget.pos(), transportItemTarget.face()));
            }
        };
    }

    private static Supplier<Predicate<TransportItemsBetweenHandlers.TransportItemTarget>> destinationPredicate(CrystalGolem crystalGolem) {
        return () -> {
            Set<BlockPosDirection> destinationPositions = crystalGolem.getDestinationPositions();

            if (destinationPositions.isEmpty()) {
                return DEFAULT_TRANSPORT_ITEM_DESTINATION_BLOCK;
            } else {
                return transportItemTarget -> destinationPositions.contains(new BlockPosDirection(transportItemTarget.pos(), transportItemTarget.face()));
            }
        };
    }

    private static Map<TransportItemsBetweenContainers.ContainerInteractionState, TransportItemsBetweenHandlers.OnTargetReachedInteraction> getTargetReachedInteractions() {
        return Map.of(
                TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_ITEM,
                onReachedTargetInteraction(CopperGolemState.GETTING_ITEM, SoundEvents.COPPER_GOLEM_ITEM_GET),
                TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_NO_ITEM,
                onReachedTargetInteraction(CopperGolemState.GETTING_NO_ITEM, SoundEvents.COPPER_GOLEM_ITEM_NO_GET),
                TransportItemsBetweenContainers.ContainerInteractionState.PLACE_ITEM,
                onReachedTargetInteraction(CopperGolemState.DROPPING_ITEM, SoundEvents.COPPER_GOLEM_ITEM_DROP),
                TransportItemsBetweenContainers.ContainerInteractionState.PLACE_NO_ITEM,
                onReachedTargetInteraction(CopperGolemState.DROPPING_NO_ITEM, SoundEvents.COPPER_GOLEM_ITEM_NO_DROP)
        );
    }

    private static TransportItemsBetweenHandlers.OnTargetReachedInteraction onReachedTargetInteraction(CopperGolemState state, @Nullable SoundEvent sound) {
        return (body, target, ticksSinceReachingTarget) -> {
            if (body instanceof CrystalGolem crystalGolem) {

                @Nullable Container container = target.blockEntity() instanceof Container ? (Container) target.blockEntity() : null;

                if (ticksSinceReachingTarget == 1) {
                    if (container != null) {
                        container.startOpen(crystalGolem);
                    }

                    crystalGolem.setOpenedChestPos(target.pos());
                    crystalGolem.setState(state);
                }

                if (ticksSinceReachingTarget == 9 && sound != null) {
                    crystalGolem.playSound(sound);
                }

                if (ticksSinceReachingTarget == 60) {
                    if (container != null) {
                        if (container.getEntitiesWithContainerOpen().contains(body)) {
                            container.stopOpen(crystalGolem);
                        }
                    }

                    crystalGolem.clearOpenedChestPos();
                }
            }
        };
    }

    private static Consumer<PathfinderMob> onTravelling() {
        return body -> {
            if (body instanceof CopperGolem copperGolem) {
                copperGolem.clearOpenedChestPos();
                copperGolem.setState(CopperGolemState.IDLE);
            }
        };
    }

    // Do not use if open by another player. Don't think there is something more common to use here
    private static Predicate<TransportItemsBetweenHandlers.TransportItemTarget> shouldQueueForTarget() {
        return transportTarget -> transportTarget.blockEntity() instanceof ChestBlockEntity chestBlockEntity
                && !chestBlockEntity.getEntitiesWithContainerOpen().isEmpty();
    }
}

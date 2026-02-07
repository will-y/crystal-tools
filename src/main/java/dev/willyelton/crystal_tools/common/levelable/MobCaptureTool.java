package dev.willyelton.crystal_tools.common.levelable;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public interface MobCaptureTool {
    Logger LOGGER = LogUtils.getLogger();

    default boolean captureMob(ItemStack stack, ServerLevel level, @Nullable ServerPlayer player, LivingEntity livingEntity) {
        return captureMob(stack, level, player, livingEntity, true);
    }

    default boolean captureMob(ItemStack stack, ServerLevel level, @Nullable ServerPlayer player, LivingEntity livingEntity, boolean force) {
        if (force || canCapture(stack, level, player, livingEntity)) {
            TagValueOutput valueOutput = TagValueOutput.createWithContext(reporter(player, livingEntity), level.registryAccess());
            livingEntity.saveWithoutId(valueOutput);

            stack.set(DataComponents.ENTITY_DATA, TypedEntityData.of(livingEntity.getType(), valueOutput.buildResult()));
            stack.set(dev.willyelton.crystal_tools.common.components.DataComponents.CAPTURED_ENTITY_TOOLTIP, livingEntity.getDisplayName().getString());
            livingEntity.discard();

            return true;
        } else {
            Component cannotCapture = cannotCapture(stack, level, player, livingEntity);
            if (cannotCapture != null && player != null) {
                player.displayClientMessage(cannotCapture, true);
            }
        }

        return false;
    }

    default void releaseMob(ItemStack stack, ServerLevel level, BlockPos pos, ServerPlayer player) {
        TypedEntityData<EntityType<?>> entityData = stack.get(DataComponents.ENTITY_DATA);

        if (entityData != null) {
            Entity entity = entityData.type().create(level, EntityType.createDefaultStackConfig(level, stack, null), pos, EntitySpawnReason.MOB_SUMMONED, true, false);
            ValueInput valueInput = TagValueInput.create(reporter(player, entity), level.registryAccess(), entityData.copyTagWithoutId());

            if (entity != null) {
                entity.load(valueInput);
                entity.setPos(Vec3.atCenterOf(pos));
                level.addFreshEntity(entity);

                if (entity instanceof Mob mob) {
                    mob.playAmbientSound();
                }

                stack.remove(DataComponents.ENTITY_DATA);
                stack.remove(dev.willyelton.crystal_tools.common.components.DataComponents.CAPTURED_ENTITY_TOOLTIP);
            }
        }
    }

    boolean canCapture(ItemStack stack, ServerLevel level, @Nullable ServerPlayer player, LivingEntity livingEntity);

    Component cannotCapture(ItemStack stack, ServerLevel level, @Nullable ServerPlayer player, LivingEntity livingEntity);

    default ProblemReporter reporter(@Nullable ServerPlayer player, Entity entity) {
        if (player == null) {
            return new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER);
        }

        return new ProblemReporter.ScopedCollector(player.problemPath(), LOGGER);
    }
}

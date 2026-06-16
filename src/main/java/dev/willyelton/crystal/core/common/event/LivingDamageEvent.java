package dev.willyelton.crystal.core.common.event;

import dev.willyelton.crystal.core.Registration;
import dev.willyelton.crystal.core.common.capability.LevelableEntity;
import dev.willyelton.crystal.core.common.config.CrystalCoreConfig;
import dev.willyelton.crystal.core.common.skill.attachment.EntitySkillData;
import dev.willyelton.crystal.core.utils.EntitySkills;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class LivingDamageEvent {

    @SubscribeEvent
    public static void onLivingDamaged(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
        // Damaged enemy
        handleDamage(event.getEntity(), event.getOriginalDamage());
        if (event.getSource().getEntity() instanceof LivingEntity livingEntity) {
            // Damaging Entity
            handleDamage(livingEntity, event.getOriginalDamage());
            handleLifesteal(livingEntity, event.getOriginalDamage());
        }
    }

    private static void handleDamage(LivingEntity entity, float amount) {
        LevelableEntity levelableEntity = LevelableEntity.of(entity, entity.level().registryAccess());
        if (levelableEntity != null && levelableEntity.allowDamageXp()) {
            levelableEntity.addExp(entity.level(), entity.getOnPos(), entity, (float) (amount * CrystalCoreConfig.ENTITY_SKILL_POINT_MULTIPLIER.get()));
        }
    }

    private static void handleLifesteal(LivingEntity entity, float amount) {
        LevelableEntity levelableEntity = LevelableEntity.of(entity, entity.level().registryAccess());

        if (levelableEntity != null) {
            EntitySkillData entitySkillData = entity.getData(Registration.ENTITY_SKILL);
            float lifesteal = entitySkillData.getSkillValue(EntitySkills.LIFESTEAL);

            if (lifesteal > 0) {
                float healAmount = Mth.ceil(amount * lifesteal);
                entity.heal(healAmount);
            }
        }
    }
}

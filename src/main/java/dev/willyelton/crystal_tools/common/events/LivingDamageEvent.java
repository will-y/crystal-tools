package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.capability.LevelableEntity;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.skill.attachment.EntitySkillData;
import dev.willyelton.crystal_tools.utils.constants.EntitySkills;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID)
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
            levelableEntity.addExp(entity.level(), entity.getOnPos(), entity, (float) (amount * CrystalToolsConfig.ENTITY_SKILL_POINT_MULTIPLIER.get()));
        }
    }

    private static void handleLifesteal(LivingEntity entity, float amount) {
        LevelableEntity levelableEntity = LevelableEntity.of(entity, entity.level().registryAccess());

        if (levelableEntity != null) {
            EntitySkillData entitySkillData = entity.getData(ModRegistration.ENTITY_SKILL);
            float lifesteal = entitySkillData.getSkillValue(EntitySkills.LIFESTEAL);

            if (lifesteal > 0) {
                float healAmount = Mth.ceil(amount * lifesteal);
                entity.heal(healAmount);
            }
        }
    }
}

package dev.willyelton.crystal_tools.common.compat.jade;

import dev.willyelton.crystal_tools.common.capability.LevelableEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public class LevelableEntityComponentProvider implements IEntityComponentProvider {
    public static LevelableEntityComponentProvider INSTANCE = new LevelableEntityComponentProvider();

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        Entity entity = entityAccessor.getEntity();
        Player player = entityAccessor.getPlayer();

        if (entity instanceof LivingEntity livingEntity) {
            LevelableEntity levelableEntity = LevelableEntity.of(livingEntity, null);
            if (levelableEntity != null && levelableEntity.checkConditions(livingEntity, player)) {
                int experience = levelableEntity.getExperience();
                int cap = levelableEntity.getExperienceCap();
                int skillPoints = levelableEntity.getSkillPoints();
                if (skillPoints > 0) {
                    iTooltip.add(Component.literal("Unspent Skill Points: " + skillPoints));
                }
                iTooltip.add(Component.literal(String.format("Skill Experience %d / %d", experience, cap)));
            }
        }
    }

    @Override
    public Identifier getUid() {
        return rl("entity_skill");
    }
}

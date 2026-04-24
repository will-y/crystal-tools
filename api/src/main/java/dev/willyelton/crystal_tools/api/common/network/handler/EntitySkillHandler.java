package dev.willyelton.crystal_tools.api.common.network.handler;

import dev.willyelton.crystal_tools.api.Registration;
import dev.willyelton.crystal_tools.api.common.event.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.api.common.network.payload.EntitySkillPayload;
import dev.willyelton.crystal_tools.api.common.skill.SkillData;
import dev.willyelton.crystal_tools.api.common.skill.SkillPoints;
import dev.willyelton.crystal_tools.api.common.skill.attachment.EntitySkillData;
import dev.willyelton.crystal_tools.api.common.skill.node.EntityNode;
import dev.willyelton.crystal_tools.api.common.skill.node.SkillDataNode;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public class EntitySkillHandler {

    public static final EntitySkillHandler INSTANCE = new EntitySkillHandler();

    public void handle(final EntitySkillPayload payload, final IPayloadContext context) {
        Level level = context.player().level();

        Entity entity = level.getEntity(payload.entityId());

        if (entity instanceof LivingEntity livingEntity) {
            Optional<Registry<SkillData>> skillDataOptional = level.registryAccess().lookup(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ENTITIES);

            if (skillDataOptional.isPresent()) {
                Optional<Holder.Reference<SkillData>> dataOptional = skillDataOptional.get().get(payload.key());

                if (dataOptional.isPresent()) {
                    SkillData data = dataOptional.get().value();
                    SkillDataNode node = data.getNodeMap().get(payload.nodeId());

                    if (node instanceof EntityNode entityNode) {
                        EntitySkillData entitySkillData = livingEntity.getData(Registration.ENTITY_SKILL);
                        SkillPoints points = entitySkillData.skillPoints();
                        int skillPoints = entitySkillData.unspentPoints();
                        int toSpend = Math.min(skillPoints, payload.pointsToSpend());
                        points.addPoints(payload.nodeId(), toSpend);
                        entitySkillData.addSkillPoints(-toSpend);
                        livingEntity.syncData(Registration.ENTITY_SKILL);

                        entityNode.processNode(data, livingEntity, payload.pointsToSpend(), level.registryAccess());
                    }
                }
            }

        }
    }
}

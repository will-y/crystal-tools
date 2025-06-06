package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.node.BlockEntityNode;
import dev.willyelton.crystal_tools.common.levelable.skill.node.SkillDataNode;
import dev.willyelton.crystal_tools.common.network.data.BlockSkillPayload;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public class BlockSkillHandler {
    public static final BlockSkillHandler INSTANCE = new BlockSkillHandler();

    public void handle(final BlockSkillPayload payload, final IPayloadContext context) {
        Player player = context.player();
        Level level = player.level();

        if (level.getBlockEntity(payload.pos()) instanceof LevelableBlockEntity levelableBlockEntity) {
            Optional<Registry<SkillData>> skillDataOptional = level.registryAccess().lookup(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_BLOCKS);
            if (skillDataOptional.isPresent()) {
                Optional<Holder.Reference<SkillData>> dataOptional = skillDataOptional.get().get(payload.key());

                if (dataOptional.isPresent()) {
                    SkillData data = dataOptional.get().value();
                    SkillDataNode node = data.getNodeMap().get(payload.nodeId());

                    if (node instanceof BlockEntityNode blockEntityNode) {
                        int skillPoints = levelableBlockEntity.getSkillPoints();
                        int pointsToAdd = Math.min(skillPoints, payload.pointsToSpend());

                        levelableBlockEntity.addSkillPoints(-pointsToAdd);
                        blockEntityNode.processNode(data, levelableBlockEntity, pointsToAdd, level.registryAccess());
                    }
                }
            }

        }
    }
}

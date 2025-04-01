package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.node.SkillDataNode;
import dev.willyelton.crystal_tools.common.network.data.ToolSkillPayload;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public class ToolSkillHandler {
    public static final ToolSkillHandler INSTANCE = new ToolSkillHandler();

    public void handle(final ToolSkillPayload payload, final IPayloadContext context) {
        Player player  = context.player();

        // TODO: Backpack special logic
        ItemStack heldTool = ItemStackUtils.getHeldLevelableTool(player);

        if (!heldTool.isEmpty()) {
            Level level = player.level();
            Optional<Registry<SkillData>> skillDataOptional = level.registryAccess().lookup(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY);

            if (skillDataOptional.isPresent()) {
                Optional<Holder.Reference<SkillData>> dataOptional = skillDataOptional.get().get(payload.key());

                if (dataOptional.isPresent()) {
                    SkillData data = dataOptional.get().value();
                    SkillDataNode node = data.getNodeMap().get(payload.nodeId());
                    if (node != null) {
                        int skillPoints = heldTool.getOrDefault(DataComponents.SKILL_POINTS, 0);
                        node.processNode(heldTool, Math.min(skillPoints, payload.pointsToSpend()), level.registryAccess());
                    }
                }
            }
        }
    }
}

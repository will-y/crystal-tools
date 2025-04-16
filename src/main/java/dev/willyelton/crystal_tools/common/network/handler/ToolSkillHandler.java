package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
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

// TODO: Points handling
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
                        SkillPoints points = heldTool.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints()).copy();
                        int skillPoints = heldTool.getOrDefault(DataComponents.SKILL_POINTS, 0);
                        int toSpend = Math.min(skillPoints, payload.pointsToSpend());
                        node.processNode(data, heldTool, toSpend, level.registryAccess());
                        points.addPoints(payload.nodeId(), toSpend);
                        heldTool.set(DataComponents.SKILL_POINT_DATA, points);
                        DataComponents.addToComponent(heldTool, DataComponents.SKILL_POINTS, -toSpend);
                    }
                }
            }
        }
    }
}

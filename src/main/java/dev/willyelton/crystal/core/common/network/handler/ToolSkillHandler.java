package dev.willyelton.crystal.core.common.network.handler;

import dev.willyelton.crystal.core.common.datacomponent.DataComponents;
import dev.willyelton.crystal.core.common.event.DatapackRegistryEvents;
import dev.willyelton.crystal.core.common.levelable.LevelableTooltip;
import dev.willyelton.crystal.core.common.network.payload.ToolSkillPayload;
import dev.willyelton.crystal.core.common.skill.SkillData;
import dev.willyelton.crystal.core.common.skill.SkillPoints;
import dev.willyelton.crystal.core.common.skill.node.ItemStackNode;
import dev.willyelton.crystal.core.common.skill.node.SkillDataNode;
import dev.willyelton.crystal.core.utils.ItemStackUtils;
import dev.willyelton.crystal.core.utils.StringUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ToolSkillHandler {
    public static final ToolSkillHandler INSTANCE = new ToolSkillHandler();

    public void handle(final ToolSkillPayload payload, final IPayloadContext context) {
        Player player = context.player();

        // TODO: Backpack special logic
        ItemStack heldTool = ItemStackUtils.getHeldLevelableTool(player);

        if (!heldTool.isEmpty()) {
            Level level = player.level();
            Optional<Registry<SkillData>> skillDataOptional = level.registryAccess().lookup(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS);

            if (skillDataOptional.isPresent()) {
                Optional<Holder.Reference<SkillData>> dataOptional = skillDataOptional.get().get(payload.key());

                if (dataOptional.isPresent()) {
                    SkillData data = dataOptional.get().value();
                    SkillDataNode node = data.getNodeMap().get(payload.nodeId());
                    if (node instanceof ItemStackNode itemStackNode) {
                        SkillPoints points = heldTool.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints()).copy();
                        int skillPoints = heldTool.getOrDefault(DataComponents.SKILL_POINTS, 0);
                        int toSpend = Math.min(skillPoints, payload.pointsToSpend());
                        points.addPoints(payload.nodeId(), toSpend);
                        heldTool.set(DataComponents.SKILL_POINT_DATA, points);
                        DataComponents.addToComponent(heldTool, DataComponents.SKILL_POINTS, -toSpend);
                        editSkillTooltips(heldTool, node.getName(), toSpend);
                        itemStackNode.processNode(data, heldTool, toSpend, level.registryAccess());
                    }
                }
            }
        }
    }

    // This probably shouldn't be here but idk where to put it
    // TODO: probably needs to move into the node because I should do value
    // instead of points
    private void editSkillTooltips(ItemStack stack, String name, int points) {
        LevelableTooltip tooltip = stack.getOrDefault(DataComponents.SKILL_TOOLTIP, new LevelableTooltip(new HashMap<>()));
        Map<String, Float> skills = new HashMap<>(tooltip.skills());

        skills.compute(StringUtils.stripRomanNumeral(name), (key, val) -> val != null ? val + points : points);

        stack.set(DataComponents.SKILL_TOOLTIP, new LevelableTooltip(skills));
    }
}

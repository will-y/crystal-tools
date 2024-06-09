package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillTreeRegistry;
import dev.willyelton.crystal_tools.common.network.data.SkillCacheUpdatePayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.Level;

public class SkillCacheUpdateHandler {
    public static SkillCacheUpdateHandler INSTANCE = new SkillCacheUpdateHandler();

    public void handle(final SkillCacheUpdatePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            CrystalTools.LOGGER.log(Level.TRACE, "Adding tool to cache: " + payload.tool());
            SkillTreeRegistry.SKILL_TREES.put(payload.tool(), payload.data());
        });
    }
}

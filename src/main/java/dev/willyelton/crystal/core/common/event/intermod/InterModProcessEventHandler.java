package dev.willyelton.crystal.core.common.event.intermod;

import dev.willyelton.crystal.core.CrystalCore;
import dev.willyelton.crystal.core.common.registry.SkillTreeBlackListRegistry;
import dev.willyelton.crystal.core.common.levelable.condition.LevelableCondition;
import dev.willyelton.crystal.core.common.levelable.condition.LevelableConditions;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;

import java.util.stream.Stream;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class InterModProcessEventHandler {

    @SubscribeEvent
    public static void handleInterModComms(InterModProcessEvent event) {
        Stream<InterModComms.IMCMessage> conditionStream = InterModComms.getMessages(ApiConstants.CORE_MOD_ID, s -> s.equals(InterModConstants.CREATE_CONDITION));
        handleLevelableConditions(conditionStream);

        Stream<InterModComms.IMCMessage> skillBlacklist = InterModComms.getMessages(ApiConstants.CORE_MOD_ID, s -> s.equals(InterModConstants.REGISTER_SKILL_BLACKLIST));
        handleSkillTreeBlacklists(skillBlacklist);
    }

    private static void handleLevelableConditions(Stream<InterModComms.IMCMessage> stream) {
        stream.forEach(message -> {
            CrystalCore.LOGGER.debug("Processing levelable condition for {}", message.senderModId());
            CrystalCore.LOGGER.trace(message);

            Object condition = message.messageSupplier().get();

            if (condition instanceof LevelableCondition levelableCondition) {
                LevelableConditions.addCondition(levelableCondition.id(), levelableCondition);
            } else {
                CrystalCore.LOGGER.warn("Unable to process levelable condition for {}. Object was not a LevelableCondition", message.senderModId());
            }
        });
    }

    private static void handleSkillTreeBlacklists(Stream<InterModComms.IMCMessage> stream) {
        stream.forEach(message -> {
            CrystalCore.LOGGER.debug("Processing skill tree blacklist for {}", message.senderModId());
            CrystalCore.LOGGER.trace(message);

            Object condition = message.messageSupplier().get();
            if (condition instanceof SkillTreeBlackListRegistry.SkillTreeBlackListPredicate predicate) {
                SkillTreeBlackListRegistry.addSkillTreeBlackListRule(predicate);
            }
        });
    }
}

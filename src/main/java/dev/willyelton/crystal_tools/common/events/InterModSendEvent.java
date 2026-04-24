package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.api.common.event.intermod.InterModConstants;
import dev.willyelton.crystal_tools.api.common.event.intermod.registries.SkillTreeBlackListRegistry;
import dev.willyelton.crystal_tools.api.utils.constants.ApiConstants;
import dev.willyelton.crystal_tools.common.config.CrystalToolsServerConfig;
import dev.willyelton.crystal_tools.common.config.VanillaSkillTreeLevel;
import dev.willyelton.crystal_tools.common.levelable.condition.WolfCondition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class InterModSendEvent {
    @SubscribeEvent
    public static void enqueueInterModComms(InterModEnqueueEvent event) {
        InterModComms.sendTo(CrystalTools.MODID, ApiConstants.CORE_MOD_ID, InterModConstants.CREATE_CONDITION, WolfCondition::new);

        InterModComms.sendTo(CrystalTools.MODID, ApiConstants.CORE_MOD_ID, InterModConstants.REGISTER_SKILL_BLACKLIST, () -> (SkillTreeBlackListRegistry.SkillTreeBlackListPredicate) (stack -> {
            ResourceKey<Item> key = stack.typeHolder().getKey();
            if (key == null) return false;
            Identifier location = key.identifier();

            if ("minecraft".equalsIgnoreCase(location.getNamespace())) {
                return switch (CrystalToolsServerConfig.VANILLA_SKILL_TREES.get()) {
                    case VanillaSkillTreeLevel.ALL -> true;
                    case VanillaSkillTreeLevel.NONE -> false;
                    case VanillaSkillTreeLevel.NETHERITE -> location.getPath().contains("netherite");
                    case VanillaSkillTreeLevel.DIAMOND_NETHERITE -> location.getPath().contains("netherite") || location.getPath().contains("diamond");
                };
            } else {
                return false;
            }
        }));
    }
}

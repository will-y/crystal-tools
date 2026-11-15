package dev.willyelton.crystal.core.common.event;

import dev.willyelton.crystal.core.common.skill.SkillData;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewDatapackRegistryEvent ;

import static dev.willyelton.crystal.core.utils.constants.ApiConstants.baseRl;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class DatapackRegistryEvents {
    public static ResourceKey<Registry<SkillData>> SKILL_DATA_REGISTRY_KEY_ITEMS = ResourceKey.createRegistryKey(baseRl("skill_trees/items"));
    public static ResourceKey<Registry<SkillData>> SKILL_DATA_REGISTRY_KEY_BLOCKS = ResourceKey.createRegistryKey(baseRl("skill_trees/blocks"));
    public static ResourceKey<Registry<SkillData>> SKILL_DATA_REGISTRY_KEY_ENTITIES = ResourceKey.createRegistryKey(baseRl("skill_trees/entities"));

    @SubscribeEvent
    public static void registerDatapackRegistry(NewDatapackRegistryEvent event) {
        event.reloadableRegistry(SKILL_DATA_REGISTRY_KEY_ITEMS, SkillData.CODEC);
        event.reloadableRegistry(SKILL_DATA_REGISTRY_KEY_BLOCKS, SkillData.CODEC);
        event.reloadableRegistry(SKILL_DATA_REGISTRY_KEY_ENTITIES, SkillData.CODEC);
    }
}

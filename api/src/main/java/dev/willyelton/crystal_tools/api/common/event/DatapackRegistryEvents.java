package dev.willyelton.crystal_tools.api.common.event;

import dev.willyelton.crystal_tools.api.common.skill.SkillData;
import dev.willyelton.crystal_tools.api.utils.constants.ApiConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import static dev.willyelton.crystal_tools.api.utils.constants.ApiConstants.baseRl;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class DatapackRegistryEvents {
    public static ResourceKey<Registry<SkillData>> SKILL_DATA_REGISTRY_KEY_ITEMS = ResourceKey.createRegistryKey(baseRl("skill_trees/items"));
    public static ResourceKey<Registry<SkillData>> SKILL_DATA_REGISTRY_KEY_BLOCKS = ResourceKey.createRegistryKey(baseRl("skill_trees/blocks"));
    public static ResourceKey<Registry<SkillData>> SKILL_DATA_REGISTRY_KEY_ENTITIES = ResourceKey.createRegistryKey(baseRl("skill_trees/entities"));

    @SubscribeEvent
    public static void registerDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(SKILL_DATA_REGISTRY_KEY_ITEMS, SkillData.CODEC, SkillData.CODEC);
        event.dataPackRegistry(SKILL_DATA_REGISTRY_KEY_BLOCKS, SkillData.CODEC, SkillData.CODEC);
        event.dataPackRegistry(SKILL_DATA_REGISTRY_KEY_ENTITIES, SkillData.CODEC, SkillData.CODEC);
    }
}

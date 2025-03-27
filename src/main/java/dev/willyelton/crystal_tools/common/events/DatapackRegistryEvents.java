package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = CrystalTools.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DatapackRegistryEvents {
    public static ResourceKey<Registry<SkillData>> SKILL_DATA_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "skill_trees"));

    @SubscribeEvent
    public static void registerDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(SKILL_DATA_REGISTRY_KEY, SkillData.CODEC, SkillData.CODEC);
    }
}

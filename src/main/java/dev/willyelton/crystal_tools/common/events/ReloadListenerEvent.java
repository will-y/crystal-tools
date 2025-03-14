package dev.willyelton.crystal_tools.common.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.network.data.SkillCacheUpdatePayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class ReloadListenerEvent {
    private static final Gson gson = new GsonBuilder().create();
    // TODO: This is really annoying
    private static final List<String> SKILL_TREES = List.of("aiot", "apple", "axe", "boots", "bow", "chestplate",
            "crystal_elytra", "crystal_furnace", "crystal_rocket", "helmet", "hoe", "leggings", "pickaxe", "shovel",
            "sword", "backpack", "trident", "fishing_rod", "generator", "quarry", "shield");

    @SubscribeEvent
    public static void handleReloadListener(AddServerReloadListenersEvent reloadListenerEvent) {
        reloadListenerEvent.addListener(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "skill_trees"), new ReloadListener());
    }

    private static Map<String, SkillData> skillDataMap;

    @SubscribeEvent
    public static void handleDataPackSync(OnDatapackSyncEvent onDatapackSyncEvent) {
        for (Map.Entry<String, SkillData> entry : skillDataMap.entrySet()) {
            if (onDatapackSyncEvent.getPlayer() != null) {
                CrystalTools.LOGGER.log(Level.TRACE, "Syncing Tool {} with player {}", entry.getKey(), onDatapackSyncEvent.getPlayer().getDisplayName().getString());
                PacketDistributor.sendToPlayer(onDatapackSyncEvent.getPlayer(), new SkillCacheUpdatePayload(entry.getKey(), entry.getValue()));
            } else {
                CrystalTools.LOGGER.log(Level.TRACE, "Syncing Tool {} with all players", entry.getKey());
                PacketDistributor.sendToAllPlayers(new SkillCacheUpdatePayload(entry.getKey(), entry.getValue()));
            }
        }
    }

    private static final class ReloadListener extends SimplePreparableReloadListener<Map<String, SkillData>> {

        @Override
        protected @NotNull Map<String, SkillData> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
            CrystalTools.LOGGER.log(Level.TRACE, "Reloading Crystal Tools Skill Trees");
            Map<String, SkillData> result = new HashMap<>();

            for (String tool : SKILL_TREES) {
                SkillData skillTree = deserializeSkillData(tool, resourceManager);
                if (skillTree != null) {
                    result.put(tool, skillTree);
                }
            }

            return result;
        }

        @Override
        protected void apply(@NotNull Map<String, SkillData> skillDataMap, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
            CrystalTools.LOGGER.log(Level.TRACE, "Populating Crystal Tools Skill Data Cache");
            ReloadListenerEvent.skillDataMap = skillDataMap;
        }

        private SkillData deserializeSkillData(String tool, ResourceManager resourceManager) {
            try {
                Optional<Resource> resourceOptional = resourceManager.getResource(ResourceLocation.fromNamespaceAndPath("crystal_tools", "skill_trees/" + tool + ".json"));

                if (resourceOptional.isPresent()) {
                    Resource resource = resourceOptional.get();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open()));

                    DataResult<SkillData> result =  SkillData.CODEC.parse(JsonOps.INSTANCE, gson.fromJson(reader, JsonElement.class));

                    if (result.result().isPresent()) {
                        return result.result().get();
                    }

                }
            } catch (IOException e) {
                CrystalTools.LOGGER.log(Level.WARN, "Failed to Load Crystal Tools Skill Tree {}", tool, e);
            }

            CrystalTools.LOGGER.log(Level.WARN, "Failed to Load Crystal Tools Skill Tree {}", tool);
            return null;
        }
    }
}

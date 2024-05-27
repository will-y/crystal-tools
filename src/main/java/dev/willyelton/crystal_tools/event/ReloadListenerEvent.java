package dev.willyelton.crystal_tools.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.packet.SkillCacheUpdatePacket;
import dev.willyelton.crystal_tools.renderer.CrystalTridentBlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID)
public class ReloadListenerEvent {

    private static final Gson gson = new GsonBuilder().create();
    private static final List<String> SKILL_TREES = List.of("aiot", "apple", "axe", "boots", "bow", "chestplate",
            "crystal_elytra", "crystal_furnace", "crystal_rocket", "helmet", "hoe", "leggings", "pickaxe", "shovel",
            "sword", "backpack", "trident", "fishing_rod");
    @SubscribeEvent
    public static void handleReloadListener(AddReloadListenerEvent reloadListenerEvent) {
        reloadListenerEvent.addListener(new ReloadListener());
        reloadListenerEvent.addListener(CrystalTridentBlockEntityWithoutLevelRenderer.INSTANCE);
    }

    private static Map<String, SkillData> skillDataMap;

    @SubscribeEvent
    public static void handleDataPackSync(OnDatapackSyncEvent onDatapackSyncEvent) {
        for (Map.Entry<String, SkillData> entry : skillDataMap.entrySet()) {

            if (onDatapackSyncEvent.getPlayer() != null) {
                CrystalTools.LOGGER.log(Level.TRACE, "Syncing Tool " + entry.getKey() + " with player " + onDatapackSyncEvent.getPlayer().getDisplayName().getString());
                PacketHandler.sendToPlayer(new SkillCacheUpdatePacket(entry.getKey(), entry.getValue()), onDatapackSyncEvent.getPlayer());
            } else {
                CrystalTools.LOGGER.log(Level.TRACE, "Syncing Tool" + entry.getKey() + " with all players");
                PacketHandler.sendToAllPlayers(new SkillCacheUpdatePacket(entry.getKey(), entry.getValue()));
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
                Optional<Resource> resourceOptional = resourceManager.getResource(new ResourceLocation("crystal_tools", "skill_trees/" + tool + ".json"));

                if (resourceOptional.isPresent()) {
                    Resource resource = resourceOptional.get();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open()));

                    DataResult<SkillData> result =  SkillData.CODEC.parse(JsonOps.INSTANCE, gson.fromJson(reader, JsonElement.class));

                    if (result.result().isPresent()) {
                        return result.result().get();
                    }

                }
            } catch (IOException e) {
                CrystalTools.LOGGER.log(Level.WARN, "Failed to Load Crystal Tools Skill Tree " + tool, e);
            }

            CrystalTools.LOGGER.log(Level.WARN, "Failed to Load Crystal Tools Skill Tree " + tool);
            return null;
        }
    }
}

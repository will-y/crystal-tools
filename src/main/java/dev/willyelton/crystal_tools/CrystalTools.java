package dev.willyelton.crystal_tools;

import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.config.CrystalToolsServerConfig;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CrystalTools.MODID)
public class CrystalTools {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "crystal_tools";

    public CrystalTools(IEventBus modEventBus, ModContainer container) {
        // Register everything
        ModRegistration.init(modEventBus);

        // Register configs
        container.registerConfig(ModConfig.Type.COMMON, CrystalToolsConfig.COMMON_CONFIG, "crystal_tools.toml");
        container.registerConfig(ModConfig.Type.SERVER, CrystalToolsServerConfig.SERVER_CONFIG, "crystal_tools-server.toml");

        SkillDataRequirements.bootstrap();
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static <T> ContextKey<T> ck(String path) {
        return new ContextKey<>(rl(path));
    }
}

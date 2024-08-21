package dev.willyelton.crystal_tools;

import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.datagen.DataGeneration;
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
        modEventBus.addListener(DataGeneration::generate);

        // Register everything
        Registration.init(modEventBus);

        // Register configs
        container.registerConfig(ModConfig.Type.COMMON, CrystalToolsConfig.GENERAL_SPEC, "crystal_tools.toml");
    }
}

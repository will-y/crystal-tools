package dev.willyelton.crystal_tools;

import dev.willyelton.crystal_tools.common.command.RegisterCommandEvent;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.crafting.ItemDisabledCondition;
import dev.willyelton.crystal_tools.common.crafting.ModRecipes;
import dev.willyelton.crystal_tools.common.datagen.DataGeneration;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CrystalTools.MODID)
public class CrystalTools {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "crystal_tools";

    public CrystalTools(IEventBus modEventBus, ModContainer container) {

        // Client Register Things
        modEventBus.addListener(this::clientSetup);
        // TODO: Move to registration
        Registry.register(NeoForgeRegistries.CONDITION_SERIALIZERS, ItemDisabledCondition.NAME, ItemDisabledCondition.ITEM_DISABLED_CODEC);
        modEventBus.addListener(DataGeneration::generate);

        // Register ourselves for server and other game events we are interested in
//        NeoForge.EVENT_BUS.register(this);

        NeoForge.EVENT_BUS.register(RegisterCommandEvent.class);

        // Register everything
        Registration.init(modEventBus);

        // Register configs
        container.registerConfig(ModConfig.Type.COMMON, CrystalToolsConfig.GENERAL_SPEC, "crystal_tools.toml");

        // Register Custom Recipes
        ModRecipes.initRecipes(modEventBus);
    }

    private void clientSetup(final FMLClientSetupEvent event) {

    }
}

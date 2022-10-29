package dev.willyelton.crystal_tools;

import dev.willyelton.crystal_tools.block.ModBlocks;
import dev.willyelton.crystal_tools.command.RegisterCommandEvent;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.crafting.ItemDisabledCondition;
import dev.willyelton.crystal_tools.crafting.ModRecipes;
import dev.willyelton.crystal_tools.item.ModItems;
import dev.willyelton.crystal_tools.item.armor.ModArmor;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.item.tool.ModTools;
import dev.willyelton.crystal_tools.world.ModOres;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CrystalTools.MODID)
public class CrystalTools {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "crystal_tools";
    public static final ItemDisabledCondition.Serializer INSTANCE = new ItemDisabledCondition.Serializer();

    public CrystalTools() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the setup method for modloading
        eventBus.addListener(this::init);
        // Client Register Things
        eventBus.addListener(this::clientSetup);

        setup();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.register(RegisterCommandEvent.class);

        // Register configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CrystalToolsConfig.GENERAL_SPEC, "crystal_tools.toml");

        // Register Blocks
        ModBlocks.initBlocks();

        // Register Items
        ModItems.initItems();

        // Register Tools
        ModTools.initTools();

        // Register Items
        ModArmor.initArmor();

        // Register Ores (Placed Features)
        ModOres.initOres();

        // Register Custom Recipes
        ModRecipes.initRecipes();

        // Register Message Handlers
        PacketHandler.register();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
    }

    private void init(final FMLCommonSetupEvent event) {
        // some preinit code
        event.enqueueWork(() -> {
            CraftingHelper.register(INSTANCE);
        });
    }

    private void setup() {

    }
}

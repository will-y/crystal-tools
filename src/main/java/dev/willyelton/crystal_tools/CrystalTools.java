package dev.willyelton.crystal_tools;

import dev.willyelton.crystal_tools.block.ModBlocks;
import dev.willyelton.crystal_tools.command.RegisterCommandEvent;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.item.ModItems;
import dev.willyelton.crystal_tools.item.armor.ModArmor;
import dev.willyelton.crystal_tools.keybinding.KeyBindings;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.item.tool.ModTools;
import dev.willyelton.crystal_tools.world.ModOres;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("crystal_tools")
public class CrystalTools {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "crystal_tools";

    public CrystalTools() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the setup method for modloading
        eventBus.addListener(this::init);
        // Register the enqueueIMC method for modloading
        eventBus.addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        eventBus.addListener(this::processIMC);
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

        // Register Message Handlers
        PacketHandler.register();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        KeyBindings.init();
    }

    private void init(final FMLCommonSetupEvent event) {
        // some preinit code
        event.enqueueWork(ModOres::registerConfiguredFeatures);
    }

    private void setup() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(ModOres::onBiomeLoadingEvent);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
//        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
//        LOGGER.info("Got IMC {}", event.getIMCStream().
//                map(m->m.messageSupplier().get()).
//                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // do something when the server starts
//        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
//            LOGGER.info("HELLO from Register Block");
        }
    }
}

package dev.willyelton.crystal_tools.tool;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.item.CreativeTabs;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModTools {

    private static final DeferredRegister<Item> TOOLS = DeferredRegister.create(ForgeRegistries.ITEMS, CrystalTools.MODID);

    public static final RegistryObject<Item> CRYSTAL_PICKAXE = TOOLS.register("crystal_pickaxe", () -> new LevelableTool(new Item.Properties().fireResistant().tab(CreativeTabs.CRYSTAL_TOOLS_TAB), BlockTags.MINEABLE_WITH_PICKAXE, "pickaxe"));
    public static final RegistryObject<Item> CRYSTAL_AXE = TOOLS.register("crystal_axe", () -> new LevelableTool(new Item.Properties().fireResistant().tab(CreativeTabs.CRYSTAL_TOOLS_TAB), BlockTags.MINEABLE_WITH_AXE, "axe"));
    public static final RegistryObject<Item> CRYSTAL_SHOVEL = TOOLS.register("crystal_shovel", () -> new LevelableTool(new Item.Properties().fireResistant().tab(CreativeTabs.CRYSTAL_TOOLS_TAB), BlockTags.MINEABLE_WITH_SHOVEL, "shovel"));
    public static final RegistryObject<Item> CRYSTAL_HOE = TOOLS.register("crystal_hoe", () -> new LevelableTool(new Item.Properties().fireResistant().tab(CreativeTabs.CRYSTAL_TOOLS_TAB), BlockTags.MINEABLE_WITH_HOE, "hoe"));


    public static void initTools() {
        TOOLS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}

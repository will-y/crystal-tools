package dev.willyelton.crystal_tools.tool;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModTools {

    private static final DeferredRegister<Item> TOOLS = DeferredRegister.create(ForgeRegistries.ITEMS, CrystalTools.MODID);

    public static final RegistryObject<Item> CRYSTAL_PICKAXE = TOOLS.register("crystal_pickaxe", () -> new LevelableTool(new Item.Properties(), BlockTags.MINEABLE_WITH_PICKAXE));


    public static void initTools() {
        TOOLS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
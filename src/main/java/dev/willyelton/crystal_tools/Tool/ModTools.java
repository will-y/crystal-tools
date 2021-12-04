package dev.willyelton.crystal_tools.Tool;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTools {

    private static final DeferredRegister<Item> TOOLS = DeferredRegister.create(ForgeRegistries.ITEMS, CrystalTools.MODID);

    public static void initTools() {
        TOOLS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}

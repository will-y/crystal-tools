package dev.willyelton.crystal_tools.item;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrystalTools.MODID);

    // Items
    public static final RegistryObject<Item> CRYSTAL = ITEMS.register("crystal", () -> new Item(new Item.Properties().tab(CreativeTabs.CRYSTAL_TOOLS_TAB)));
    public static final RegistryObject<Item> NETHERITE_STICK = ITEMS.register("netherite_stick", () -> new Item(new Item.Properties().fireResistant().tab(CreativeTabs.CRYSTAL_TOOLS_TAB)));

    public static void initItems() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}

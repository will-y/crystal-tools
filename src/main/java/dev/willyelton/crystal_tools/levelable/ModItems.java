package dev.willyelton.crystal_tools.levelable;

import dev.willyelton.crystal_tools.CreativeTabs;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.levelable.tool.CrystalApple;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrystalTools.MODID);

    // Items
    public static final RegistryObject<Item> CRYSTAL = ITEMS.register("crystal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NETHERITE_STICK = ITEMS.register("netherite_stick", () -> new Item(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> CRYSTAL_APPLE = ITEMS.register("crystal_apple", CrystalApple::new);

    public static void initItems() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}

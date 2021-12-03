package dev.willyelton.crystal_tools.Item;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrystalTools.MODID);

    // Items
    public static final RegistryObject<Item> CRYSTAL = ITEMS.register("crystal", () -> new Item(new Item.Properties()));

    public static void initItems() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}

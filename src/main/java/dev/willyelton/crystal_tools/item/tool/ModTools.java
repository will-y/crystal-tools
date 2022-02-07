package dev.willyelton.crystal_tools.item.tool;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModTools {

    private static final DeferredRegister<Item> TOOLS = DeferredRegister.create(ForgeRegistries.ITEMS, CrystalTools.MODID);

    public static final RegistryObject<Item> CRYSTAL_PICKAXE = TOOLS.register("crystal_pickaxe", PickaxeLevelableTool::new);
    public static final RegistryObject<Item> CRYSTAL_AXE = TOOLS.register("crystal_axe", AxeLevelableTool::new);
    public static final RegistryObject<Item> CRYSTAL_SHOVEL = TOOLS.register("crystal_shovel", ShovelLevelableTool::new);
    public static final RegistryObject<Item> CRYSTAL_HOE = TOOLS.register("crystal_hoe", HoeLevelableTool::new);
    public static final RegistryObject<Item> CRYSTAL_SWORD =  TOOLS.register("crystal_sword", SwordLevelableTool::new);
    public static final RegistryObject<Item> CRYSTAL_BOW = TOOLS.register("crystal_bow", BowLevelableItem::new);
    public static final RegistryObject<Item> CRYSTAL_AIOT = TOOLS.register("crystal_aiot", AIOLevelableTool::new);

    public static void initTools() {
        TOOLS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}

package dev.willyelton.crystal_tools.item.armor;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModArmor {

    private static final DeferredRegister<Item> ARMOR = DeferredRegister.create(ForgeRegistries.ITEMS, CrystalTools.MODID);

    public static final RegistryObject<Item> CRYSTAL_HELMET = ARMOR.register("crystal_helmet", () -> new LevelableArmor("helmet", EquipmentSlot.HEAD));
    public static final RegistryObject<Item> CRYSTAL_CHESTPLATE = ARMOR.register("crystal_chestplate", () -> new LevelableArmor("chestplate", EquipmentSlot.CHEST));
    public static final RegistryObject<Item> CRYSTAL_LEGGINGS = ARMOR.register("crystal_leggings", () -> new LevelableArmor("leggings", EquipmentSlot.LEGS));
    public static final RegistryObject<Item> CRYSTAL_BOOTS = ARMOR.register("crystal_boots", () -> new LevelableArmor("boots", EquipmentSlot.FEET));

    public static void initArmor() {
        ARMOR.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}

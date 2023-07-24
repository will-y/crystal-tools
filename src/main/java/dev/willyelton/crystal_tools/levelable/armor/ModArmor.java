package dev.willyelton.crystal_tools.levelable.armor;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModArmor {

    private static final DeferredRegister<Item> ARMOR = DeferredRegister.create(ForgeRegistries.ITEMS, CrystalTools.MODID);

    public static final RegistryObject<Item> CRYSTAL_HELMET = ARMOR.register("crystal_helmet", () -> new LevelableArmor("helmet", ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> CRYSTAL_CHESTPLATE = ARMOR.register("crystal_chestplate", () -> new LevelableArmor("chestplate", ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> CRYSTAL_LEGGINGS = ARMOR.register("crystal_leggings", () -> new LevelableArmor("leggings",  ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> CRYSTAL_BOOTS = ARMOR.register("crystal_boots", () -> new LevelableArmor("boots",  ArmorItem.Type.BOOTS));

    public static final RegistryObject<Item> CRYSTAL_ELYTRA = ARMOR.register("crystal_elytra", () -> new CrystalElytra(new Item.Properties().durability(1000)));

    public static void initArmor() {
        ARMOR.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}

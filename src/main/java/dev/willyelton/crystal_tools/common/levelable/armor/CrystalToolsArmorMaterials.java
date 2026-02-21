package dev.willyelton.crystal_tools.common.levelable.armor;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import net.minecraft.util.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.EnumMap;

import static net.minecraft.world.item.equipment.EquipmentAssets.ROOT_ID;

public class CrystalToolsArmorMaterials {
    public static final ResourceKey<EquipmentAsset> CRYSTAL_ELYTRA = ResourceKey.create(ROOT_ID, Identifier.fromNamespaceAndPath(CrystalTools.MODID, "crystal_elytra"));
    public static final ResourceKey<EquipmentAsset> CRYSTAL_EQUIPMENT_ASSET = ResourceKey.create(ROOT_ID, Identifier.fromNamespaceAndPath(CrystalTools.MODID, "crystal"));

    public static final ArmorMaterial CRYSTAL = new ArmorMaterial(37, Util.make(new EnumMap<>(ArmorType.class), consumer -> {
        consumer.put(ArmorType.BOOTS, 3);
        consumer.put(ArmorType.LEGGINGS, 6);
        consumer.put(ArmorType.CHESTPLATE, 8);
        consumer.put(ArmorType.HELMET, 3);
        consumer.put(ArmorType.BODY, 11);
    }), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F,
            CrystalToolsTags.REPAIRS_CRYSTAL, CRYSTAL_EQUIPMENT_ASSET);
}

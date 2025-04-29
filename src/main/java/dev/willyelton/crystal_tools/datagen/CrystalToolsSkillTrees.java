package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.constants.SkillTreeDescriptions;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.List;

import static dev.willyelton.crystal_tools.utils.StringUtils.formatKey;
import static dev.willyelton.crystal_tools.utils.constants.SkillConstants.DURABILITY;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.AUTO_PICKUP;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.AUTO_REPAIR;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.AUTO_SMELTING;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.AUTO_TARGET;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.AXE_VEIN_MINING;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.CREATIVE_FLIGHT;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.LEAF_MINER;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.MINING_3x3;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.MODE_SWITCH;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.NIGHT_VISION;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.SILK_TOUCH;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.TORCH;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.TORCH_SUBTEXT;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.VEIN_MINING;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.VEIN_MINING_SUBTEXT;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.arrowDamage;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.arrowSpeed;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.baseArmor;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.blastProtection;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.drawSpeed;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.durability;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.eatSpeed;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.fireProtection;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.flightDuration;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.fortune;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.healthBonus;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.intToRomanNumeral;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.miningSpeed;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.moveSpeed;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.projectileProtection;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.protection;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.reach;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.toughness;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.unbreaking;

public class CrystalToolsSkillTrees {
    private final BootstrapContext<SkillData> context;

    public static void register(BootstrapContext<SkillData> context) {
        CrystalToolsSkillTrees skillTrees = new CrystalToolsSkillTrees(context);
        skillTrees.registerSkillTrees();
    }

    public CrystalToolsSkillTrees(BootstrapContext<SkillData> context) {
        this.context = context;
    }

    public void registerSkillTrees() {
        // Basic Mining Tools
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                Registration.CRYSTAL_PICKAXE.getId()), basicMiningTool("Pickaxe"));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                Registration.CRYSTAL_HOE.getId()), basicMiningTool("Hoe"));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                Registration.CRYSTAL_SHOVEL.getId()), basicMiningTool("Shovel"));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                Registration.CRYSTAL_AXE.getId()), basicMiningTool("Axe"));

        // Weapons
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                Registration.CRYSTAL_BOW.getId()), bow());

        // Food
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                Registration.CRYSTAL_APPLE.getId()), food("Apple"));

        // Armor
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                Registration.CRYSTAL_HELMET.getId()), helmet());
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                Registration.CRYSTAL_CHESTPLATE.getId()), chestplate(false));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                Registration.CRYSTAL_LEGGINGS.getId()), leggings());
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                Registration.CRYSTAL_BOOTS.getId()), boots());
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                Registration.CRYSTAL_ELYTRA.getId()), chestplate(true));

        // Misc
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                Registration.CRYSTAL_ROCKET.getId()), rocket());
    }

    private SkillData basicMiningTool(String name) {
        SkillTreeDescriptions desc = new SkillTreeDescriptions(name);

        boolean axe = name.equals("Axe");

        return SkillData.builder(EquipmentSlot.MAINHAND)
                .tier()
                    .attributeNode(0, miningSpeed(1), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6F)
                    .dataComponentNode(1, durability(1), desc.durability(), DURABILITY, 200)
                .tier()
                    .attributeNode(2, miningSpeed(2), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6F)
                        .nodeRequirement(0)
                    .dataComponentNode(3, durability(2), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(1)
                    .dataComponentNode(4, unbreaking(1), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(5, miningSpeed(3), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6F)
                        .nodeRequirement(2)
                    .dataComponentNode(6, durability(3), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(3)
                    .attributeNode(7, reach(1), desc.reach(), List.of(attr(Attributes.BLOCK_INTERACTION_RANGE), attr(Attributes.ENTITY_INTERACTION_RANGE)), 1)
                        .previousTierOrRequirements()
                .tier()
                    .enchantmentNode(8, SILK_TOUCH, desc.silkTouch(), Enchantments.SILK_TOUCH, 1)
                        .previousTierOrRequirements()
                        .notNodeRequirement(9, 22)
                    .enchantmentNode(9, fortune(3), desc.fortune(), Enchantments.FORTUNE, 3)
                        .previousTierOrRequirements()
                        .notNodeRequirement(8, 22)
                    .dataComponentNode(10, unbreaking(2), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .previousTierOrRequirements()
                        .nodeRequirement(4)
                    .optional(axe)
                        .dataComponentNode(30, LEAF_MINER, desc.leafMiner(), DataComponents.LEAF_MINE.getId(), 1)
                            .previousTierOrRequirements()
                    .endOptional()
                .tier()
                    .attributeNode(11, miningSpeed(4), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6F)
                        .nodeRequirement(5)
                        .previousTierOrRequirements()
                    .dataComponentNode(12, durability(4), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(6)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(13, miningSpeed(5), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6F)
                        .nodeRequirement(11)
                    .dataComponentNode(14, durability(5), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(12)
                    .attributeNode(15, reach(2), desc.reach(), List.of(attr(Attributes.BLOCK_INTERACTION_RANGE), attr(Attributes.ENTITY_INTERACTION_RANGE)), 1)
                        .nodeRequirement(7)
                        .previousTierOrRequirements()
                    .dataComponentNode(16, AUTO_PICKUP, desc.autoPickup(), DataComponents.AUTO_PICKUP.getId(), 1)
                        .previousTierOrRequirements()
                .tier()
                    .infiniteDataComponentNode(17, AUTO_REPAIR, desc.autoRepair(), DataComponents.AUTO_REPAIR.getId(), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(18, MINING_3x3, desc.mining3x3(), DataComponents.HAS_3x3.getId(), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(19, !axe ? VEIN_MINING : AXE_VEIN_MINING, desc.veinMining(axe), DataComponents.VEIN_MINER.getId(), 1)
                        .previousTierOrRequirements()
                        .subText(VEIN_MINING_SUBTEXT, "#ABABAB")
                    .dataComponentNode(20, AUTO_SMELTING, desc.autoSmelting(), DataComponents.AUTO_SMELT.getId(), 1)
                        .previousTierOrRequirements()
                .tier()
                    .optional(name.equals("Pickaxe"))
                        .dataComponentNode(21, TORCH, desc.torch(), DataComponents.TORCH.getId(), 1)
                            .previousTierOrRequirements()
                            .subText(TORCH_SUBTEXT, "#ABABAB")
                    .endOptional()
                    .dataComponentNode(22, MODE_SWITCH, desc.mineMode(), DataComponents.MINE_MODE.getId(), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(23, unbreaking(3), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .previousTierOrRequirements()
                        .nodeRequirement(10)
                    .attributeNode(24, reach(3), desc.reach(), List.of(attr(Attributes.BLOCK_INTERACTION_RANGE), attr(Attributes.ENTITY_INTERACTION_RANGE)), 1)
                        .nodeRequirement(15)
                        .previousTierOrRequirements()
                .tier()
                    .infiniteAttributeNode(25, miningSpeed(0), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 0.2F)
                        .nodeRequirement(13)
                        .previousTierOrRequirements()
                    .infiniteDataComponentNode(26, durability(0), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(14)
                        .previousTierOrRequirements()
                    .dataComponentNode(27, unbreaking(0), desc.unbreaking(), DURABILITY, 50, 70)
                        .nodeRequirement(23)
                        .previousTierOrRequirements()
                    .infiniteAttributeNode(28, reach(0), desc.reach(), List.of(attr(Attributes.BLOCK_INTERACTION_RANGE), attr(Attributes.ENTITY_INTERACTION_RANGE)), 0.1F)
                        .nodeRequirement(24)
                        .previousTierOrRequirements()
                .tier()
                    .enchantmentNode(29, fortune(5), desc.fortune(), Enchantments.FORTUNE, 5)
                        .nodeRequirement(9, 25, 26, 27, 28)
                .build();
    }

    private SkillData food(String name) {
        SkillTreeDescriptions desc = new SkillTreeDescriptions(name);

        return SkillData.builder(EquipmentSlot.MAINHAND)
                .tier()
                    .nutrition(0, 1, 2, desc.nutrition())
                    .saturation(1, 1, 0.4F, desc.saturation())
                    .dataComponentNode(2, eatSpeed(1), desc.eatSpeed(), DataComponents.EAT_SPEED_BONUS.getId(), 6)
                    .dataComponentNode(3, durability(1), desc.durability(), DURABILITY, 25)
                .tier()
                    .nutrition(4, 2, 2, desc.nutrition())
                        .nodeRequirement(0)
                    .saturation(5, 2, 0.4F, desc.saturation())
                        .nodeRequirement(1)
                    .dataComponentNode(6, eatSpeed(2), desc.eatSpeed(), DataComponents.EAT_SPEED_BONUS.getId(), 6)
                        .nodeRequirement(2)
                    .dataComponentNode(7, durability(2), desc.durability(), DURABILITY, 25)
                        .nodeRequirement(3)
                .tier()
                    .infiniteDataComponentNode(8, AUTO_REPAIR, desc.autoRepair(), DataComponents.AUTO_REPAIR.getId(), 1)
                        .previousTierOrRequirements()
                    .alwaysEat(9, desc.alwaysEat())
                        .previousTierOrRequirements()
                .tier()
                    .nutrition(10, 3, 2, desc.nutrition())
                        .nodeRequirement(4)
                    .saturation(11, 3, 0.4F, desc.saturation())
                        .nodeRequirement(5)
                    .dataComponentNode(12, eatSpeed(3), desc.eatSpeed(), DataComponents.EAT_SPEED_BONUS.getId(), 6)
                        .nodeRequirement(6)
                    .dataComponentNode(13, durability(3), desc.durability(), DURABILITY, 25)
                        .nodeRequirement(7)
                .tier()
                    .nutrition(14, 4, 2, desc.nutrition())
                        .nodeRequirement(10)
                    .saturation(15, 4, 0.4F, desc.saturation())
                        .nodeRequirement(11)
                    .dataComponentNode(16, eatSpeed(4), desc.eatSpeed(), DataComponents.EAT_SPEED_BONUS.getId(), 6)
                        .nodeRequirement(12)
                    .dataComponentNode(17, durability(4), desc.durability(), DURABILITY, 25)
                        .nodeRequirement(13)
                .tier()
                    .nutrition(18, 0, 1, desc.nutrition(), 0)
                        .nodeRequirement(14)
                    .saturation(19, 0, 0.1F, desc.saturation(), 0)
                        .nodeRequirement(15)
                    .dataComponentNode(20, eatSpeed(0), desc.eatSpeed(), DataComponents.EAT_SPEED_BONUS.getId(), 1, 7)
                        .nodeRequirement(16)
                    .infiniteDataComponentNode(21, durability(0), desc.durability(), DURABILITY, 10)
                        .nodeRequirement(17)
                .tier()
                    .effect(22, desc, new MobEffectInstance(MobEffects.SPEED, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.SUGAR, Items.GLOWSTONE_DUST)
                    .effect(23, desc, new MobEffectInstance(MobEffects.HASTE, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.GOLD_INGOT, Items.GLOWSTONE_DUST)
                    .effect(24, desc, new MobEffectInstance(MobEffects.STRENGTH, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.BLAZE_POWDER, Items.GLOWSTONE_DUST)
                    .effect(25, desc, new MobEffectInstance(MobEffects.JUMP_BOOST, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.RABBIT_FOOT, Items.GLOWSTONE_DUST)
                    .effect(26, desc, new MobEffectInstance(MobEffects.REGENERATION, 10, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.GHAST_TEAR, Items.GLOWSTONE_DUST)
                    .effect(27, desc, new MobEffectInstance(MobEffects.RESISTANCE, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.IRON_INGOT, Items.GLOWSTONE_DUST)
                    .effect(28, desc, new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.MAGMA_CREAM)
                    .effect(29, desc, new MobEffectInstance(MobEffects.WATER_BREATHING, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.PUFFERFISH)
                    .effect(30, desc, new MobEffectInstance(MobEffects.INVISIBILITY, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.GOLDEN_CARROT, Items.FERMENTED_SPIDER_EYE)
                    .effect(31, desc, new MobEffectInstance(MobEffects.NIGHT_VISION, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.GOLDEN_CARROT)
                    .effect(32, desc, new MobEffectInstance(MobEffects.ABSORPTION, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.GLISTERING_MELON_SLICE, Items.GLOWSTONE_DUST)
                    .effect(33, desc, new MobEffectInstance(MobEffects.SATURATION, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.COOKED_BEEF, Items.GLOWSTONE_DUST)
                    .effect(34, desc, new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.PHANTOM_MEMBRANE)
                .build();
    }

    private SkillData helmet() {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("Helmet");

        SkillData.Builder builder = SkillData.builder(EquipmentSlot.HEAD);
        armorTier(builder, 0, -1, 1, desc);
        armorTier(builder, 4, 0, 2, desc);

        builder
                .tier()
                    .attributeNode(8, baseArmor(1), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                        .previousTierOrRequirements()
                    .attributeNode(9, toughness(1), desc.toughness(), attr(Attributes.ARMOR_TOUGHNESS), 0.25F)
                        .previousTierOrRequirements()
                    .enchantmentNode(10, enchantmentName(Enchantments.AQUA_AFFINITY, 0), desc.enchantment(enchantmentName(Enchantments.AQUA_AFFINITY, 0)), Enchantments.AQUA_AFFINITY, 1);

        armorTier(builder, 11, 4, 3, desc, true);

        builder
                .tier()
                    .attributeNode(15, baseArmor(2), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                        .nodeRequirement(8)
                        .previousTierOrRequirements()
                    .attributeNode(16, toughness(2), desc.toughness(), attr(Attributes.ARMOR_TOUGHNESS), 0.25F)
                        .nodeRequirement(9)
                        .previousTierOrRequirements()
                .enchantmentNode(17, enchantmentName(Enchantments.RESPIRATION, 3), desc.enchantment(enchantmentName(Enchantments.RESPIRATION, 3)), Enchantments.RESPIRATION, 3)
                    .previousTierOrRequirements()
                .infiniteDataComponentNode(18, AUTO_REPAIR, desc.autoRepair(), DataComponents.AUTO_REPAIR.getId(), 1)
                    .previousTierOrRequirements();

        armorTier(builder, 19, 11, 4, desc, true);

        builder
                .tier()
                    .attributeNode(23, baseArmor(3), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                        .nodeRequirement(15)
                        .previousTierOrRequirements()
                    .attributeNode(24, toughness(3), desc.toughness(), attr(Attributes.ARMOR_TOUGHNESS), 0.5F)
                        .nodeRequirement(16)
                        .previousTierOrRequirements()
                    .enchantmentNode(25, enchantmentName(Enchantments.THORNS, 3), desc.enchantment(enchantmentName(Enchantments.THORNS, 3)), Enchantments.THORNS, 3)
                        .previousTierOrRequirements()
                    .dataComponentNode(26, NIGHT_VISION, desc.nightVision(), DataComponents.NIGHT_VISION.getId(), 1)
                        .subText("Can be disabled with the mode switch key", "#ABABAB")
                        .previousTierOrRequirements();


        return builder.build();
    }

    private SkillData chestplate(boolean elytra) {
        SkillTreeDescriptions desc = new SkillTreeDescriptions(elytra ? "Elytra" : "Chestplate");

        SkillData.Builder builder = SkillData.builder(EquipmentSlot.CHEST);
        armorTier(builder, 0, -1, 1, desc);
        armorTier(builder, 4, 0, 2, desc);

        builder
                .tier()
                    .attributeNode(8, baseArmor(1), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                        .previousTierOrRequirements()
                    .attributeNode(9, toughness(1), desc.toughness(), attr(Attributes.ARMOR_TOUGHNESS), 0.25F)
                        .previousTierOrRequirements()
                    .attributeNode(10, healthBonus(1), desc.healthBonus(),attr(Attributes.MAX_HEALTH), 2)
                        .previousTierOrRequirements();

        armorTier(builder, 11, 4, 3, desc, true);

        builder
                .tier()
                    .attributeNode(15, baseArmor(2), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                        .nodeRequirement(8)
                        .previousTierOrRequirements()
                    .attributeNode(16, toughness(2), desc.toughness(), attr(Attributes.ARMOR_TOUGHNESS), 0.25F)
                        .nodeRequirement(9)
                        .previousTierOrRequirements()
                    .attributeNode(17, healthBonus(2), desc.healthBonus(), attr(Attributes.MAX_HEALTH), 2)
                        .nodeRequirement(10)
                        .previousTierOrRequirements()
                    .infiniteDataComponentNode(18, AUTO_REPAIR, desc.autoRepair(), DataComponents.AUTO_REPAIR.getId(), 1)
                        .previousTierOrRequirements();

        armorTier(builder, 19, 11, 4, desc, true);

        builder
                .tier()
                    .attributeNode(23, baseArmor(3), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                        .nodeRequirement(15)
                        .previousTierOrRequirements()
                    .attributeNode(24, toughness(3), desc.toughness(), attr(Attributes.ARMOR_TOUGHNESS), 0.5F)
                        .nodeRequirement(16)
                        .previousTierOrRequirements()
                    .enchantmentNode(25, enchantmentName(Enchantments.THORNS, 3), desc.enchantment(enchantmentName(Enchantments.THORNS, 3)), Enchantments.THORNS, 3)
                        .previousTierOrRequirements()
                    .attributeNode(26, healthBonus(3), desc.healthBonus(), attr(Attributes.MAX_HEALTH), 4)
                        .nodeRequirement(17)
                        .previousTierOrRequirements();

        if (elytra) {
            builder.tier()
                    .attributeNode(27, CREATIVE_FLIGHT, desc.creativeFlight(), attr(NeoForgeMod.CREATIVE_FLIGHT), 1, 100, true)
                        .nodeRequirement(23, 24, 25, 26);
        }

        return builder.build();
    }

    private SkillData leggings() {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("Leggings");

        SkillData.Builder builder = SkillData.builder(EquipmentSlot.LEGS);
        armorTier(builder, 0, -1, 1, desc);
        builder.enchantmentNode(4, enchantmentName(Enchantments.SWIFT_SNEAK, 1), desc.enchantment(enchantmentName(Enchantments.SWIFT_SNEAK, 1)), Enchantments.SWIFT_SNEAK, 1);
        armorTier(builder, 5, 0, 2, desc);
        builder.enchantmentNode(9, enchantmentName(Enchantments.SWIFT_SNEAK, 2), desc.enchantment(enchantmentName(Enchantments.SWIFT_SNEAK, 2)), Enchantments.SWIFT_SNEAK, 2)
                .nodeRequirement(4)
                .previousTierOrRequirements();

        builder
                .tier()
                    .attributeNode(10, baseArmor(1), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                        .previousTierOrRequirements()
                    .attributeNode(11, toughness(1), desc.toughness(), attr(Attributes.ARMOR_TOUGHNESS), 0.25F)
                        .previousTierOrRequirements()
                    .attributeNode(12, moveSpeed(1), desc.moveSpeed(), attr(Attributes.MOVEMENT_SPEED), 1)
                        .previousTierOrRequirements();

        armorTier(builder, 13, 5, 3, desc, true);
        builder.enchantmentNode(17, enchantmentName(Enchantments.SWIFT_SNEAK, 3), desc.enchantment(enchantmentName(Enchantments.SWIFT_SNEAK, 3)), Enchantments.SWIFT_SNEAK, 3)
                .nodeRequirement(9)
                .previousTierOrRequirements();

        builder
                .tier()
                    .attributeNode(18, baseArmor(2), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                        .nodeRequirement(10)
                        .previousTierOrRequirements()
                    .attributeNode(19, toughness(2), desc.toughness(), attr(Attributes.ARMOR_TOUGHNESS), 0.25F)
                        .nodeRequirement(11)
                        .previousTierOrRequirements()
                    .attributeNode(20, moveSpeed(2), desc.moveSpeed(), attr(Attributes.MOVEMENT_SPEED), 1)
                        .nodeRequirement(12)
                        .previousTierOrRequirements()
                    .infiniteDataComponentNode(21, AUTO_REPAIR, desc.autoRepair(), DataComponents.AUTO_REPAIR.getId(), 1)
                        .previousTierOrRequirements();

        armorTier(builder, 22, 13, 4, desc, true);
        builder.enchantmentNode(26, enchantmentName(Enchantments.SWIFT_SNEAK, 4), desc.enchantment(enchantmentName(Enchantments.SWIFT_SNEAK, 4)), Enchantments.SWIFT_SNEAK, 4)
                .nodeRequirement(17)
                .previousTierOrRequirements();

        builder
                .tier()
                    .attributeNode(27, baseArmor(3), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                        .nodeRequirement(18)
                        .previousTierOrRequirements()
                    .attributeNode(28, toughness(3), desc.toughness(), attr(Attributes.ARMOR_TOUGHNESS), 0.5F)
                        .nodeRequirement(19)
                        .previousTierOrRequirements()
                    .enchantmentNode(29, enchantmentName(Enchantments.THORNS, 3), desc.enchantment(enchantmentName(Enchantments.THORNS, 3)), Enchantments.THORNS, 3)
                        .previousTierOrRequirements()
                    .attributeNode(30, moveSpeed(3), desc.moveSpeed(), attr(Attributes.MOVEMENT_SPEED), 1)
                        .nodeRequirement(20)
                        .previousTierOrRequirements()
                    .enchantmentNode(31, enchantmentName(Enchantments.SWIFT_SNEAK, 5), desc.enchantment(enchantmentName(Enchantments.SWIFT_SNEAK, 5)), Enchantments.SWIFT_SNEAK, 5)
                        .nodeRequirement(29);


        return builder.build();
    }

    private SkillData boots() {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("Boots");

        SkillData.Builder builder = SkillData.builder(EquipmentSlot.FEET);
        armorTier(builder, 0, -1, 1, desc);
        armorTier(builder, 4, 0, 2, desc);

        builder
                .tier()
                    .attributeNode(8, baseArmor(1), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                        .previousTierOrRequirements()
                    .attributeNode(9, toughness(1), desc.toughness(), attr(Attributes.ARMOR_TOUGHNESS), 0.25F)
                        .previousTierOrRequirements()
                    .enchantmentNode(10, enchantmentName(Enchantments.FEATHER_FALLING, 4), desc.enchantment(enchantmentName(Enchantments.FEATHER_FALLING, 4)), Enchantments.FEATHER_FALLING, 4)
                        .previousTierOrRequirements();

        armorTier(builder, 11, 4, 3, desc, true);

        builder
                .tier()
                    .attributeNode(15, baseArmor(2), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                        .nodeRequirement(8)
                        .previousTierOrRequirements()
                    .attributeNode(16, toughness(2), desc.toughness(), attr(Attributes.ARMOR_TOUGHNESS), 0.25F)
                        .nodeRequirement(9)
                        .previousTierOrRequirements()
                    .enchantmentNode(17, enchantmentName(Enchantments.DEPTH_STRIDER, 3), desc.enchantment(enchantmentName(Enchantments.DEPTH_STRIDER, 3)), Enchantments.DEPTH_STRIDER, 3)
                        .previousTierOrRequirements()
                    .infiniteDataComponentNode(18, AUTO_REPAIR, desc.autoRepair(), DataComponents.AUTO_REPAIR.getId(), 1)
                        .previousTierOrRequirements();

        armorTier(builder, 19, 11, 4, desc, true);

        builder
                .tier()
                    .attributeNode(23, baseArmor(3), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                        .nodeRequirement(15)
                        .previousTierOrRequirements()
                    .attributeNode(24, toughness(3), desc.toughness(), attr(Attributes.ARMOR_TOUGHNESS), 0.5F)
                        .nodeRequirement(16)
                        .previousTierOrRequirements()
                    .enchantmentNode(25, enchantmentName(Enchantments.THORNS, 3), desc.enchantment(enchantmentName(Enchantments.THORNS, 3)), Enchantments.THORNS, 3)
                        .previousTierOrRequirements()
                    .enchantmentNode(26, enchantmentName(Enchantments.SOUL_SPEED, 3), desc.enchantment(enchantmentName(Enchantments.SOUL_SPEED, 3)), Enchantments.SOUL_SPEED, 3)
                        .previousTierOrRequirements()
                    .enchantmentNode(27, enchantmentName(Enchantments.FROST_WALKER, 0), desc.enchantment(enchantmentName(Enchantments.FROST_WALKER, 0)), Enchantments.FROST_WALKER, 2)
                        .previousTierOrRequirements()
                    .subText("Can be disabled with the mode switch key", "#ABABAB");

        return builder.build();
    }

    private SkillData bow() {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("Bow");
        SkillData.Builder builder = SkillData.builder(EquipmentSlot.MAINHAND);

        return builder
                .tier()
                    .dataComponentNode(0, arrowDamage(1), desc.arrowDamage(), DataComponents.ARROW_DAMAGE.getId(), 1)
                    .dataComponentNode(1, arrowSpeed(1), desc.arrowSpeed(), DataComponents.ARROW_SPEED.getId(), 1)
                    .dataComponentNode(2, drawSpeed(1), desc.drawSpeed(), DataComponents.DRAW_SPEED.getId(), 1)
                    .dataComponentNode(3, durability(1), desc.durability(), DURABILITY, 200)
                .tier()
                    .dataComponentNode(4, arrowDamage(2), desc.arrowDamage(), DataComponents.ARROW_DAMAGE.getId(), 1)
                        .nodeRequirement(0)
                    .dataComponentNode(5, arrowSpeed(2), desc.arrowSpeed(), DataComponents.ARROW_SPEED.getId(), 1)
                        .nodeRequirement(1)
                    .dataComponentNode(6, drawSpeed(2), desc.drawSpeed(), DataComponents.DRAW_SPEED.getId(), 1)
                        .nodeRequirement(2)
                    .dataComponentNode(7, durability(2), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(3)
                .tier()
                    .enchantmentNode(8, desc, Enchantments.PUNCH, 1)
                        .previousTierOrRequirements()
                    .enchantmentNode(9, desc, Enchantments.FLAME, 0)
                        .previousTierOrRequirements()
                    .dataComponentNode(10, unbreaking(1), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(11, arrowDamage(3), desc.arrowDamage(), DataComponents.ARROW_DAMAGE.getId(), 1)
                        .nodeRequirement(4)
                        .previousTierOrRequirements()
                    .dataComponentNode(12, arrowSpeed(3), desc.arrowSpeed(), DataComponents.ARROW_SPEED.getId(), 1)
                        .nodeRequirement(5)
                        .previousTierOrRequirements()
                    .dataComponentNode(13, drawSpeed(3), desc.drawSpeed(), DataComponents.DRAW_SPEED.getId(), 1)
                        .nodeRequirement(6)
                        .previousTierOrRequirements()
                    .dataComponentNode(14, durability(3), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(7)
                        .previousTierOrRequirements()
                .tier()
                    .enchantmentNode(15, desc, Enchantments.PUNCH, 2)
                        .nodeRequirement(8)
                       .previousTierOrRequirements()
                    .enchantmentNode(16, desc, Enchantments.INFINITY, 0)
                        .previousTierOrRequirements()
                    .dataComponentNode(17, unbreaking(2), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .nodeRequirement(10)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(18, arrowDamage(4), desc.arrowDamage(), DataComponents.ARROW_DAMAGE.getId(), 1)
                        .nodeRequirement(11)
                        .previousTierOrRequirements()
                    .dataComponentNode(19, arrowSpeed(4), desc.arrowSpeed(), DataComponents.ARROW_SPEED.getId(), 1)
                        .nodeRequirement(12)
                        .previousTierOrRequirements()
                    .dataComponentNode(20, drawSpeed(4), desc.drawSpeed(), DataComponents.DRAW_SPEED.getId(), 1)
                        .nodeRequirement(13)
                        .previousTierOrRequirements()
                    .dataComponentNode(21, durability(4), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(14)
                        .previousTierOrRequirements()
                .tier()
                    .enchantmentNode(22, desc, Enchantments.PUNCH, 3)
                        .nodeRequirement(15)
                        .previousTierOrRequirements()
                    .dataComponentNode(23, AUTO_REPAIR, desc.autoRepair(), DataComponents.AUTO_REPAIR.getId(), 1, 0)
                        .previousTierOrRequirements()
                    .dataComponentNode(24, unbreaking(3), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .nodeRequirement(17)
                        .previousTierOrRequirements()
                    .dataComponentNode(25, AUTO_TARGET, desc.autoTarget(), DataComponents.AUTO_TARGET.getId(), 1F)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(26, arrowDamage(5), desc.arrowDamage(), DataComponents.ARROW_DAMAGE.getId(), 1)
                        .nodeRequirement(18)
                        .previousTierOrRequirements()
                    .dataComponentNode(27, arrowSpeed(5), desc.arrowSpeed(), DataComponents.ARROW_SPEED.getId(), 1)
                        .nodeRequirement(19)
                        .previousTierOrRequirements()
                    .dataComponentNode(28, drawSpeed(5), desc.drawSpeed(), DataComponents.DRAW_SPEED.getId(), 1)
                        .nodeRequirement(20)
                        .previousTierOrRequirements()
                    .dataComponentNode(29, durability(5), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(21)
                        .previousTierOrRequirements()
                .tier()
                    .infiniteDataComponentNode(30, arrowDamage(0), desc.arrowDamage(), DataComponents.ARROW_DAMAGE.getId(), 0.2F)
                        .nodeRequirement(26)
                    .infiniteDataComponentNode(31, arrowSpeed(0), desc.arrowSpeed(), DataComponents.ARROW_SPEED.getId(), 0.2F)
                        .nodeRequirement(27)
                    .infiniteDataComponentNode(32, drawSpeed(0), desc.drawSpeed(), DataComponents.DRAW_SPEED.getId(), 0.2F)
                        .nodeRequirement(28)
                    .infiniteDataComponentNode(33, durability(0), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(29)
                    .dataComponentNode(34, unbreaking(0), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.01F, 70)
                        .nodeRequirement(24)
                        .previousTierOrRequirements()
                .tier()
                    .effect(35, desc, new MobEffectInstance(MobEffects.POISON, 120, 1, false, false), "Tipped: ")
                        .itemRequirement(Items.NETHER_WART, Items.SPIDER_EYE, Items.FERMENTED_SPIDER_EYE, Items.GLOWSTONE_DUST)
                        .previousTierOrRequirements()
                    .effect(36, desc, new MobEffectInstance(MobEffects.WEAKNESS, 240, 1, false, false), "Tipped: ")
                        .itemRequirement(Items.NETHER_WART, Items.BLAZE_POWDER, Items.FERMENTED_SPIDER_EYE, Items.GLOWSTONE_DUST)
                        .previousTierOrRequirements()
                    .effect(37, desc, new MobEffectInstance(MobEffects.SLOWNESS, 240, 1, false, false), "Tipped: ")
                        .itemRequirement(Items.NETHER_WART, Items.SUGAR, Items.FERMENTED_SPIDER_EYE, Items.GLOWSTONE_DUST)
                        .previousTierOrRequirements()
                    .effect(38, desc, new MobEffectInstance(MobEffects.WITHER, 120, 1, false, false), "Tipped: ")
                        .itemRequirement(Items.NETHER_WART, Items.BONE, Items.FERMENTED_SPIDER_EYE, Items.GLOWSTONE_DUST)
                        .previousTierOrRequirements()
                .build();
    }

    private SkillData rocket() {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("Rocket");
        SkillData.Builder builder = SkillData.builder(EquipmentSlot.MAINHAND);

        return builder
                .tier()
                    .dataComponentNode(0, durability(1), desc.durability(), DURABILITY, 25)
                    .dataComponentNode(1, flightDuration(1), desc.flightDuration(), DataComponents.FLIGHT_TIME.getId(), 2)
                        .itemRequirement(Items.GUNPOWDER)
                .tier()
                    .dataComponentNode(2, durability(2), desc.durability(), DURABILITY, 25)
                        .nodeRequirement(0)
                    .dataComponentNode(3, flightDuration(2), desc.flightDuration(), DataComponents.FLIGHT_TIME.getId(), 2)
                        .nodeRequirement(1)
                        .itemRequirement(Items.GUNPOWDER)
                .tier()
                    .infiniteDataComponentNode(4, durability(0), desc.durability(), DURABILITY, 10)
                        .nodeRequirement(2)
                    .infiniteDataComponentNode(5, AUTO_REPAIR, desc.autoRepair(), DataComponents.AUTO_REPAIR.getId(), 1)
                        .previousTierOrRequirements()
                .build();
    }

    private void armorTier(SkillData.Builder builder, int indexStart, int prevIndexStart, int level, SkillTreeDescriptions desc) {
        armorTier(builder, indexStart, prevIndexStart, level, desc, false);
    }

    private void armorTier(SkillData.Builder builder, int indexStart, int prevIndexStart, int level, SkillTreeDescriptions desc, boolean previousTierOrRequirements) {
        builder
            .tier()
                .enchantmentNode(indexStart++, protection(level), desc.enchantment(enchantmentName(Enchantments.PROTECTION, level)), Enchantments.PROTECTION, level)
                    .optional(prevIndexStart >= 0)
                        .nodeRequirement(prevIndexStart++)
                    .endOptional()
                    .optional(previousTierOrRequirements)
                        .previousTierOrRequirements()
                    .endOptional()
                .enchantmentNode(indexStart++, fireProtection(level), desc.enchantment(enchantmentName(Enchantments.FIRE_PROTECTION, level)), Enchantments.FIRE_PROTECTION, level)
                    .optional(prevIndexStart >= 0)
                        .nodeRequirement(prevIndexStart++)
                    .endOptional()
                    .optional(previousTierOrRequirements)
                        .previousTierOrRequirements()
                    .endOptional()
                .enchantmentNode(indexStart++, blastProtection(level), desc.enchantment(enchantmentName(Enchantments.BLAST_PROTECTION, level)), Enchantments.BLAST_PROTECTION, level)
                    .optional(prevIndexStart >= 0)
                        .nodeRequirement(prevIndexStart++)
                    .endOptional()
                    .optional(previousTierOrRequirements)
                        .previousTierOrRequirements()
                    .endOptional()
                .enchantmentNode(indexStart, projectileProtection(level), desc.enchantment(enchantmentName(Enchantments.PROJECTILE_PROTECTION, level)), Enchantments.PROJECTILE_PROTECTION, level)
                    .optional(prevIndexStart >= 0)
                        .nodeRequirement(prevIndexStart)
                    .endOptional()
                    .optional(previousTierOrRequirements)
                        .previousTierOrRequirements()
                    .endOptional();
    }

    private ResourceLocation attr(Holder<Attribute> attribute) {
        ResourceKey<?> key = attribute.getKey();
        if (key == null) {
            throw new IllegalArgumentException("Invalid attribute " + attribute);
        }

        return key.location();
    }

    public static String enchantmentName(ResourceKey<Enchantment> enchantment, int level) {
        if (level == 0) {
            return formatKey(enchantment.location().getPath());
        } else {
            return formatKey(enchantment.location().getPath()) + " " + intToRomanNumeral(level);
        }
    }
}

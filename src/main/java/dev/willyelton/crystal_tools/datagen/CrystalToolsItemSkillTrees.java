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
import static dev.willyelton.crystal_tools.utils.constants.SkillConstants.BLOCKS_ATTACKS;
import static dev.willyelton.crystal_tools.utils.constants.SkillConstants.DURABILITY;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.*;

public class CrystalToolsItemSkillTrees {
    private final BootstrapContext<SkillData> context;

    public static void register(BootstrapContext<SkillData> context) {
        CrystalToolsItemSkillTrees skillTrees = new CrystalToolsItemSkillTrees(context);
        skillTrees.registerSkillTrees();
    }

    public CrystalToolsItemSkillTrees(BootstrapContext<SkillData> context) {
        this.context = context;
    }

    public void registerSkillTrees() {
        // Basic Mining Tools
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_PICKAXE.getId()), basicMiningTool("Pickaxe"));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_HOE.getId()), basicMiningTool("Hoe"));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_SHOVEL.getId()), basicMiningTool("Shovel"));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_AXE.getId()), basicMiningTool("Axe"));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_AIOT.getId()), aiot());

        // Other Tools
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_FISHING_ROD.getId()), fishingRod());
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_SHIELD.getId()), shield());

        // Weapons
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_SWORD.getId()), sword());
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_BOW.getId()), bow());
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_TRIDENT.getId()), trident());

        // Food
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_APPLE.getId()), food("Apple"));

        // Armor
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_HELMET.getId()), helmet());
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_CHESTPLATE.getId()), chestplate(false));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_LEGGINGS.getId()), leggings());
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_BOOTS.getId()), boots());
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_ELYTRA.getId()), chestplate(true));

        // Misc
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_ROCKET.getId()), rocket());
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                Registration.CRYSTAL_BACKPACK.getId()), backpack());
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

    private SkillData sword() {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("Sword");

        return SkillData.builder(EquipmentSlot.MAINHAND)
                .tier()
                    .attributeNode(0, attackDamage(1), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                    .attributeNode(1, attackSpeed(1), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 1)
                    .dataComponentNode(2, durability(1), desc.durability(), DURABILITY, 200)
                .tier()
                    .attributeNode(3, attackDamage(2), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                        .nodeRequirement(0)
                    .attributeNode(4, attackSpeed(2), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 1)
                        .nodeRequirement(1)
                    .dataComponentNode(5, durability(2), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(2)
                .tier()
                    .enchantmentNode(6, desc, Enchantments.FIRE_ASPECT, 2)
                        .previousTierOrRequirements()
                    .attributeNode(7, knockbackResistance(1), desc.knockbackResistance(), attr(Attributes.KNOCKBACK_RESISTANCE), 0.1F)
                        .previousTierOrRequirements()
                    .attributeNode(8, knockback(1), desc.knockback(), attr(Attributes.KNOCKBACK_RESISTANCE), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(9, unbreaking(1), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .previousTierOrRequirements()
                    .dataComponentNode(10, beheading(1), desc.beheading(), DataComponents.BEHEADING.getId(), 0.1F)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(11, attackDamage(3), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                        .nodeRequirement(3)
                        .previousTierOrRequirements()
                    .attributeNode(12, attackSpeed(3), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 1)
                        .nodeRequirement(4)
                        .previousTierOrRequirements()
                    .dataComponentNode(13, durability(3), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(5)
                        .previousTierOrRequirements()
                    .attributeNode(14, reach(1), desc.reach(), List.of(attr(Attributes.ENTITY_INTERACTION_RANGE), attr(Attributes.BLOCK_INTERACTION_RANGE)), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(15, capturing(1), desc.capturing(), DataComponents.CAPTURING.getId(), 1)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(16, lifesteal(1), desc.lifesteal(), DataComponents.LIFESTEAL.getId(), 2)
                        .previousTierOrRequirements()
                    .attributeNode(17, knockbackResistance(2), desc.knockbackResistance(), attr(Attributes.KNOCKBACK_RESISTANCE), 0.1F)
                        .nodeRequirement(7)
                        .previousTierOrRequirements()
                    .attributeNode(18, knockback(2), desc.knockback(), attr(Attributes.KNOCKBACK_RESISTANCE), 1)
                        .nodeRequirement(8)
                        .previousTierOrRequirements()
                    .dataComponentNode(19, unbreaking(2), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .nodeRequirement(9)
                        .previousTierOrRequirements()
                    .dataComponentNode(20, beheading(2), desc.beheading(), DataComponents.BEHEADING.getId(), 0.1F)
                        .nodeRequirement(10)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(21, attackDamage(4), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                        .nodeRequirement(11)
                        .previousTierOrRequirements()
                    .attributeNode(22, attackSpeed(4), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 1)
                        .nodeRequirement(12)
                        .previousTierOrRequirements()
                    .dataComponentNode(23, durability(4), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(13)
                        .previousTierOrRequirements()
                    .attributeNode(24, reach(2), desc.reach(), List.of(attr(Attributes.ENTITY_INTERACTION_RANGE), attr(Attributes.BLOCK_INTERACTION_RANGE)), 1)
                        .nodeRequirement(14)
                        .previousTierOrRequirements()
                    .dataComponentNode(25, capturing(2), desc.capturing(), DataComponents.CAPTURING.getId(), 1)
                        .nodeRequirement(15)
                        .previousTierOrRequirements()
                .tier()
                    .enchantmentNode(26, desc, Enchantments.LOOTING, 3)
                        .previousTierOrRequirements()
                    .dataComponentNode(27, AUTO_REPAIR, desc.autoRepair(), DataComponents.AUTO_REPAIR.getId(), 1, 0)
                        .previousTierOrRequirements()
                    .dataComponentNode(28, unbreaking(3), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .nodeRequirement(19)
                        .previousTierOrRequirements()
                    .dataComponentNode(29, beheading(3), desc.beheading(), DataComponents.BEHEADING.getId(), 0.1F)
                        .nodeRequirement(20)
                        .previousTierOrRequirements()
                    .enchantmentNode(30, desc, Enchantments.SWEEPING_EDGE, 3)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(31, attackDamage(5), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                        .nodeRequirement(21)
                        .previousTierOrRequirements()
                    .attributeNode(32, attackSpeed(5), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 1)
                        .nodeRequirement(22)
                        .previousTierOrRequirements()
                    .dataComponentNode(33, durability(4), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(23)
                        .previousTierOrRequirements()
                    .attributeNode(34, reach(3), desc.reach(), List.of(attr(Attributes.ENTITY_INTERACTION_RANGE), attr(Attributes.BLOCK_INTERACTION_RANGE)), 1)
                        .nodeRequirement(24)
                        .previousTierOrRequirements()
                    .dataComponentNode(35, capturing(3), desc.capturing(), DataComponents.CAPTURING.getId(), 1)
                        .nodeRequirement(25)
                        .previousTierOrRequirements()
                .tier()
                    .infiniteAttributeNode(36, attackDamage(0), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 0.25F)
                        .nodeRequirement(31)
                    .infiniteDataComponentNode(37, attackSpeed(0), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 0.25F)
                        .nodeRequirement(32)
                    .infiniteDataComponentNode(38, durability(0), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(33)
                    .dataComponentNode(39, unbreaking(0), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F, 70)
                        .nodeRequirement(28)
                        .previousTierOrRequirements()
                    .infiniteAttributeNode(40, reach(0), desc.reach(), List.of(attr(Attributes.ENTITY_INTERACTION_RANGE), attr(Attributes.BLOCK_INTERACTION_RANGE)), 0.1F)
                        .nodeRequirement(34)
                .tier()
                    .enchantmentNode(41, desc, Enchantments.LOOTING, 5)
                        .previousTierAndRequirements()
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
        if (elytra) {
            builder.dataComponentNode(28, unbreaking(1), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                    .previousTierOrRequirements();
        }

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

        if (elytra) {
            builder.dataComponentNode(29, unbreaking(2), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                    .nodeRequirement(28)
                    .previousTierOrRequirements();
        }

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
            builder.dataComponentNode(30, unbreaking(3), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                    .nodeRequirement(29)
                    .previousTierOrRequirements();
        }

        if (elytra) {
            builder.tier()
                    .dataComponentNode(31, unbreaking(0), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.01F, 70)
                        .nodeRequirement(30)
                    .attributeNode(27, CREATIVE_FLIGHT, desc.creativeFlight(), attr(NeoForgeMod.CREATIVE_FLIGHT), 1, 100, true)
                        .nodeRequirement(23, 24, 25, 26, 30);
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
                    .dataComponentNode(39, beheading(1), desc.beheading(), DataComponents.BEHEADING.getId(), 0.1F)
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
                    .dataComponentNode(42, capturing(1), desc.capturing(), DataComponents.CAPTURING.getId(), 0.01F)
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
                    .dataComponentNode(40, beheading(2), desc.beheading(), DataComponents.BEHEADING.getId(), 0.1F)
                        .nodeRequirement(39)
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
                .dataComponentNode(43, capturing(2), desc.capturing(), DataComponents.CAPTURING.getId(), 0.01F)
                    .nodeRequirement(42)
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
                    .dataComponentNode(25, AUTO_TARGET, desc.autoTarget(false), DataComponents.AUTO_TARGET.getId(), 1F)
                        .previousTierOrRequirements()
                    .dataComponentNode(41, beheading(3), desc.beheading(), DataComponents.BEHEADING.getId(), 0.1F)
                        .nodeRequirement(40)
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
                    .dataComponentNode(44, capturing(3), desc.capturing(), DataComponents.CAPTURING.getId(), 0.01F)
                        .nodeRequirement(43)
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
        return SkillData.builder(EquipmentSlot.MAINHAND)
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

    private SkillData fishingRod() {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("Fishing Rod");

        return SkillData.builder(EquipmentSlot.MAINHAND)
                .tier()
                    .enchantmentNode(0, desc, Enchantments.LURE, 1)
                    .enchantmentNode(1, desc, Enchantments.LUCK_OF_THE_SEA, 1)
                    .dataComponentNode(2, durability(1), desc.durability(), DURABILITY, 50)
                .tier()
                    .enchantmentNode(3, desc, Enchantments.LURE, 2)
                        .nodeRequirement(0)
                    .enchantmentNode(4, desc, Enchantments.LUCK_OF_THE_SEA, 2)
                        .nodeRequirement(1)
                    .dataComponentNode(5, durability(2), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(2)
                .tier()
                    .dataComponentNode(6, unbreaking(1), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .previousTierOrRequirements()
                    .dataComponentNode(7, doubleItems(1), desc.doubleItems(), DataComponents.DOUBLE_DROPS.getId(), 0.2F)
                        .previousTierAndRequirements()
                .tier()
                    .enchantmentNode(8, desc, Enchantments.LURE, 3)
                        .nodeRequirement(3)
                        .previousTierAndRequirements()
                    .enchantmentNode(9, desc, Enchantments.LUCK_OF_THE_SEA, 3)
                        .nodeRequirement(4)
                        .previousTierAndRequirements()
                    .dataComponentNode(10, durability(3), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(5)
                        .previousTierAndRequirements()
                .tier()
                    .dataComponentNode(11, unbreaking(2), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .nodeRequirement(6)
                        .previousTierOrRequirements()
                    .dataComponentNode(12, doubleItems(2), desc.doubleItems(), DataComponents.DOUBLE_DROPS.getId(), 0.2F)
                        .nodeRequirement(7)
                        .previousTierAndRequirements()
                .tier()
                    .enchantmentNode(13, desc, Enchantments.LURE, 4)
                        .nodeRequirement(8)
                        .previousTierAndRequirements()
                    .enchantmentNode(14, desc, Enchantments.LUCK_OF_THE_SEA, 4)
                        .nodeRequirement(9)
                        .previousTierAndRequirements()
                    .dataComponentNode(15, durability(4), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(10)
                        .previousTierAndRequirements()
                .tier()
                    .dataComponentNode(16, unbreaking(3), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .nodeRequirement(11)
                        .previousTierOrRequirements()
                    .dataComponentNode(17, doubleItems(3), desc.doubleItems(), DataComponents.DOUBLE_DROPS.getId(), 0.2F)
                        .nodeRequirement(12)
                        .previousTierAndRequirements()
                    .dataComponentNode(18, AUTO_REPAIR, desc.autoRepair(), DataComponents.AUTO_REPAIR.getId(), 1, 0)
                        .previousTierAndRequirements()
                .tier()
                    .enchantmentNode(19, desc, Enchantments.LURE, 5)
                        .nodeRequirement(13)
                        .previousTierAndRequirements()
                    .enchantmentNode(20, desc, Enchantments.LUCK_OF_THE_SEA, 5)
                        .nodeRequirement(14)
                        .previousTierAndRequirements()
                    .dataComponentNode(21, durability(5), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(15)
                        .previousTierAndRequirements()
                .tier()
                    .dataComponentNode(22, unbreaking(0), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.01F, 70)
                .build();
    }

    private SkillData shield() {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("Shield");

        return SkillData.builder(EquipmentSlot.OFFHAND)
                .tier()
                    .dataComponentNode(0, durability(1), desc.durability(), DURABILITY, 200)
                    .attributeNode(1, baseArmor(1), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                    .dataComponentNode(2, thorns(1), desc.thorns(), DataComponents.SHIELD_THORNS.getId(), 1)
                    .dataComponentNode(3, shieldCooldown(1), desc.shieldCooldown(), BLOCKS_ATTACKS, 0.1F)
                .tier()
                    .attributeNode(4, attackDamage(1), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 2)
                        .previousTierOrRequirements()
                    .dataComponentNode(5, flamingShield(1), desc.flamingShield(), DataComponents.FLAMING_SHIELD.getId(), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(6, totemSlot(1), desc.totemSlot(), DataComponents.TOTEM_SLOTS.getId(), 1)
                        .subText(TOTEM_SLOT_SUBTEXT, "#ABABAB")
                        .previousTierOrRequirements()
                    .effect(7, desc, new MobEffectInstance(MobEffects.SLOWNESS, 100, 1), "", false)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(8, durability(2), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(0)
                        .previousTierOrRequirements()
                    .attributeNode(9, baseArmor(2), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                        .nodeRequirement(1)
                        .previousTierOrRequirements()
                    .dataComponentNode(10, thorns(2), desc.thorns(), DataComponents.SHIELD_THORNS.getId(), 1)
                        .nodeRequirement(2)
                        .previousTierOrRequirements()
                    .dataComponentNode(11, shieldCooldown(2), desc.shieldCooldown(), BLOCKS_ATTACKS, 0.1F)
                        .nodeRequirement(3)
                        .previousTierOrRequirements()
                    .dataComponentNode(12, AUTO_REPAIR, desc.autoRepair(), DataComponents.AUTO_REPAIR.getId(), 1, 0)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(13, attackDamage(2), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 2)
                        .nodeRequirement(4)
                        .previousTierOrRequirements()
                    .dataComponentNode(14, SHIELD_KNOCKBACK, desc.shieldKnockback(), DataComponents.SHIELD_KNOCKBACK.getId(), 1)
                        .itemRequirement(Items.WIND_CHARGE)
                        .previousTierOrRequirements()
                    .dataComponentNode(15, totemSlot(1), desc.totemSlot(), DataComponents.TOTEM_SLOTS.getId(), 1)
                        .subText(TOTEM_SLOT_SUBTEXT, "#ABABAB")
                        .nodeRequirement(6)
                        .previousTierOrRequirements()
                    .effect(16, desc, new MobEffectInstance(MobEffects.POISON, 100, 1), "", false)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(17, durability(3), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(8)
                        .previousTierOrRequirements()
                    .attributeNode(18, baseArmor(3), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                        .nodeRequirement(9)
                        .previousTierOrRequirements()
                    .dataComponentNode(19, thorns(3), desc.thorns(), DataComponents.SHIELD_THORNS.getId(), 1)
                        .nodeRequirement(10)
                        .previousTierOrRequirements()
                    .dataComponentNode(20, shieldCooldown(3), desc.shieldCooldown(), BLOCKS_ATTACKS, 0.1F)
                        .nodeRequirement(11)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(21, attackDamage(3), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 2)
                        .nodeRequirement(13)
                        .previousTierOrRequirements()
                    .dataComponentNode(22, AUTO_TARGET, desc.autoTarget(true), DataComponents.AUTO_TARGET.getId(), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(23, totemSlot(1), desc.totemSlot(), DataComponents.TOTEM_SLOTS.getId(), 1)
                        .subText(TOTEM_SLOT_SUBTEXT, "#ABABAB")
                        .nodeRequirement(15)
                        .previousTierOrRequirements()
                    .effect(24, desc, new MobEffectInstance(MobEffects.WITHER, 100, 1), "", false)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(25, durability(4), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(17)
                        .previousTierOrRequirements()
                    .attributeNode(26, baseArmor(4), desc.baseArmor(), attr(Attributes.ARMOR), 1)
                        .nodeRequirement(18)
                        .previousTierOrRequirements()
                    .dataComponentNode(27, thorns(4), desc.thorns(), DataComponents.SHIELD_THORNS.getId(), 1)
                        .nodeRequirement(19)
                        .previousTierOrRequirements()
                    .dataComponentNode(28, shieldCooldown(4), desc.shieldCooldown(), BLOCKS_ATTACKS, 0.1F)
                        .nodeRequirement(20)
                        .previousTierOrRequirements()
                .tier()
                    .infiniteDataComponentNode(29, durability(0), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(25)
                    .infiniteAttributeNode(30, attackDamage(0), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 0.1F)
                        .nodeRequirement(21)
                        .previousTierOrRequirements()
                    .dataComponentNode(31, shieldCooldown(0), desc.shieldCooldown(), BLOCKS_ATTACKS, 0.25F, 24)
                        .subText("When you have all points here, the shield can't be disabled!", "#ABABAB")
                        .nodeRequirement(28)
                .build();
    }

    private SkillData trident() {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("Trident");

        return SkillData.builder(EquipmentSlot.MAINHAND)
                .tier()
                    .attributeNode(0, attackDamage(1), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                    .dataComponentNode(1, projectileDamage(1), desc.projectileDamage(), DataComponents.PROJECTILE_DAMAGE.getId(), 1)
                    .dataComponentNode(2, durability(1), desc.durability(), DURABILITY, 200)
                .tier()
                    .dataComponentNode(3, unbreaking(1), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .previousTierOrRequirements()
                    .dataComponentNode(4, projectileSpeed(1), desc.projectileSpeed(), DataComponents.PROJECTILE_SPEED.getId(), 1)
                        .previousTierOrRequirements()
                    .attributeNode(5, reach(1), desc.reach(), List.of(attr(Attributes.ENTITY_INTERACTION_RANGE), attr(Attributes.BLOCK_INTERACTION_RANGE)), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(6, beheading(1), desc.beheading(), DataComponents.BEHEADING.getId(), 0.1F)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(7, riptide(1), desc.riptide(), DataComponents.RIPTIDE.getId(), 1)
                        .previousTierOrRequirements()
                        .notNodeRequirement(8, 32)
                    .enchantmentNode(8, desc, Enchantments.LOYALTY, 1)
                        .previousTierOrRequirements()
                        .notNodeRequirement(7, 32)
                    .dataComponentNode(9, CHANNELING, desc.channeling(), DataComponents.CHANNELING.getId(), 1, 0)
                        .previousTierOrRequirements()
                        .notNodeRequirement(7, 32)
                    .dataComponentNode(10, capturing(1), desc.capturing(), DataComponents.CAPTURING.getId(), 0.01F)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(11, attackDamage(2), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                        .nodeRequirement(0)
                        .previousTierOrRequirements()
                    .dataComponentNode(12, projectileDamage(2), desc.projectileDamage(), DataComponents.PROJECTILE_DAMAGE.getId(), 1)
                        .nodeRequirement(1)
                        .previousTierOrRequirements()
                    .dataComponentNode(13, durability(2), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(2)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(14, unbreaking(2), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .nodeRequirement(3)
                        .previousTierOrRequirements()
                    .dataComponentNode(15, projectileSpeed(2), desc.projectileSpeed(), DataComponents.PROJECTILE_SPEED.getId(), 1)
                        .nodeRequirement(4)
                        .previousTierOrRequirements()
                    .attributeNode(16, reach(2), desc.reach(), List.of(attr(Attributes.ENTITY_INTERACTION_RANGE), attr(Attributes.BLOCK_INTERACTION_RANGE)), 1)
                        .nodeRequirement(5)
                        .previousTierOrRequirements()
                    .dataComponentNode(17, beheading(2), desc.beheading(), DataComponents.BEHEADING.getId(), 0.1F)
                        .nodeRequirement(6)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(18, riptide(2), desc.riptide(), DataComponents.RIPTIDE.getId(), 1)
                        .nodeRequirement(7)
                        .previousTierOrRequirements()
                        .notNodeRequirement(8, 32)
                    .enchantmentNode(19, desc, Enchantments.LOYALTY, 2)
                        .nodeRequirement(8)
                        .previousTierOrRequirements()
                        .notNodeRequirement(7, 32)
                    .dataComponentNode(20, capturing(1), desc.capturing(), DataComponents.CAPTURING.getId(), 0.01F)
                        .nodeRequirement(10)
                        .previousTierOrRequirements()
                    .dataComponentNode(21, AUTO_REPAIR, desc.autoRepair(), DataComponents.AUTO_REPAIR.getId(), 1, 0)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(22, attackDamage(3), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                        .nodeRequirement(11)
                        .previousTierOrRequirements()
                    .dataComponentNode(23, projectileDamage(3), desc.projectileDamage(), DataComponents.PROJECTILE_DAMAGE.getId(), 1)
                        .nodeRequirement(12)
                        .previousTierOrRequirements()
                    .dataComponentNode(24, durability(3), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(13)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(25, unbreaking(3), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .nodeRequirement(14)
                        .previousTierOrRequirements()
                    .dataComponentNode(26, projectileSpeed(3), desc.projectileSpeed(), DataComponents.PROJECTILE_SPEED.getId(), 1)
                        .nodeRequirement(15)
                        .previousTierOrRequirements()
                    .attributeNode(27, reach(3), desc.reach(), List.of(attr(Attributes.ENTITY_INTERACTION_RANGE), attr(Attributes.BLOCK_INTERACTION_RANGE)), 1)
                        .nodeRequirement(16)
                        .previousTierOrRequirements()
                    .dataComponentNode(28, beheading(3), desc.beheading(), DataComponents.BEHEADING.getId(), 0.1F)
                        .nodeRequirement(17)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(29, riptide(3), desc.riptide(), DataComponents.RIPTIDE.getId(), 1)
                        .nodeRequirement(18)
                        .previousTierOrRequirements()
                        .notNodeRequirement(8, 32)
                    .enchantmentNode(30, desc, Enchantments.LOYALTY, 3)
                        .nodeRequirement(19)
                        .previousTierOrRequirements()
                        .notNodeRequirement(7, 32)
                    .dataComponentNode(31, capturing(1), desc.capturing(), DataComponents.CAPTURING.getId(), 0.01F)
                        .nodeRequirement(20)
                        .previousTierOrRequirements()
                    .dataComponentNode(32, RIPTIDE_TOGGLE, desc.riptideToggle(), DataComponents.MINE_MODE.getId(), 1)
                        .previousTierOrRequirements()
                .tier()
                    .infiniteAttributeNode(33, attackDamage(0), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 0.25F)
                        .nodeRequirement(22)
                        .previousTierOrRequirements()
                    .infiniteDataComponentNode(34, projectileDamage(0), desc.projectileDamage(), DataComponents.PROJECTILE_DAMAGE.getId(), 0.25F)
                        .nodeRequirement(23)
                        .previousTierOrRequirements()
                    .infiniteDataComponentNode(35, durability(0), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(24)
                        .previousTierOrRequirements()
                    .dataComponentNode(36, unbreaking(0), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.01F, 70)
                        .nodeRequirement(25)
                        .previousTierOrRequirements()
                    .infiniteDataComponentNode(37, projectileSpeed(0), desc.projectileSpeed(), DataComponents.PROJECTILE_SPEED.getId(), 0.25F)
                        .nodeRequirement(26)
                        .previousTierOrRequirements()
                    .infiniteAttributeNode(38, reach(0), desc.reach(), List.of(attr(Attributes.ENTITY_INTERACTION_RANGE), attr(Attributes.BLOCK_INTERACTION_RANGE)), 0.1F)
                        .nodeRequirement(27)
                        .previousTierOrRequirements()
                    .infiniteDataComponentNode(39, riptide(0), desc.riptide(), DataComponents.RIPTIDE.getId(), 0.25F)
                        .nodeRequirement()
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(40, "Always Riptide", "Allows you to Riptide even when it isn't raining", DataComponents.ALWAYS_RIPTIDE.getId(), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(41, "Instant Loyalty", "The Trident instantly comes back to you", DataComponents.INSTANT_LOYALTY.getId(), 1)
                        .previousTierAndRequirements()
                .build();
    }

    private SkillData aiot() {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("AIOT");

        return SkillData.builder(EquipmentSlot.MAINHAND)
                .tier()
                    .attributeNode(0, miningSpeed(1), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6)
                    .attributeNode(1, attackDamage(1), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                    .attributeNode(2, attackSpeed(1), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 1)
                    .dataComponentNode(3, durability(1), desc.durability(), DURABILITY, 200)
                .tier()
                    .attributeNode(4, miningSpeed(2), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6)
                        .nodeRequirement(0)
                    .attributeNode(5, attackDamage(2), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                        .nodeRequirement(1)
                    .attributeNode(6, attackSpeed(2), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 1)
                        .nodeRequirement(2)
                    .dataComponentNode(7, durability(2), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(3)
                .tier()
                    .enchantmentNode(8, desc, Enchantments.SILK_TOUCH, 1)
                        .notNodeRequirement(9, 40)
                        .previousTierOrRequirements()
                    .enchantmentNode(9, desc, Enchantments.FORTUNE, 3)
                        .notNodeRequirement(8, 40)
                        .previousTierOrRequirements()
                    .dataComponentNode(10, unbreaking(1), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .previousTierOrRequirements()
                    .attributeNode(11, reach(1), desc.reach(), List.of(attr(Attributes.ENTITY_INTERACTION_RANGE), attr(Attributes.BLOCK_INTERACTION_RANGE)), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(12, beheading(1), desc.beheading(), DataComponents.BEHEADING.getId(), 0.1F)
                        .previousTierOrRequirements()
                .tier()
                    .enchantmentNode(13, desc, Enchantments.FIRE_ASPECT, 2)
                        .previousTierOrRequirements()
                    .attributeNode(14, knockback(1), desc.knockback(), attr(Attributes.ATTACK_KNOCKBACK), 1)
                        .previousTierOrRequirements()
                    .attributeNode(15, knockbackResistance(1), desc.knockbackResistance(), attr(Attributes.KNOCKBACK_RESISTANCE), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(16, capturing(1), desc.capturing(), DataComponents.CAPTURING.getId(), 0.01F)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(17, miningSpeed(3), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6)
                        .nodeRequirement(4)
                        .previousTierOrRequirements()
                    .attributeNode(18, attackDamage(3), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                        .nodeRequirement(5)
                        .previousTierOrRequirements()
                    .attributeNode(19, attackSpeed(3), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 1)
                        .nodeRequirement(6)
                        .previousTierOrRequirements()
                    .dataComponentNode(20, durability(3), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(7)
                        .previousTierOrRequirements()
                    .enchantmentNode(21, desc, Enchantments.SWEEPING_EDGE, 3)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(22, AUTO_REPAIR, desc.autoRepair(), DataComponents.AUTO_REPAIR.getId(), 1, 0)
                        .previousTierOrRequirements()
                    .dataComponentNode(23, MINING_3x3, desc.mining3x3(), DataComponents.HAS_3x3.getId(), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(24, VEIN_MINING, desc.veinMining(false), DataComponents.VEIN_MINER.getId(), 1, 0)
                        .subText(VEIN_MINING_SUBTEXT, "#ABABAB")
                        .previousTierOrRequirements()
                    .dataComponentNode(25, AUTO_SMELTING, desc.autoSmelting(), DataComponents.AUTO_SMELT.getId(), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(26, capturing(2), desc.capturing(), DataComponents.CAPTURING.getId(), 0.01F)
                        .nodeRequirement(16)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(27, lifesteal(1), desc.lifesteal(), DataComponents.LIFESTEAL.getId(), 2)
                        .previousTierOrRequirements()
                    .attributeNode(28, knockback(2), desc.knockback(), attr(Attributes.ATTACK_KNOCKBACK), 1)
                        .nodeRequirement(14)
                        .previousTierOrRequirements()
                    .attributeNode(29, knockbackResistance(2), desc.knockbackResistance(), attr(Attributes.KNOCKBACK_RESISTANCE), 1)
                        .nodeRequirement(15)
                        .previousTierOrRequirements()
                    .dataComponentNode(30, unbreaking(2), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .nodeRequirement(10)
                        .previousTierOrRequirements()
                    .attributeNode(31, reach(2), desc.reach(), List.of(attr(Attributes.ENTITY_INTERACTION_RANGE), attr(Attributes.BLOCK_INTERACTION_RANGE)), 1)
                        .nodeRequirement(11)
                        .previousTierOrRequirements()
                    .dataComponentNode(32, beheading(2), desc.beheading(), DataComponents.BEHEADING.getId(), 0.1F)
                        .nodeRequirement(12)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(33, miningSpeed(4), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6)
                        .nodeRequirement(17)
                        .previousTierOrRequirements()
                    .attributeNode(34, attackDamage(4), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                        .nodeRequirement(18)
                        .previousTierOrRequirements()
                    .attributeNode(35, attackSpeed(4), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 1)
                        .nodeRequirement(19)
                        .previousTierOrRequirements()
                    .dataComponentNode(36, durability(4), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(20)
                        .previousTierOrRequirements()
                    .dataComponentNode(37, AUTO_PICKUP, desc.autoPickup(), DataComponents.AUTO_PICKUP.getId(), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(38, capturing(3), desc.capturing(), DataComponents.CAPTURING.getId(), 0.01F)
                        .nodeRequirement(26)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(39, TORCH, desc.torch(), DataComponents.TORCH.getId(), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(40, MODE_SWITCH, desc.mineMode(), DataComponents.MINE_MODE.getId(), 1)
                        .previousTierOrRequirements()
                    .enchantmentNode(41, desc, Enchantments.LOOTING, 3)
                        .previousTierOrRequirements()
                    .dataComponentNode(42, unbreaking(3), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .nodeRequirement(30)
                        .previousTierOrRequirements()
                    .attributeNode(43, reach(3), desc.reach(), List.of(attr(Attributes.ENTITY_INTERACTION_RANGE), attr(Attributes.BLOCK_INTERACTION_RANGE)), 1)
                        .nodeRequirement(31)
                        .previousTierOrRequirements()
                    .dataComponentNode(44, beheading(3), desc.beheading(), DataComponents.BEHEADING.getId(), 0.1F)
                        .nodeRequirement(32)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(45, miningSpeed(5), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6)
                        .nodeRequirement(33)
                        .previousTierOrRequirements()
                    .attributeNode(46, attackDamage(5), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                        .nodeRequirement(34)
                        .previousTierOrRequirements()
                    .attributeNode(47, attackSpeed(5), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 1)
                        .nodeRequirement(35)
                        .previousTierOrRequirements()
                    .dataComponentNode(48, durability(5), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(36)
                        .previousTierOrRequirements()
                .tier()
                    .infiniteAttributeNode(49, miningSpeed(0), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 0.05F)
                        .nodeRequirement(45)
                    .infiniteAttributeNode(50, attackDamage(0), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 0.25F)
                        .nodeRequirement(46)
                    .infiniteAttributeNode(51, attackSpeed(0), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 0.25F)
                        .nodeRequirement(47)
                    .infiniteDataComponentNode(52, durability(0), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(48)
                    .dataComponentNode(53, unbreaking(0), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.01F, 70)
                        .nodeRequirement(42)
                        .previousTierOrRequirements()
                    .infiniteAttributeNode(54, reach(0), desc.reach(), List.of(attr(Attributes.ENTITY_INTERACTION_RANGE), attr(Attributes.BLOCK_INTERACTION_RANGE)), 0.1F)
                        .nodeRequirement(43)
                        .previousTierOrRequirements()
                .tier()
                    .enchantmentNode(55, desc, Enchantments.FORTUNE, 5)
                        .previousTierAndRequirements(9)
                    .enchantmentNode(56, desc, Enchantments.LOOTING, 5)
                        .previousTierAndRequirements(41)
                .build();
    }

    private SkillData backpack() {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("Backpack");

        return SkillData.builder(EquipmentSlot.MAINHAND)
                .tier()
                    .dataComponentNode(0, capacity(1), desc.capacity(), DataComponents.CAPACITY.getId(), 1)
                    .dataComponentNode(1, filterSlots(1), desc.filterSlots(), DataComponents.FILTER_CAPACITY.getId(), 1)
                    .dataComponentNode(2, AUTO_PICKUP, desc.backpackPickup(), DataComponents.BACKPACK_AUTO_PICKUP.getId(), 1)
                .tier()
                    .dataComponentNode(3, capacity(2), desc.capacity(), DataComponents.CAPACITY.getId(), 1)
                        .nodeRequirement(0)
                    .dataComponentNode(4, filterSlots(2), desc.filterSlots(), DataComponents.FILTER_CAPACITY.getId(), 1)
                        .nodeRequirement(1)
                .tier()
                    .dataComponentNode(5, capacity(3), desc.capacity(), DataComponents.CAPACITY.getId(), 1)
                        .nodeRequirement(3)
                    .dataComponentNode(6, filterSlots(3), desc.filterSlots(), DataComponents.FILTER_CAPACITY.getId(), 1)
                        .nodeRequirement(4)
                    .dataComponentNode(7, SORT, desc.sort(), DataComponents.SORT_ENABLED.getId(), 1)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(8, capacity(4), desc.capacity(), DataComponents.CAPACITY.getId(), 1)
                        .nodeRequirement(5)
                    .dataComponentNode(9, filterSlots(4), desc.filterSlots(), DataComponents.FILTER_CAPACITY.getId(), 1)
                        .nodeRequirement(6)
                .tier()
                    .dataComponentNode(10, capacity(5), desc.capacity(), DataComponents.CAPACITY.getId(), 1)
                        .nodeRequirement(8)
                    .dataComponentNode(11, filterSlots(5), desc.filterSlots(), DataComponents.FILTER_CAPACITY.getId(), 1)
                        .nodeRequirement(9)
                    .dataComponentNode(12, STORE, desc.store(), DataComponents.INVENTORY_STORE.getId(), 1)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(13, capacity(6), desc.capacity(), DataComponents.CAPACITY.getId(), 1)
                        .nodeRequirement(10)
                    .dataComponentNode(14, filterSlots(6), desc.filterSlots(), DataComponents.FILTER_CAPACITY.getId(), 1)
                        .nodeRequirement(11)
                    .dataComponentNode(15, COMPRESS, desc.compress(), DataComponents.COMPRESSION_ENABLED.getId(), 1)
                        .previousTierOrRequirements()
                .tier()
                    .infiniteDataComponentNode(16, capacity(0), desc.capacity(), DataComponents.CAPACITY.getId(), 1)
                        .previousTierAndRequirements()
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

    public static ResourceLocation attr(Holder<Attribute> attribute) {
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

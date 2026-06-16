package dev.willyelton.crystal.tools;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.willyelton.crystal.core.common.block.entity.action.ActionType;
import dev.willyelton.crystal.core.common.levelable.LevelableArmor;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import dev.willyelton.crystal.tools.client.particle.quarry.breakblock.QuarryBreakParticleType;
import dev.willyelton.crystal.tools.common.components.DataComponents;
import dev.willyelton.crystal.tools.common.crafting.CrystalAIOTRecipe;
import dev.willyelton.crystal.tools.common.crafting.CrystalElytraRecipe;
import dev.willyelton.crystal.tools.common.crafting.CrystalGeneratorRecipe;
import dev.willyelton.crystal.tools.common.crafting.CrystalQuarryRecipe;
import dev.willyelton.crystal.tools.common.crafting.CrystalShieldTotemRecipe;
import dev.willyelton.crystal.tools.common.entity.CrystalTridentEntity;
import dev.willyelton.crystal.tools.common.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal.tools.common.inventory.container.CrystalFurnaceContainerMenu;
import dev.willyelton.crystal.tools.common.inventory.container.CrystalGeneratorContainerMenu;
import dev.willyelton.crystal.tools.common.inventory.container.CrystalMagnetContainerMenu;
import dev.willyelton.crystal.tools.common.inventory.container.CrystalPedestalContainerMenu;
import dev.willyelton.crystal.tools.common.inventory.container.CrystalQuarryContainerMenu;
import dev.willyelton.crystal.tools.common.inventory.container.PortableGeneratorContainerMenu;
import dev.willyelton.crystal.tools.common.levelable.CrystalBackpack;
import dev.willyelton.crystal.tools.common.levelable.DogCage;
import dev.willyelton.crystal.tools.common.levelable.PortableGenerator;
import dev.willyelton.crystal.tools.common.levelable.armor.CrystalElytra;
import dev.willyelton.crystal.tools.common.levelable.armor.CrystalToolsArmorMaterials;
import dev.willyelton.crystal.tools.common.levelable.block.CrystalFurnaceBlock;
import dev.willyelton.crystal.tools.common.levelable.block.CrystalGeneratorBlock;
import dev.willyelton.crystal.tools.common.levelable.block.CrystalPedestalBlock;
import dev.willyelton.crystal.tools.common.levelable.block.CrystalQuarryBlock;
import dev.willyelton.crystal.tools.common.levelable.block.CrystalQuarryBlockItem;
import dev.willyelton.crystal.tools.common.levelable.block.LevelableBlockItem;
import dev.willyelton.crystal.tools.common.levelable.block.QuarryStabilizer;
import dev.willyelton.crystal.tools.common.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal.tools.common.levelable.block.entity.CrystalGeneratorBlockEntity;
import dev.willyelton.crystal.tools.common.levelable.block.entity.CrystalPedestalBlockEntity;
import dev.willyelton.crystal.tools.common.levelable.block.entity.CrystalQuarryBlockEntity;
import dev.willyelton.crystal.tools.common.levelable.block.entity.action.BlockBreakAction;
import dev.willyelton.crystal.tools.common.levelable.block.entity.action.ChargingAction;
import dev.willyelton.crystal.tools.common.levelable.block.entity.action.MagnetAction;
import dev.willyelton.crystal.tools.common.levelable.block.entity.data.SimpleLevelableContainerData;
import dev.willyelton.crystal.tools.common.levelable.tool.AIOLevelableTool;
import dev.willyelton.crystal.tools.common.levelable.tool.AxeLevelableTool;
import dev.willyelton.crystal.tools.common.levelable.tool.BowLevelableItem;
import dev.willyelton.crystal.tools.common.levelable.tool.CrystalApple;
import dev.willyelton.crystal.tools.common.levelable.tool.CrystalFishingRod;
import dev.willyelton.crystal.tools.common.levelable.tool.CrystalMagnet;
import dev.willyelton.crystal.tools.common.levelable.tool.CrystalRocket;
import dev.willyelton.crystal.tools.common.levelable.tool.CrystalShield;
import dev.willyelton.crystal.tools.common.levelable.tool.CrystalTrident;
import dev.willyelton.crystal.tools.common.levelable.tool.HoeLevelableTool;
import dev.willyelton.crystal.tools.common.levelable.tool.PickaxeLevelableTool;
import dev.willyelton.crystal.tools.common.levelable.tool.ShovelLevelableTool;
import dev.willyelton.crystal.tools.common.levelable.tool.SwordLevelableTool;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

import static dev.willyelton.crystal.core.utils.constants.ApiConstants.ACTION_TYPE_REGISTRY_KEY;
import static dev.willyelton.crystal.core.utils.constants.ApiConstants.baseRl;

// TODO: Rename back if that weird issue with the debug Registration goes away
public class ModRegistration {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CrystalTools.MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CrystalTools.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, CrystalTools.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CrystalTools.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, CrystalTools.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CrystalTools.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(Registries.RECIPE_SERIALIZER, CrystalTools.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, CrystalTools.MODID);
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CrystalTools.MODID);
    public static final DeferredRegister<ActionType<?, ?>> ACTION_TYPES = DeferredRegister.create(ACTION_TYPE_REGISTRY_KEY, ApiConstants.CORE_MOD_ID);

    // Items
    public static final DeferredHolder<Item, Item> CRYSTAL_UPGRADE_SMITHING_TEMPLATE = ITEMS.registerSimpleItem("crystal_upgrade_smithing_template", () -> new Item.Properties().fireResistant());
    public static final DeferredHolder<Item, Item> CRYSTAL_COLLAR = ITEMS.registerSimpleItem("crystal_collar", () -> new Item.Properties().fireResistant().stacksTo(1)
            .component(net.minecraft.core.component.DataComponents.LORE, new ItemLore(List.of(Component.translatable("tooltip.crystal_tools.dog_collar")))));
    public static final DeferredHolder<Item, Item> CRYSTAL_DOG_CAGE = ITEMS.registerItem("crystal_dog_cage", DogCage::new);

    // Tools
    public static final DeferredHolder<Item, PickaxeLevelableTool> CRYSTAL_PICKAXE = ITEMS.registerItem("crystal_pickaxe", PickaxeLevelableTool::new);
    public static final DeferredHolder<Item, AxeLevelableTool> CRYSTAL_AXE = ITEMS.registerItem("crystal_axe", AxeLevelableTool::new);
    public static final DeferredHolder<Item, ShovelLevelableTool> CRYSTAL_SHOVEL = ITEMS.registerItem("crystal_shovel", ShovelLevelableTool::new);
    public static final DeferredHolder<Item, HoeLevelableTool> CRYSTAL_HOE = ITEMS.registerItem("crystal_hoe", HoeLevelableTool::new);
    public static final DeferredHolder<Item, SwordLevelableTool> CRYSTAL_SWORD = ITEMS.registerItem("crystal_sword", SwordLevelableTool::new);
    public static final DeferredHolder<Item, BowLevelableItem> CRYSTAL_BOW = ITEMS.registerItem("crystal_bow", BowLevelableItem::new);
    public static final DeferredHolder<Item, AIOLevelableTool> CRYSTAL_AIOT = ITEMS.registerItem("crystal_aiot", AIOLevelableTool::new);
    public static final DeferredHolder<Item, CrystalRocket> CRYSTAL_ROCKET = ITEMS.registerItem("crystal_rocket", CrystalRocket::new);
    public static final DeferredHolder<Item, CrystalTrident> CRYSTAL_TRIDENT = ITEMS.registerItem("crystal_trident", CrystalTrident::new);
    public static final DeferredHolder<Item, CrystalFishingRod> CRYSTAL_FISHING_ROD = ITEMS.registerItem("crystal_fishing_rod", CrystalFishingRod::new);
    public static final DeferredHolder<Item, CrystalShield> CRYSTAL_SHIELD = ITEMS.registerItem("crystal_shield", CrystalShield::new);
    public static final DeferredHolder<Item, CrystalApple> CRYSTAL_APPLE = ITEMS.registerItem("crystal_apple", CrystalApple::new);
    public static final DeferredHolder<Item, CrystalBackpack> CRYSTAL_BACKPACK = ITEMS.registerItem("crystal_backpack", CrystalBackpack::new);
    public static final DeferredHolder<Item, CrystalMagnet> CRYSTAL_MAGNET = ITEMS.registerItem("crystal_magnet", CrystalMagnet::new);
    public static final DeferredHolder<Item, PortableGenerator> PORTABLE_GENERATOR = ITEMS.registerItem("portable_crystal_generator", PortableGenerator::new);

    // Armor
    public static final DeferredHolder<Item, LevelableArmor> CRYSTAL_HELMET = ITEMS.registerItem("crystal_helmet", (properties) -> new LevelableArmor(properties.humanoidArmor(CrystalToolsArmorMaterials.CRYSTAL, ArmorType.HELMET)));
    public static final DeferredHolder<Item, LevelableArmor> CRYSTAL_CHESTPLATE = ITEMS.registerItem("crystal_chestplate", (properties) -> new LevelableArmor(properties.humanoidArmor(CrystalToolsArmorMaterials.CRYSTAL, ArmorType.CHESTPLATE)));
    public static final DeferredHolder<Item, LevelableArmor> CRYSTAL_LEGGINGS = ITEMS.registerItem("crystal_leggings", (properties) -> new LevelableArmor(properties.humanoidArmor(CrystalToolsArmorMaterials.CRYSTAL, ArmorType.LEGGINGS)));
    public static final DeferredHolder<Item, LevelableArmor> CRYSTAL_BOOTS = ITEMS.registerItem("crystal_boots", (properties) -> new LevelableArmor(properties.humanoidArmor(CrystalToolsArmorMaterials.CRYSTAL, ArmorType.BOOTS)));
    public static final DeferredHolder<Item, CrystalElytra> CRYSTAL_ELYTRA = ITEMS.registerItem("crystal_elytra", CrystalElytra::new);

    // Blocks
    public static final DeferredHolder<Block, CrystalFurnaceBlock> CRYSTAL_FURNACE = BLOCKS.registerBlock("crystal_furnace", CrystalFurnaceBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE).requiresCorrectToolForDrops().strength(3.0F));
    public static final DeferredHolder<Block, CrystalGeneratorBlock> CRYSTAL_GENERATOR = BLOCKS.registerBlock("crystal_generator", CrystalGeneratorBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE).requiresCorrectToolForDrops().strength(3.0F));
    public static final DeferredHolder<Block, CrystalQuarryBlock> CRYSTAL_QUARRY = BLOCKS.registerBlock("crystal_quarry", CrystalQuarryBlock::new, () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(3.0F));
    public static final DeferredHolder<Block, CrystalPedestalBlock> CRYSTAL_PEDESTAL = BLOCKS.registerBlock("crystal_pedestal", CrystalPedestalBlock::new, () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(3.0F));
    public static final DeferredHolder<Block, QuarryStabilizer> QUARRY_STABILIZER = BLOCKS.registerBlock("quarry_stabilizer", QuarryStabilizer::new);

    // Block Items
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_FURNACE_ITEM = ITEMS.registerItem("crystal_furnace", (properties) -> new LevelableBlockItem(CRYSTAL_FURNACE.get(), properties.useBlockDescriptionPrefix()));
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_GENERATOR_ITEM = ITEMS.registerItem("crystal_generator", (properties) -> new LevelableBlockItem(CRYSTAL_GENERATOR.get(), properties.useBlockDescriptionPrefix()));
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_QUARRY_ITEM = ITEMS.registerItem("crystal_quarry", (properties) -> new CrystalQuarryBlockItem(CRYSTAL_QUARRY.get(), properties.useBlockDescriptionPrefix()));
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_PEDESTAL_ITEM = ITEMS.registerSimpleBlockItem(CRYSTAL_PEDESTAL);
    public static final DeferredHolder<Item, BlockItem> QUARRY_STABILIZER_ITEM = ITEMS.registerSimpleBlockItem(QUARRY_STABILIZER);

    // Entities
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalTridentEntity>> CRYSTAL_TRIDENT_ENTITY = ENTITIES.register("crystal_trident", () -> EntityType.Builder.<CrystalTridentEntity>of(CrystalTridentEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(CrystalTools.MODID, "crystal_trident"))));

    // Block Entities
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalFurnaceBlockEntity>> CRYSTAL_FURNACE_BLOCK_ENTITY = BLOCK_ENTITIES.register("crystal_furnace", () -> new BlockEntityType<>(CrystalFurnaceBlockEntity::new, CRYSTAL_FURNACE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalGeneratorBlockEntity>> CRYSTAL_GENERATOR_BLOCK_ENTITY = BLOCK_ENTITIES.register("crystal_generator", () -> new BlockEntityType<>(CrystalGeneratorBlockEntity::new, CRYSTAL_GENERATOR.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalQuarryBlockEntity>> CRYSTAL_QUARRY_BLOCK_ENTITY = BLOCK_ENTITIES.register("crystal_quarry", () -> new BlockEntityType<>(CrystalQuarryBlockEntity::new, CRYSTAL_QUARRY.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalPedestalBlockEntity>> CRYSTAL_PEDESTAL_BLOCK_ENTITY = BLOCK_ENTITIES.register("crystal_pedestal", () -> new BlockEntityType<>(CrystalPedestalBlockEntity::new, CRYSTAL_PEDESTAL.get()));

    // Containers
    public static final DeferredHolder<MenuType<?>, MenuType<CrystalFurnaceContainerMenu>> CRYSTAL_FURNACE_CONTAINER = CONTAINERS.register("crystal_furnace",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new CrystalFurnaceContainerMenu(windowId, inv.player.level(), data.readBlockPos(), inv, new SimpleLevelableContainerData(CrystalFurnaceBlockEntity.DATA_SIZE))));
    public static final DeferredHolder<MenuType<?>, MenuType<CrystalGeneratorContainerMenu>> CRYSTAL_GENERATOR_CONTAINER = CONTAINERS.register("crystal_generator",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new CrystalGeneratorContainerMenu(windowId, inv.player.level(), data.readBlockPos(), inv, new SimpleLevelableContainerData(CrystalGeneratorBlockEntity.DATA_SIZE))));
    public static final DeferredHolder<MenuType<?>, MenuType<PortableGeneratorContainerMenu>> PORTABLE_CRYSTAL_GENERATOR_CONTAINER = CONTAINERS.register("portable_crystal_generator",
            () -> IMenuTypeExtension.create(PortableGeneratorContainerMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<CrystalQuarryContainerMenu>> CRYSTAL_QUARRY_CONTAINER = CONTAINERS.register("crystal_quarry",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new CrystalQuarryContainerMenu(windowId, inv.player.level(), data.readBlockPos(), data.readInt(), inv, new SimpleLevelableContainerData(CrystalQuarryBlockEntity.DATA_SIZE))));
    public static final DeferredHolder<MenuType<?>, MenuType<CrystalBackpackContainerMenu>> CRYSTAL_BACKPACK_CONTAINER = CONTAINERS.register("crystal_backpack",
            () -> IMenuTypeExtension.create(CrystalBackpackContainerMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<CrystalMagnetContainerMenu>> CRYSTAL_MAGNET_CONTAINER = CONTAINERS.register("crystal_magnet",
            () -> IMenuTypeExtension.create(CrystalMagnetContainerMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<CrystalPedestalContainerMenu>> CRYSTAL_PEDESTAL_CONTAINER = CONTAINERS.register("crystal_pedestal",
            () -> IMenuTypeExtension.create(CrystalPedestalContainerMenu::new));

    // Particles
    public static final DeferredHolder<ParticleType<?>, QuarryBreakParticleType> QUARRY_BREAK_PARTICLE = PARTICLES.register("quarry_break_particle", () -> new QuarryBreakParticleType(false));

    // Data Attachments
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> MAGNET_ITEM = ATTACHMENT_TYPES.register("crystal_magnet_attracted", () ->
            AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL.fieldOf("bool"))
                    .build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> WOLF_COLLAR = ATTACHMENT_TYPES.register("wolf_collar", () ->
            AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL.fieldOf("wolfCollar"))
                    .sync(ByteBufCodecs.BOOL)
                    .build());

    // Creative Tabs
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("crystal_tools_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("tab.crystal_tools"))
                    .icon(() -> new ItemStack(CRYSTAL_PICKAXE.get()))
                    .displayItems((featureFlags, output) -> {
                        output.accept(CRYSTAL_UPGRADE_SMITHING_TEMPLATE.get());
                        output.accept(CRYSTAL_COLLAR.get());
                        output.accept(CRYSTAL_DOG_CAGE.get());
                        output.accept(CRYSTAL_APPLE.get());
                        output.accept(CRYSTAL_PICKAXE.get());
                        output.accept(CRYSTAL_AXE.get());
                        output.accept(CRYSTAL_SHOVEL.get());
                        output.accept(CRYSTAL_HOE.get());
                        output.accept(CRYSTAL_SWORD.get());
                        output.accept(CRYSTAL_BOW.get());
                        output.accept(CRYSTAL_AIOT.get());
                        output.accept(CRYSTAL_ROCKET.get());
                        output.accept(CRYSTAL_HELMET.get());
                        output.accept(CRYSTAL_CHESTPLATE.get());
                        output.accept(CRYSTAL_LEGGINGS.get());
                        output.accept(CRYSTAL_BOOTS.get());
                        output.accept(CRYSTAL_ELYTRA.get());
                        output.accept(CRYSTAL_FURNACE_ITEM.get());
                        output.accept(CRYSTAL_GENERATOR_ITEM.get());
                        output.accept(CRYSTAL_QUARRY_ITEM.get());
                        output.accept(QUARRY_STABILIZER_ITEM.get());
                        output.accept(CRYSTAL_PEDESTAL_ITEM.get());
                        output.accept(CRYSTAL_BACKPACK.get());
                        output.accept(CRYSTAL_TRIDENT.get());
                        output.accept(CRYSTAL_FISHING_ROD.get());
                        output.accept(CRYSTAL_SHIELD.get());
                        output.accept(CRYSTAL_MAGNET.get());
                        output.accept(PORTABLE_GENERATOR.get());
                    })
                    .build());

    // Recipes
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrystalElytraRecipe>> CRYSTAL_ELYTRA_RECIPE = RECIPES.register("crystal_elytra_recipe", () -> new RecipeSerializer<>(MapCodec.unit(CrystalElytraRecipe.INSTANCE), StreamCodec.unit(CrystalElytraRecipe.INSTANCE)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrystalAIOTRecipe>> CRYSTAL_AIOT_RECIPE = RECIPES.register("crystal_aiot_recipe", () -> new RecipeSerializer<>(MapCodec.unit(CrystalAIOTRecipe.INSTANCE), StreamCodec.unit(CrystalAIOTRecipe.INSTANCE)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrystalGeneratorRecipe>> CRYSTAL_GENERATOR_RECIPE = RECIPES.register("crystal_generator_recipe", () -> new RecipeSerializer<>(MapCodec.unit(CrystalGeneratorRecipe.INSTANCE), StreamCodec.unit(CrystalGeneratorRecipe.INSTANCE)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrystalQuarryRecipe>> CRYSTAL_QUARRY_RECIPE = RECIPES.register("crystal_quarry_recipe", () -> new RecipeSerializer<>(MapCodec.unit(CrystalQuarryRecipe.INSTANCE), StreamCodec.unit(CrystalQuarryRecipe.INSTANCE)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrystalShieldTotemRecipe>> CRYSTAL_SHIELD_TOTEM_RECIPE = RECIPES.register("crystal_shield_totem_recipe", () -> new RecipeSerializer<>(MapCodec.unit(CrystalShieldTotemRecipe.INSTANCE), StreamCodec.unit(CrystalShieldTotemRecipe.INSTANCE)));

    // Action Types
    public static final DeferredHolder<ActionType<?, ?>, ActionType<BlockBreakAction, ?>> BLOCK_BREAK_ACTION = ACTION_TYPES.register("block_break", () -> new ActionType<>(baseRl("block_break"), BlockBreakAction::new));
    public static final DeferredHolder<ActionType<?, ?>, ActionType<MagnetAction, ?>> MAGNET_ACTION = ACTION_TYPES.register("magnet", () -> new ActionType<>(baseRl("magnet"), MagnetAction::new));
    public static final DeferredHolder<ActionType<?, ?>, ActionType<ChargingAction, ?>> CHARGE_ACTION = ACTION_TYPES.register("charge", () -> new ActionType<>(baseRl("charge"), ChargingAction::new));

    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        ENTITIES.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CONTAINERS.register(modEventBus);
        TABS.register(modEventBus);
        DataComponents.COMPONENTS.register(modEventBus);
        RECIPES.register(modEventBus);
        PARTICLES.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);
    }
}

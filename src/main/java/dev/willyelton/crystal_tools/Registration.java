package dev.willyelton.crystal_tools;

import dev.willyelton.crystal_tools.client.particle.quarry.breakblock.QuarryBreakParticleType;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.crafting.CrystalAIOTRecipe;
import dev.willyelton.crystal_tools.common.crafting.CrystalElytraRecipe;
import dev.willyelton.crystal_tools.common.crafting.CrystalGeneratorRecipe;
import dev.willyelton.crystal_tools.common.crafting.CrystalQuarryRecipe;
import dev.willyelton.crystal_tools.common.crafting.CrystalShieldTotemRecipe;
import dev.willyelton.crystal_tools.common.crafting.ItemDisabledCondition;
import dev.willyelton.crystal_tools.common.entity.CrystalTridentEntity;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalFurnaceContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalGeneratorContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalQuarryContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.CrystalBackpack;
import dev.willyelton.crystal_tools.common.levelable.armor.CrystalElytra;
import dev.willyelton.crystal_tools.common.levelable.armor.LevelableArmor;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalFurnaceBlock;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalGeneratorBlock;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalQuarryBlock;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalQuarryBlockItem;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalTorch;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalWallTorch;
import dev.willyelton.crystal_tools.common.levelable.block.LevelableBlockItem;
import dev.willyelton.crystal_tools.common.levelable.block.QuarryStabilizer;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalGeneratorBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalQuarryBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.SimpleLevelableContainerData;
import dev.willyelton.crystal_tools.common.levelable.tool.AIOLevelableTool;
import dev.willyelton.crystal_tools.common.levelable.tool.AxeLevelableTool;
import dev.willyelton.crystal_tools.common.levelable.tool.BowLevelableItem;
import dev.willyelton.crystal_tools.common.levelable.tool.CrystalApple;
import dev.willyelton.crystal_tools.common.levelable.tool.CrystalFishingRod;
import dev.willyelton.crystal_tools.common.levelable.tool.CrystalRocket;
import dev.willyelton.crystal_tools.common.levelable.tool.CrystalShield;
import dev.willyelton.crystal_tools.common.levelable.tool.CrystalTrident;
import dev.willyelton.crystal_tools.common.levelable.tool.HoeLevelableTool;
import dev.willyelton.crystal_tools.common.levelable.tool.PickaxeLevelableTool;
import dev.willyelton.crystal_tools.common.levelable.tool.ShovelLevelableTool;
import dev.willyelton.crystal_tools.common.levelable.tool.SwordLevelableTool;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class Registration {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CrystalTools.MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CrystalTools.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, CrystalTools.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CrystalTools.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, CrystalTools.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CrystalTools.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(Registries.RECIPE_SERIALIZER, CrystalTools.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, CrystalTools.MODID);

    // Items
    public static final DeferredHolder<Item, Item> CRYSTAL = ITEMS.registerSimpleItem("crystal");
    public static final DeferredHolder<Item, Item> NETHERITE_STICK = ITEMS.registerSimpleItem("netherite_stick", new Item.Properties().fireResistant());
    public static final DeferredHolder<Item, CrystalApple> CRYSTAL_APPLE = ITEMS.registerItem("crystal_apple", CrystalApple::new);
    public static final DeferredHolder<Item, CrystalBackpack> CRYSTAL_BACKPACK = ITEMS.registerItem("crystal_backpack", CrystalBackpack::new);

    // Tools
    public static final DeferredHolder<Item, PickaxeLevelableTool> CRYSTAL_PICKAXE = ITEMS.registerItem("crystal_pickaxe", PickaxeLevelableTool::new);
    public static final DeferredHolder<Item, AxeLevelableTool> CRYSTAL_AXE = ITEMS.registerItem("crystal_axe", AxeLevelableTool::new);
    public static final DeferredHolder<Item, ShovelLevelableTool> CRYSTAL_SHOVEL = ITEMS.registerItem("crystal_shovel", ShovelLevelableTool::new);
    public static final DeferredHolder<Item, HoeLevelableTool> CRYSTAL_HOE = ITEMS.registerItem("crystal_hoe", HoeLevelableTool::new);
    public static final DeferredHolder<Item, SwordLevelableTool> CRYSTAL_SWORD =  ITEMS.registerItem("crystal_sword", SwordLevelableTool::new);
    public static final DeferredHolder<Item, BowLevelableItem> CRYSTAL_BOW = ITEMS.registerItem("crystal_bow", BowLevelableItem::new);
    public static final DeferredHolder<Item, AIOLevelableTool> CRYSTAL_AIOT = ITEMS.registerItem("crystal_aiot", AIOLevelableTool::new);
    public static final DeferredHolder<Item, CrystalRocket> CRYSTAL_ROCKET = ITEMS.registerItem("crystal_rocket", CrystalRocket::new);
    public static final DeferredHolder<Item, CrystalTrident> CRYSTAL_TRIDENT = ITEMS.registerItem("crystal_trident", CrystalTrident::new);
    public static final DeferredHolder<Item, CrystalFishingRod> CRYSTAL_FISHING_ROD = ITEMS.registerItem("crystal_fishing_rod", CrystalFishingRod::new);
    public static final DeferredHolder<Item, CrystalShield> CRYSTAL_SHIELD = ITEMS.registerItem("crystal_shield", CrystalShield::new);

    // Armor
    public static final DeferredHolder<Item, LevelableArmor> CRYSTAL_HELMET = ITEMS.registerItem("crystal_helmet", (properties) -> new LevelableArmor(properties, ArmorType.HELMET));
    public static final DeferredHolder<Item, LevelableArmor> CRYSTAL_CHESTPLATE = ITEMS.registerItem("crystal_chestplate", (properties) -> new LevelableArmor(properties, ArmorType.CHESTPLATE));
    public static final DeferredHolder<Item, LevelableArmor> CRYSTAL_LEGGINGS = ITEMS.registerItem("crystal_leggings", (properties) -> new LevelableArmor(properties,  ArmorType.LEGGINGS));
    public static final DeferredHolder<Item, LevelableArmor> CRYSTAL_BOOTS = ITEMS.registerItem("crystal_boots", (properties) -> new LevelableArmor(properties,  ArmorType.BOOTS));
    public static final DeferredHolder<Item, CrystalElytra> CRYSTAL_ELYTRA = ITEMS.registerItem("crystal_elytra", CrystalElytra::new);

    // Blocks
    public static final DeferredHolder<Block, DropExperienceBlock> CRYSTAL_ORE = BLOCKS.registerBlock("crystal_ore", (properties) -> new DropExperienceBlock(UniformInt.of(3, 7), properties), Block.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
    public static final DeferredHolder<Block, DropExperienceBlock> CRYSTAL_DEEPSLATE_ORE = BLOCKS.registerBlock("crystal_deepslate_ore", (properties) -> new DropExperienceBlock(UniformInt.of(3, 7), properties), Block.Properties.ofFullCopy(Blocks.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE));
    public static final DeferredHolder<Block, Block> CRYSTAL_BLOCK = BLOCKS.registerBlock("crystal_block", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
    public static final DeferredHolder<Block, CrystalFurnaceBlock> CRYSTAL_FURNACE = BLOCKS.registerBlock("crystal_furnace", CrystalFurnaceBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE).requiresCorrectToolForDrops().strength(3.0F));
    public static final DeferredHolder<Block, CrystalGeneratorBlock> CRYSTAL_GENERATOR = BLOCKS.registerBlock("crystal_generator", CrystalGeneratorBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE).requiresCorrectToolForDrops().strength(3.0F));
    public static final DeferredHolder<Block, CrystalQuarryBlock> CRYSTAL_QUARRY = BLOCKS.registerBlock("crystal_quarry", CrystalQuarryBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(3.0F));
    public static final DeferredHolder<Block, CrystalTorch> CRYSTAL_TORCH = BLOCKS.registerBlock("crystal_torch", CrystalTorch::new);
    public static final DeferredHolder<Block, CrystalWallTorch> CRYSTAL_WALL_TORCH = BLOCKS.registerBlock("crystal_wall_torch", CrystalWallTorch::new, BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_WALL_TORCH));
    public static final DeferredHolder<Block, QuarryStabilizer> QUARRY_STABILIZER = BLOCKS.registerBlock("quarry_stabilizer", QuarryStabilizer::new);

    // Block Items
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_ORE_ITEM = ITEMS.registerSimpleBlockItem(CRYSTAL_ORE);
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_DEEPSLATE_ORE_ITEM = ITEMS.registerSimpleBlockItem(CRYSTAL_DEEPSLATE_ORE);
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(CRYSTAL_BLOCK);
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_FURNACE_ITEM = ITEMS.registerItem("crystal_furnace", (properties) -> new LevelableBlockItem(CRYSTAL_FURNACE.get(), properties.useBlockDescriptionPrefix()));
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_GENERATOR_ITEM = ITEMS.registerItem("crystal_generator", (properties) -> new LevelableBlockItem(CRYSTAL_GENERATOR.get(), properties.useBlockDescriptionPrefix()));
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_QUARRY_ITEM = ITEMS.registerItem("crystal_quarry", (properties) -> new CrystalQuarryBlockItem(CRYSTAL_QUARRY.get(), properties.useBlockDescriptionPrefix()));
    public static final DeferredHolder<Item, BlockItem> QUARRY_STABILIZER_ITEM = ITEMS.registerSimpleBlockItem(QUARRY_STABILIZER);
    public static final DeferredHolder<Item, StandingAndWallBlockItem> CRYSTAL_TORCH_ITEM = ITEMS.registerItem("crystal_torch", (properties) -> new StandingAndWallBlockItem(CRYSTAL_TORCH.get(), CRYSTAL_WALL_TORCH.get(), Direction.DOWN, properties.useBlockDescriptionPrefix()));

    // Entities
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalTridentEntity>> CRYSTAL_TRIDENT_ENTITY = ENTITIES.register("crystal_trident", () -> EntityType.Builder.<CrystalTridentEntity>of(CrystalTridentEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "crystal_trident"))));

    // Block Entities
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalFurnaceBlockEntity>> CRYSTAL_FURNACE_BLOCK_ENTITY = BLOCK_ENTITIES.register("crystal_furnace", () -> new BlockEntityType<>(CrystalFurnaceBlockEntity::new, CRYSTAL_FURNACE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalGeneratorBlockEntity>> CRYSTAL_GENERATOR_BLOCK_ENTITY = BLOCK_ENTITIES.register("crystal_generator", () -> new BlockEntityType<>(CrystalGeneratorBlockEntity::new, CRYSTAL_GENERATOR.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalQuarryBlockEntity>> CRYSTAL_QUARRY_BLOCK_ENTITY = BLOCK_ENTITIES.register("crystal_quarry", () -> new BlockEntityType<>(CrystalQuarryBlockEntity::new, CRYSTAL_QUARRY.get()));

    // Containers
    public static final DeferredHolder<MenuType<?>, MenuType<CrystalFurnaceContainerMenu>> CRYSTAL_FURNACE_CONTAINER = CONTAINERS.register("crystal_furnace",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new CrystalFurnaceContainerMenu(windowId, inv.player.level(), data.readBlockPos(), inv, new SimpleLevelableContainerData(CrystalFurnaceBlockEntity.DATA_SIZE))));
    public static final DeferredHolder<MenuType<?>, MenuType<CrystalGeneratorContainerMenu>> CRYSTAL_GENERATOR_CONTAINER = CONTAINERS.register("crystal_generator",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new CrystalGeneratorContainerMenu(windowId, inv.player.level(), data.readBlockPos(), inv, new SimpleLevelableContainerData(CrystalGeneratorBlockEntity.DATA_SIZE))));
    public static final DeferredHolder<MenuType<?>, MenuType<CrystalQuarryContainerMenu>> CRYSTAL_QUARRY_CONTAINER = CONTAINERS.register("crystal_quarry",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new CrystalQuarryContainerMenu(windowId, inv.player.level(), data.readBlockPos(), data.readInt(), inv, new SimpleLevelableContainerData(CrystalQuarryBlockEntity.DATA_SIZE))));
    public static final DeferredHolder<MenuType<?>, MenuType<CrystalBackpackContainerMenu>> CRYSTAL_BACKPACK_CONTAINER = CONTAINERS.register("crystal_backpack",
            () -> IMenuTypeExtension.create(CrystalBackpackContainerMenu::new));

    // Particles
    public static final DeferredHolder<ParticleType<?>, QuarryBreakParticleType> QUARRY_BREAK_PARTICLE = PARTICLES.register("quary_break_particle", () -> new QuarryBreakParticleType(false));

    // Creative Tabs
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("crystal_tools_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("tab.crystal_tools"))
                    .icon(() -> new ItemStack(CRYSTAL_PICKAXE.get()))
                    .displayItems((featureFlags, output) -> {
                        output.accept(CRYSTAL.get());
                        output.accept(NETHERITE_STICK.get());
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
                        output.accept(CRYSTAL_ORE_ITEM.get());
                        output.accept(CRYSTAL_DEEPSLATE_ORE_ITEM.get());
                        output.accept(CRYSTAL_BLOCK_ITEM.get());
                        output.accept(CRYSTAL_FURNACE_ITEM.get());
                        output.accept(CRYSTAL_GENERATOR_ITEM.get());
                        output.accept(CRYSTAL_QUARRY_ITEM.get());
                        output.accept(QUARRY_STABILIZER_ITEM.get());
                        output.accept(CRYSTAL_TORCH_ITEM.get());
                        output.accept(CRYSTAL_BACKPACK.get());
                        output.accept(CRYSTAL_TRIDENT.get());
                        output.accept(CRYSTAL_FISHING_ROD.get());
                        output.accept(CRYSTAL_SHIELD.get());
                    })
                    .build());

    // Recipes
    public static final DeferredHolder<RecipeSerializer<?>, CustomRecipe.Serializer<CrystalElytraRecipe>> CRYSTAL_ELYTRA_RECIPE = RECIPES.register("crystal_elytra_recipe", () -> new CustomRecipe.Serializer<>(CrystalElytraRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, CustomRecipe.Serializer<CrystalAIOTRecipe>> CRYSTAL_AIOT_RECIPE = RECIPES.register("crystal_aiot_recipe", () -> new CustomRecipe.Serializer<>(CrystalAIOTRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, CustomRecipe.Serializer<CrystalGeneratorRecipe>> CRYSTAL_GENERATOR_RECIPE = RECIPES.register("crystal_generator_recipe", () -> new CustomRecipe.Serializer<>(CrystalGeneratorRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, CustomRecipe.Serializer<CrystalQuarryRecipe>> CRYSTAL_QUARRY_RECIPE = RECIPES.register("crystal_quarry_recipe", () -> new CustomRecipe.Serializer<>(CrystalQuarryRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, CustomRecipe.Serializer<CrystalShieldTotemRecipe>> CRYSTAL_SHIELD_TOTEM_RECIPE = RECIPES.register("crystal_shield_totem_recipe", () -> new CustomRecipe.Serializer<>(CrystalShieldTotemRecipe::new));

    public static void init(IEventBus modEventBus) {
        // Conditions
        Registry.register(NeoForgeRegistries.CONDITION_SERIALIZERS, ItemDisabledCondition.NAME, ItemDisabledCondition.CODEC);

        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        ENTITIES.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CONTAINERS.register(modEventBus);
        TABS.register(modEventBus);
        DataComponents.COMPONENTS.register(modEventBus);
        RECIPES.register(modEventBus);
        PARTICLES.register(modEventBus);
    }
}

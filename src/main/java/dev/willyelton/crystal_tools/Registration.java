package dev.willyelton.crystal_tools;

import dev.willyelton.crystal_tools.entity.CrystalTridentEntity;
import dev.willyelton.crystal_tools.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.inventory.container.CrystalFurnaceContainerMenu;
import dev.willyelton.crystal_tools.levelable.CrystalBackpack;
import dev.willyelton.crystal_tools.levelable.armor.CrystalElytra;
import dev.willyelton.crystal_tools.levelable.armor.LevelableArmor;
import dev.willyelton.crystal_tools.levelable.block.CrystalFurnaceBlock;
import dev.willyelton.crystal_tools.levelable.block.CrystalTorch;
import dev.willyelton.crystal_tools.levelable.block.CrystalWallTorch;
import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.levelable.tool.*;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrystalTools.MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CrystalTools.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CrystalTools.MODID);

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CrystalTools.MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CrystalTools.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CrystalTools.MODID);

    // Items
    public static final RegistryObject<Item> CRYSTAL = ITEMS.register("crystal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NETHERITE_STICK = ITEMS.register("netherite_stick", () -> new Item(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> CRYSTAL_APPLE = ITEMS.register("crystal_apple", CrystalApple::new);
    public static final RegistryObject<Item> CRYSTAL_BACKPACK = ITEMS.register("crystal_backpack", CrystalBackpack::new);

    // Tools
    public static final RegistryObject<Item> CRYSTAL_PICKAXE = ITEMS.register("crystal_pickaxe", PickaxeLevelableTool::new);
    public static final RegistryObject<Item> CRYSTAL_AXE = ITEMS.register("crystal_axe", AxeLevelableTool::new);
    public static final RegistryObject<Item> CRYSTAL_SHOVEL = ITEMS.register("crystal_shovel", ShovelLevelableTool::new);
    public static final RegistryObject<Item> CRYSTAL_HOE = ITEMS.register("crystal_hoe", HoeLevelableTool::new);
    public static final RegistryObject<Item> CRYSTAL_SWORD =  ITEMS.register("crystal_sword", SwordLevelableTool::new);
    public static final RegistryObject<Item> CRYSTAL_BOW = ITEMS.register("crystal_bow", BowLevelableItem::new);
    public static final RegistryObject<Item> CRYSTAL_AIOT = ITEMS.register("crystal_aiot", AIOLevelableTool::new);
    public static final RegistryObject<Item> CRYSTAL_ROCKET = ITEMS.register("crystal_rocket", CrystalRocket::new);
    public static final RegistryObject<Item> CRYSTAL_TRIDENT = ITEMS.register("crystal_trident", CrystalTrident::new);

    // Armor
    public static final RegistryObject<Item> CRYSTAL_HELMET = ITEMS.register("crystal_helmet", () -> new LevelableArmor("helmet", ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> CRYSTAL_CHESTPLATE = ITEMS.register("crystal_chestplate", () -> new LevelableArmor("chestplate", ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> CRYSTAL_LEGGINGS = ITEMS.register("crystal_leggings", () -> new LevelableArmor("leggings",  ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> CRYSTAL_BOOTS = ITEMS.register("crystal_boots", () -> new LevelableArmor("boots",  ArmorItem.Type.BOOTS));
    public static final RegistryObject<Item> CRYSTAL_ELYTRA = ITEMS.register("crystal_elytra", () -> new CrystalElytra(new Item.Properties().durability(1000)));

    // Blocks
    public static final RegistryObject<Block> CRYSTAL_ORE = BLOCKS.register("crystal_ore", () -> new DropExperienceBlock(Block.Properties.copy(Blocks.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), UniformInt.of(3, 7)));
    public static final RegistryObject<Block> CRYSTAL_DEEPSLATE_ORE = BLOCKS.register("crystal_deepslate_ore", () -> new DropExperienceBlock(Block.Properties.copy(Blocks.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(3, 7)));
    public static final RegistryObject<Block> CRYSTAL_BLOCK = BLOCKS.register("crystal_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> CRYSTAL_FURNACE = BLOCKS.register("crystal_furnace", () -> new CrystalFurnaceBlock(BlockBehaviour.Properties.copy(Blocks.FURNACE).requiresCorrectToolForDrops().strength(3.0F)));
    public static final RegistryObject<Block> CRYSTAL_TORCH = BLOCKS.register("crystal_torch", CrystalTorch::new);
    public static final RegistryObject<Block> CRYSTAL_WALL_TORCH = BLOCKS.register("crystal_wall_torch", CrystalWallTorch::new);

    // Block Items
    public static final RegistryObject<Item> CRYSTAL_ORE_ITEM = ITEMS.register("crystal_ore", () -> new BlockItem(CRYSTAL_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_DEEPSLATE_ORE_ITEM = ITEMS.register("crystal_deepslate_ore", () -> new BlockItem(CRYSTAL_DEEPSLATE_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_BLOCK_ITEM = ITEMS.register("crystal_block", () -> new BlockItem(CRYSTAL_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_FURNACE_ITEM = ITEMS.register("crystal_furnace", () -> new BlockItem(CRYSTAL_FURNACE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_TORCH_ITEM = ITEMS.register("crystal_torch", () -> new StandingAndWallBlockItem(CRYSTAL_TORCH.get(), CRYSTAL_WALL_TORCH.get(), new Item.Properties(), Direction.DOWN));

    // Entities
    public static final RegistryObject<EntityType<CrystalTridentEntity>> CRYSTAL_TRIDENT_ENTITY = ENTITIES.register("crystal_trident", () -> EntityType.Builder.of(CrystalTridentEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build("crystal_tools:crystal_trident"));

    // Block Entities
    public static final RegistryObject<BlockEntityType<CrystalFurnaceBlockEntity>> CRYSTAL_FURNACE_BLOCK_ENTITY = BLOCK_ENTITIES.register("crystal_furnace", () -> BlockEntityType.Builder.of(CrystalFurnaceBlockEntity::new, CRYSTAL_FURNACE.get()).build(null));

    // Containers
    public static final RegistryObject<MenuType<CrystalFurnaceContainerMenu>> CRYSTAL_FURNACE_CONTAINER = CONTAINERS.register("crystal_furnace",
            () -> IForgeMenuType.create((windowId, inv, data) -> new CrystalFurnaceContainerMenu(windowId, inv.player.level(), data.readBlockPos(), inv, new SimpleContainerData(CrystalFurnaceBlockEntity.DATA_SIZE))));
    public static final RegistryObject<MenuType<CrystalBackpackContainerMenu>> CRYSTAL_BACKPACK_CONTAINER = CONTAINERS.register("crystal_backpack",
            () -> IForgeMenuType.create(CrystalBackpackContainerMenu::new));

    // Tags
    public static final TagKey<EntityType<?>> ENTITY_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CrystalTools.MODID, "entity_blacklist"));

    // Creative Tabs
    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("crystal_tools_tab", () ->
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
                        output.accept(CRYSTAL_TORCH_ITEM.get());
                        output.accept(CRYSTAL_BACKPACK.get());
                    })
                    .build());


    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        ENTITIES.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CONTAINERS.register(modEventBus);
        TABS.register(modEventBus);
    }
}

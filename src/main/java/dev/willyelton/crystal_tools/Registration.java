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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Registration {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, CrystalTools.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, CrystalTools.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, CrystalTools.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CrystalTools.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, CrystalTools.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CrystalTools.MODID);

    // Items
    public static final DeferredHolder<Item, Item> CRYSTAL = ITEMS.register("crystal", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> NETHERITE_STICK = ITEMS.register("netherite_stick", () -> new Item(new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, CrystalApple> CRYSTAL_APPLE = ITEMS.register("crystal_apple", CrystalApple::new);
    public static final DeferredHolder<Item, CrystalBackpack> CRYSTAL_BACKPACK = ITEMS.register("crystal_backpack", CrystalBackpack::new);

    // Tools
    public static final DeferredHolder<Item, PickaxeLevelableTool> CRYSTAL_PICKAXE = ITEMS.register("crystal_pickaxe", PickaxeLevelableTool::new);
    public static final DeferredHolder<Item, AxeLevelableTool> CRYSTAL_AXE = ITEMS.register("crystal_axe", AxeLevelableTool::new);
    public static final DeferredHolder<Item, ShovelLevelableTool> CRYSTAL_SHOVEL = ITEMS.register("crystal_shovel", ShovelLevelableTool::new);
    public static final DeferredHolder<Item, HoeLevelableTool> CRYSTAL_HOE = ITEMS.register("crystal_hoe", HoeLevelableTool::new);
    public static final DeferredHolder<Item, SwordLevelableTool> CRYSTAL_SWORD =  ITEMS.register("crystal_sword", SwordLevelableTool::new);
    public static final DeferredHolder<Item, BowLevelableItem> CRYSTAL_BOW = ITEMS.register("crystal_bow", BowLevelableItem::new);
    public static final DeferredHolder<Item, AIOLevelableTool> CRYSTAL_AIOT = ITEMS.register("crystal_aiot", AIOLevelableTool::new);
    public static final DeferredHolder<Item, CrystalRocket> CRYSTAL_ROCKET = ITEMS.register("crystal_rocket", CrystalRocket::new);
    public static final DeferredHolder<Item, CrystalTrident> CRYSTAL_TRIDENT = ITEMS.register("crystal_trident", CrystalTrident::new);
    public static final DeferredHolder<Item, CrystalFishingRod> CRYSTAL_FISHING_ROD = ITEMS.register("crystal_fishing_rod", CrystalFishingRod::new);

    // Armor
    public static final DeferredHolder<Item, LevelableArmor> CRYSTAL_HELMET = ITEMS.register("crystal_helmet", () -> new LevelableArmor("helmet", ArmorItem.Type.HELMET));
    public static final DeferredHolder<Item, LevelableArmor> CRYSTAL_CHESTPLATE = ITEMS.register("crystal_chestplate", () -> new LevelableArmor("chestplate", ArmorItem.Type.CHESTPLATE));
    public static final DeferredHolder<Item, LevelableArmor> CRYSTAL_LEGGINGS = ITEMS.register("crystal_leggings", () -> new LevelableArmor("leggings",  ArmorItem.Type.LEGGINGS));
    public static final DeferredHolder<Item, LevelableArmor> CRYSTAL_BOOTS = ITEMS.register("crystal_boots", () -> new LevelableArmor("boots",  ArmorItem.Type.BOOTS));
    public static final DeferredHolder<Item, CrystalElytra> CRYSTAL_ELYTRA = ITEMS.register("crystal_elytra", () -> new CrystalElytra(new Item.Properties().durability(1000)));

    // Blocks
    public static final DeferredHolder<Block, DropExperienceBlock> CRYSTAL_ORE = BLOCKS.register("crystal_ore", () -> new DropExperienceBlock(UniformInt.of(3, 7), Block.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final DeferredHolder<Block, DropExperienceBlock> CRYSTAL_DEEPSLATE_ORE = BLOCKS.register("crystal_deepslate_ore", () -> new DropExperienceBlock(UniformInt.of(3, 7), Block.Properties.ofFullCopy(Blocks.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
    public static final DeferredHolder<Block, Block> CRYSTAL_BLOCK = BLOCKS.register("crystal_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
    public static final DeferredHolder<Block, CrystalFurnaceBlock> CRYSTAL_FURNACE = BLOCKS.register("crystal_furnace", () -> new CrystalFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE).requiresCorrectToolForDrops().strength(3.0F)));
    public static final DeferredHolder<Block, CrystalTorch> CRYSTAL_TORCH = BLOCKS.register("crystal_torch", () -> new CrystalTorch());
    public static final DeferredHolder<Block, CrystalWallTorch> CRYSTAL_WALL_TORCH = BLOCKS.register("crystal_wall_torch", CrystalWallTorch::new);

    // Block Items
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_ORE_ITEM = ITEMS.register("crystal_ore", () -> new BlockItem(CRYSTAL_ORE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_DEEPSLATE_ORE_ITEM = ITEMS.register("crystal_deepslate_ore", () -> new BlockItem(CRYSTAL_DEEPSLATE_ORE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_BLOCK_ITEM = ITEMS.register("crystal_block", () -> new BlockItem(CRYSTAL_BLOCK.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_FURNACE_ITEM = ITEMS.register("crystal_furnace", () -> new BlockItem(CRYSTAL_FURNACE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, StandingAndWallBlockItem> CRYSTAL_TORCH_ITEM = ITEMS.register("crystal_torch", () -> new StandingAndWallBlockItem(CRYSTAL_TORCH.get(), CRYSTAL_WALL_TORCH.get(), new Item.Properties(), Direction.DOWN));

    // Entities
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalTridentEntity>> CRYSTAL_TRIDENT_ENTITY = ENTITIES.register("crystal_trident", () -> EntityType.Builder.<CrystalTridentEntity>of(CrystalTridentEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build("crystal_tools:crystal_trident"));

    // Block Entities
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalFurnaceBlockEntity>> CRYSTAL_FURNACE_BLOCK_ENTITY = BLOCK_ENTITIES.register("crystal_furnace", () -> BlockEntityType.Builder.of(CrystalFurnaceBlockEntity::new, CRYSTAL_FURNACE.get()).build(null));

    // Containers
    public static final DeferredHolder<MenuType<?>, MenuType<CrystalFurnaceContainerMenu>> CRYSTAL_FURNACE_CONTAINER = CONTAINERS.register("crystal_furnace",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new CrystalFurnaceContainerMenu(windowId, inv.player.level(), data.readBlockPos(), inv, new SimpleContainerData(CrystalFurnaceBlockEntity.DATA_SIZE))));
    public static final DeferredHolder<MenuType<?>, MenuType<CrystalBackpackContainerMenu>> CRYSTAL_BACKPACK_CONTAINER = CONTAINERS.register("crystal_backpack",
            () -> IMenuTypeExtension.create(CrystalBackpackContainerMenu::new));

    // Tags
    public static final TagKey<EntityType<?>> ENTITY_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CrystalTools.MODID, "entity_blacklist"));

    // Conditions

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
                        output.accept(CRYSTAL_TORCH_ITEM.get());
                        output.accept(CRYSTAL_BACKPACK.get());
                        output.accept(CRYSTAL_TRIDENT.get());
                        output.accept(CRYSTAL_FISHING_ROD.get());
                    })
                    .build());


    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        ENTITIES.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CONTAINERS.register(modEventBus);
        TABS.register(modEventBus);
    }
}

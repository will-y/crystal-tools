package dev.willyelton.crystal_tools.levelable.block;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.levelable.block.container.CrystalFurnaceContainer;
import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
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

public class ModBlocks {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CrystalTools.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrystalTools.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CrystalTools.MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CrystalTools.MODID);

    // Blocks
    public static final RegistryObject<Block> CRYSTAL_ORE = BLOCKS.register("crystal_ore", () -> new DropExperienceBlock(Block.Properties.copy(Blocks.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), UniformInt.of(3, 7)));
    public static final RegistryObject<Block> CRYSTAL_DEEPSLATE_ORE = BLOCKS.register("crystal_deepslate_ore", () -> new DropExperienceBlock(Block.Properties.copy(Blocks.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(3, 7)));
    public static final RegistryObject<Block> CRYSTAL_BLOCK = BLOCKS.register("crystal_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));

    // Machines
    public static final RegistryObject<Block> CRYSTAL_FURNACE = BLOCKS.register("crystal_furnace", () -> new CrystalFurnaceBlock(BlockBehaviour.Properties.copy(Blocks.FURNACE).requiresCorrectToolForDrops().strength(3.0F)));
    public static final RegistryObject<BlockEntityType<CrystalFurnaceBlockEntity>> CRYSTAL_FURNACE_BLOCK_ENTITY = BLOCK_ENTITIES.register("crystal_furnace", () -> BlockEntityType.Builder.of(CrystalFurnaceBlockEntity::new, CRYSTAL_FURNACE.get()).build(null));
    public static final RegistryObject<MenuType<CrystalFurnaceContainer>> CRYSTAL_FURNACE_CONTAINER = CONTAINERS.register("crystal_furnace",
            () -> IForgeMenuType.create((windowId, inv, data) -> new CrystalFurnaceContainer(windowId, inv.player.level(), data.readBlockPos(), inv, new SimpleContainerData(CrystalFurnaceBlockEntity.DATA_SIZE))));
    public static final RegistryObject<Block> CRYSTAL_TORCH = BLOCKS.register("crystal_torch", CrystalTorch::new);
    public static final RegistryObject<Block> CRYSTAL_WALL_TORCH = BLOCKS.register("crystal_wall_torch", CrystalWallTorch::new);


    // Block Items
    public static final RegistryObject<Item> CRYSTAL_ORE_ITEM = ITEMS.register("crystal_ore", () -> new BlockItem(CRYSTAL_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_DEEPSLATE_ORE_ITEM = ITEMS.register("crystal_deepslate_ore", () -> new BlockItem(CRYSTAL_DEEPSLATE_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_BLOCK_ITEM = ITEMS.register("crystal_block", () -> new BlockItem(CRYSTAL_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_FURNACE_ITEM = ITEMS.register("crystal_furnace", () -> new BlockItem(CRYSTAL_FURNACE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_TORCH_ITEM = ITEMS.register("crystal_torch", () -> new StandingAndWallBlockItem(CRYSTAL_TORCH.get(), CRYSTAL_WALL_TORCH.get(), new Item.Properties(), Direction.DOWN));

    public static void initBlocks() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CONTAINERS.register(modEventBus);
    }
}

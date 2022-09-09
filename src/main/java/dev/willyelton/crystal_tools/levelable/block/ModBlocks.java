package dev.willyelton.crystal_tools.levelable.block;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.CreativeTabs;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CrystalTools.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrystalTools.MODID);

    // Blocks
    // public static final Block DIAMOND_ORE = register("diamond_ore", new OreBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), UniformInt.of(3, 7)));
    public static final RegistryObject<Block> CRYSTAL_ORE = BLOCKS.register("crystal_ore", () -> new DropExperienceBlock(Block.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), UniformInt.of(3, 7)));

    // public static final Block DEEPSLATE_DIAMOND_ORE = register("deepslate_diamond_ore", new OreBlock(BlockBehaviour.Properties.copy(DIAMOND_ORE).color(MaterialColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(3, 7)));
    public static final RegistryObject<Block> CRYSTAL_DEEPSLATE_ORE = BLOCKS.register("crystal_deepslate_ore", () -> new DropExperienceBlock(Block.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(4.5F, 3.0F).color(MaterialColor.DEEPSLATE).sound(SoundType.DEEPSLATE), UniformInt.of(3, 7)));

    // public static final Block DIAMOND_BLOCK = register("diamond_block", new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.DIAMOND).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> CRYSTAL_BLOCK = BLOCKS.register("crystal_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.NONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));


    // Block Items
    public static final RegistryObject<Item> CRYSTAL_ORE_ITEM = ITEMS.register("crystal_ore", () -> new BlockItem(CRYSTAL_ORE.get(), new Item.Properties().tab(CreativeTabs.CRYSTAL_TOOLS_TAB)));
    public static final RegistryObject<Item> CRYSTAL_DEEPSLATE_ORE_ITEM = ITEMS.register("crystal_deepslate_ore", () -> new BlockItem(CRYSTAL_DEEPSLATE_ORE.get(), new Item.Properties().tab(CreativeTabs.CRYSTAL_TOOLS_TAB)));
    public static final RegistryObject<Item> CRYSTAL_BLOCK_ITEM = ITEMS.register("crystal_block", () -> new BlockItem(CRYSTAL_BLOCK.get(), new Item.Properties().tab(CreativeTabs.CRYSTAL_TOOLS_TAB)));

    public static void initBlocks() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}

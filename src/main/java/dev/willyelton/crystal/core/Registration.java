package dev.willyelton.crystal.core;

import dev.willyelton.crystal.core.common.block.entity.CrystalTorch;
import dev.willyelton.crystal.core.common.block.entity.CrystalWallTorch;
import dev.willyelton.crystal.core.common.block.entity.action.ActionType;
import dev.willyelton.crystal.core.common.block.entity.action.AutoOutputAction;
import dev.willyelton.crystal.core.common.block.entity.action.AutoOutputable;
import dev.willyelton.crystal.core.common.block.entity.action.ChunkLoader;
import dev.willyelton.crystal.core.common.block.entity.action.ChunkLoadingAction;
import dev.willyelton.crystal.core.common.datacomponent.DataComponents;
import dev.willyelton.crystal.core.common.skill.attachment.EntitySkillData;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.List;

import static dev.willyelton.crystal.core.utils.constants.ApiConstants.ACTION_TYPE_REGISTRY_KEY;
import static dev.willyelton.crystal.core.utils.constants.ApiConstants.baseRl;

public class Registration {

    private Registration() {}

    public static final Registry<ActionType<?, ?>> ACTION_TYPE_REGISTRY = new RegistryBuilder<>(ACTION_TYPE_REGISTRY_KEY)
            .create();

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ApiConstants.CORE_MOD_ID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ApiConstants.CORE_MOD_ID);
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ApiConstants.CORE_MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ApiConstants.CORE_MOD_ID);
    public static final DeferredRegister<ActionType<?, ?>> ACTION_TYPES = DeferredRegister.create(ACTION_TYPE_REGISTRY_KEY, ApiConstants.CORE_MOD_ID);

    // Items
    public static final DeferredHolder<Item, Item> CRYSTAL = ITEMS.registerSimpleItem("crystal");
    public static final DeferredHolder<Item, Item> NETHERITE_STICK = ITEMS.registerSimpleItem("netherite_stick", () -> new Item.Properties().fireResistant());
    public static final DeferredHolder<Item, Item> NETHERITE_INFUSED_CRYSTAL_SHARD = ITEMS.registerSimpleItem("netherite_infused_crystal_shard", () -> new Item.Properties().fireResistant().component(net.minecraft.core.component.DataComponents.LORE, new ItemLore(List.of(Component.literal("Found in geodes at the bottom of the Nether!")))));

    // Blocks
    public static final DeferredHolder<Block, DropExperienceBlock> CRYSTAL_ORE = BLOCKS.registerBlock("crystal_ore", (properties) -> new DropExperienceBlock(UniformInt.of(3, 7), properties), () -> Block.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
    public static final DeferredHolder<Block, DropExperienceBlock> CRYSTAL_DEEPSLATE_ORE = BLOCKS.registerBlock("crystal_deepslate_ore", (properties) -> new DropExperienceBlock(UniformInt.of(3, 7), properties), () -> Block.Properties.ofFullCopy(Blocks.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE));
    public static final DeferredHolder<Block, Block> CRYSTAL_BLOCK = BLOCKS.registerBlock("crystal_block", Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
    public static final DeferredHolder<Block, Block> CRYSTAL_GEODE = BLOCKS.registerBlock("crystal_geode", Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.AMETHYST));
    public static final DeferredHolder<Block, Block> NETHERITE_INFUSED_CRYSTAL_GEODE = BLOCKS.registerBlock("netherite_infused_crystal_geode", Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.AMETHYST));
    public static final DeferredHolder<Block, CrystalTorch> CRYSTAL_TORCH = BLOCKS.registerBlock("crystal_torch", CrystalTorch::new);
    public static final DeferredHolder<Block, CrystalWallTorch> CRYSTAL_WALL_TORCH = BLOCKS.registerBlock("crystal_wall_torch", CrystalWallTorch::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_WALL_TORCH));

    // Block Items
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_ORE_ITEM = ITEMS.registerSimpleBlockItem(CRYSTAL_ORE);
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_DEEPSLATE_ORE_ITEM = ITEMS.registerSimpleBlockItem(CRYSTAL_DEEPSLATE_ORE);
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(CRYSTAL_BLOCK);
    public static final DeferredHolder<Item, BlockItem> CRYSTAL_GEODE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(CRYSTAL_GEODE);
    public static final DeferredHolder<Item, BlockItem> NETHERITE_INFUSED_CRYSTAL_GEODE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(NETHERITE_INFUSED_CRYSTAL_GEODE);
    public static final DeferredHolder<Item, StandingAndWallBlockItem> CRYSTAL_TORCH_ITEM = ITEMS.registerItem("crystal_torch", (properties) -> new StandingAndWallBlockItem(CRYSTAL_TORCH.get(), CRYSTAL_WALL_TORCH.get(), Direction.DOWN, properties.useBlockDescriptionPrefix()));

    // Data Attachments
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<EntitySkillData>> ENTITY_SKILL = ATTACHMENT_TYPES.register("entity_skill_data", () ->
            AttachmentType.builder(EntitySkillData::empty)
                    .serialize(EntitySkillData.CODEC.fieldOf("skillData"))
                    .sync(EntitySkillData.STREAM_CODEC)
                    .build());

    // Creative Tabs
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("crystal_core_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("tab.crystal_tools"))
                    .icon(() -> new ItemStack(CRYSTAL.get()))
                    .displayItems((featureFlags, output) -> {
                        output.accept(CRYSTAL.get());
                        output.accept(NETHERITE_STICK.get());
                        output.accept(NETHERITE_INFUSED_CRYSTAL_SHARD.get());
                        output.accept(CRYSTAL_ORE_ITEM.get());
                        output.accept(CRYSTAL_DEEPSLATE_ORE_ITEM.get());
                        output.accept(CRYSTAL_BLOCK_ITEM.get());
                        output.accept(CRYSTAL_GEODE.get());
                        output.accept(NETHERITE_INFUSED_CRYSTAL_GEODE.get());
                        output.accept(CRYSTAL_TORCH_ITEM.get());
                    }).build());

    // Actions
    public static final DeferredHolder<ActionType<?, ?>, ActionType<AutoOutputAction, AutoOutputable>> AUTO_OUTPUT_ACTION = ACTION_TYPES.register("auto_output", () -> new ActionType<>(baseRl("auto_output"), AutoOutputAction::new, AutoOutputable.class));
    public static final DeferredHolder<ActionType<?, ?>, ActionType<ChunkLoadingAction, ChunkLoader>> CHUNK_LOAD_ACTION = ACTION_TYPES.register("chunk_load", () -> new ActionType<>(baseRl("chunk_load"), ChunkLoadingAction::new, ChunkLoader.class));

    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);
        TABS.register(modEventBus);
        DataComponents.COMPONENTS.register(modEventBus);
    }
}

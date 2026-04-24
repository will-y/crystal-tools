package dev.willyelton.crystal_tools.api;

import dev.willyelton.crystal_tools.api.common.block.entity.action.ActionType;
import dev.willyelton.crystal_tools.api.common.block.entity.action.AutoOutputAction;
import dev.willyelton.crystal_tools.api.common.block.entity.action.AutoOutputable;
import dev.willyelton.crystal_tools.api.common.block.entity.action.ChunkLoader;
import dev.willyelton.crystal_tools.api.common.block.entity.action.ChunkLoadingAction;
import dev.willyelton.crystal_tools.api.common.skill.attachment.EntitySkillData;
import dev.willyelton.crystal_tools.api.utils.constants.ApiConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegistryBuilder;

import static dev.willyelton.crystal_tools.api.utils.constants.ApiConstants.ACTION_TYPE_REGISTRY_KEY;
import static dev.willyelton.crystal_tools.api.utils.constants.ApiConstants.baseRl;

public class Registration {

    private Registration() {}

    public static final Registry<ActionType<?, ?>> ACTION_TYPE_REGISTRY = new RegistryBuilder<>(ACTION_TYPE_REGISTRY_KEY)
            .create();

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ApiConstants.CORE_MOD_ID);
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ApiConstants.CORE_MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ApiConstants.CORE_MOD_ID);
    public static final DeferredRegister<ActionType<?, ?>> ACTION_TYPES = DeferredRegister.create(ACTION_TYPE_REGISTRY_KEY, ApiConstants.CORE_MOD_ID);

    // Items
    public static final DeferredHolder<Item, Item> CRYSTAL = ITEMS.registerSimpleItem("crystal");

    // Data Attachments
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<EntitySkillData>> ENTITY_SKILL = ATTACHMENT_TYPES.register("entity_skill_data", () ->
            AttachmentType.builder(EntitySkillData::empty)
                    .serialize(EntitySkillData.CODEC.fieldOf("skillData"))
                    .sync(EntitySkillData.STREAM_CODEC)
                    .build());

    // Creative Tabs
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("crystal_tools_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("tab.crystal_tools"))
                    .icon(() -> new ItemStack(CRYSTAL.get()))
                    .displayItems((featureFlags, output) -> {
                        output.accept(CRYSTAL.get());
                    }).build());

    // Actions
    public static final DeferredHolder<ActionType<?, ?>, ActionType<AutoOutputAction, AutoOutputable>> AUTO_OUTPUT_ACTION = ACTION_TYPES.register("auto_output", () -> new ActionType<>(baseRl("auto_output"), AutoOutputAction::new, AutoOutputable.class));
    public static final DeferredHolder<ActionType<?, ?>, ActionType<ChunkLoadingAction, ChunkLoader>> CHUNK_LOAD_ACTION = ACTION_TYPES.register("chunk_load", () -> new ActionType<>(baseRl("chunk_load"), ChunkLoadingAction::new, ChunkLoader.class));

    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);
        TABS.register(modEventBus);
    }
}

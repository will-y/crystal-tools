package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.client.events.RegisterKeyBindingsEvent;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.utils.StringUtils;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.Tags;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

import static dev.willyelton.crystal_tools.common.tags.CrystalToolsTags.MINABLE_WITH_AIOT;
import static net.neoforged.neoforge.common.ItemAbilities.DEFAULT_AXE_ACTIONS;
import static net.neoforged.neoforge.common.ItemAbilities.DEFAULT_HOE_ACTIONS;
import static net.neoforged.neoforge.common.ItemAbilities.DEFAULT_SHOVEL_ACTIONS;

public class AIOLevelableTool extends DiggerLevelableTool {
    public static final Set<ItemAbility> AIOT_ACTIONS = new HashSet<>();

    public AIOLevelableTool(Item.Properties properties) {
        super(properties.tool(CRYSTAL, MINABLE_WITH_AIOT, 3.0F, -2.4F, 5.0F), "aiot");
        AIOT_ACTIONS.addAll(DEFAULT_AXE_ACTIONS);
        AIOT_ACTIONS.addAll(DEFAULT_HOE_ACTIONS);
        AIOT_ACTIONS.addAll(DEFAULT_SHOVEL_ACTIONS);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();

        if (this.isDisabled()) {
            stack.shrink(1);
            return InteractionResult.FAIL;
        }

        UseMode mode = UseMode.fromString(stack.getOrDefault(DataComponents.USE_MODE, ""));

        switch (mode) {
            case HOE -> {
                return ToolUseUtils.useOnHoe3x3(context, this);
            }
            case SHOVEL -> {
                return ToolUseUtils.useOnShovel3x3(context, this);
            }
            case AXE -> {
                return ToolUseUtils.useOnAxeVeinStrip(context, this);
            }
            case TORCH -> {
                return ToolUseUtils.useOnTorch(context, this);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return AIOT_ACTIONS.contains(itemAbility);
    }

    // TODO: probably don't need this
    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.getDestroySpeed(null, null) != -1;
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_AIOT.get();
    }

    @Override
    public boolean canVeinMin(ItemStack stack, BlockState blockState) {
        return blockState.is(Tags.Blocks.ORES) || blockState.is(BlockTags.LOGS) || (blockState.is(BlockTags.LEAVES));
    }

    @Override
    public void addAdditionalTooltips(ItemStack stack, Consumer<Component> components, LevelableItem item) {
        String toolTip = "\u00A79" + "Use Mode: " + StringUtils.capitalize(stack.getOrDefault(DataComponents.USE_MODE, "hoe").toLowerCase(Locale.ROOT));
        if (RegisterKeyBindingsEvent.MODE_SWITCH != null) {
            toolTip = toolTip + " (alt + " + RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString() + " to change)";
        }
        components.accept(Component.literal(toolTip));
    }
}

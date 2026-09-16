package dev.willyelton.crystal.tools.common.levelable.tool;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal.core.common.levelable.ItemClickHandler;
import dev.willyelton.crystal.tools.common.components.DataComponents;
import dev.willyelton.crystal.tools.common.entity.BlockPosDirection;
import dev.willyelton.crystal.tools.common.entity.CrystalGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.CommonColors;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class GolemCommandRod extends Item implements ItemClickHandler {

    public GolemCommandRod(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        builder.accept(Component.literal("Mode: " + itemStack.getOrDefault(DataComponents.GOLEM_COMMAND_ROD_MODE, CommandRodMode.SET_POSITIONS)).withColor(TextColor.BLUE));
        List<BlockPosDirection> sourcePositions = itemStack.getOrDefault(DataComponents.GOLEM_COMMAND_ROD_SOURCE_POSITIONS, List.of());
        List<BlockPosDirection> destinationPositions = itemStack.getOrDefault(DataComponents.GOLEM_COMMAND_ROD_DESTINATION_POSITIONS, List.of());
        builder.accept(Component.literal(String.format("Stored Source Positions: %d", sourcePositions.size())).withColor(TextColor.AQUA));
        if (tooltipFlag.hasControlDown()) {
            for (BlockPosDirection sourcePosition : sourcePositions) {
                builder.accept(Component.literal("    " + sourcePosition.toString()).withColor(TextColor.AQUA));
            }
        } else if (!sourcePositions.isEmpty()) {
            builder.accept(Component.literal("    [Ctrl]").withColor(TextColor.AQUA));
        }
        builder.accept(Component.literal(String.format("Stored Destination Positions: %d", destinationPositions.size())).withColor(TextColor.AQUA));
        if (tooltipFlag.hasAltDown()) {
            for (BlockPosDirection destinationPosition : destinationPositions) {
                builder.accept(Component.literal("    " + destinationPosition.toString()).withColor(TextColor.AQUA));
            }
        } else if (!destinationPositions.isEmpty()) {
            builder.accept(Component.literal("    [Alt]").withColor(TextColor.AQUA));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        return handleClicks(level, player, stack, pos, context.getClickedFace(), DataComponents.GOLEM_COMMAND_ROD_DESTINATION_POSITIONS.get(), "Destination");
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!stack.getOrDefault(DataComponents.GOLEM_COMMAND_ROD_DESTINATION_POSITIONS, List.of()).isEmpty()) {
                stack.remove(DataComponents.GOLEM_COMMAND_ROD_DESTINATION_POSITIONS);
                player.sendOverlayMessage(Component.literal("Destination Positions Cleared").withColor(TextColor.RED));

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean leftClickBlock(ItemStack stack, Level level, Player player, BlockPos pos, Direction face) {
        if (!level.isClientSide()) {
            handleClicks(level, player, stack, pos, face, DataComponents.GOLEM_COMMAND_ROD_SOURCE_POSITIONS.get(), "Source");
        }

        return false;
    }

    @Override
    public void leftClickAir(ItemStack stack, Level level, Player player, boolean shiftDown) {
        if (shiftDown) {
            if (!stack.getOrDefault(DataComponents.GOLEM_COMMAND_ROD_SOURCE_POSITIONS, List.of()).isEmpty()) {
                stack.remove(DataComponents.GOLEM_COMMAND_ROD_SOURCE_POSITIONS);
                player.sendOverlayMessage(Component.literal("Source Positions Cleared").withColor(TextColor.RED));
            }
        }
    }

    private InteractionResult handleClicks(Level level, Player player, ItemStack stack, BlockPos pos, Direction face, DataComponentType<List<BlockPosDirection>> dataComponent, String type) {
        ResourceHandler<ItemResource> resourceHandler = level.getCapability(Capabilities.Item.BLOCK, pos, face);
        if (resourceHandler == null) {
            if (player != null) {
                player.sendOverlayMessage(Component.literal("This block does not have an inventory").withColor(TextColor.RED));
            }
            return InteractionResult.FAIL;
        }

        Set<BlockPosDirection> existingPositions = new HashSet<>(stack.getOrDefault(dataComponent, List.of()));
        BlockPosDirection newBlockPosDirection = new BlockPosDirection(pos, face);

        if (existingPositions.contains(newBlockPosDirection)) {
            existingPositions.remove(newBlockPosDirection);
            if (player != null) {
                player.sendOverlayMessage(Component.literal(String.format("%s removed from the %s list", newBlockPosDirection, type)).withColor(TextColor.AQUA));
            }
        } else {
            existingPositions.add(newBlockPosDirection);
            if (player != null) {
                player.sendOverlayMessage(Component.literal(String.format("%s added to the %s list", newBlockPosDirection, type)).withColor(TextColor.AQUA));
            }
        }

        stack.set(dataComponent, existingPositions.stream().toList());
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.is(newStack.getItem());
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return !oldStack.is(newStack.getItem());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand type) {
        if (target instanceof CrystalGolem crystalGolem) {
            CommandRodMode mode = stack.getOrDefault(DataComponents.GOLEM_COMMAND_ROD_MODE, CommandRodMode.SET_POSITIONS);

            switch (mode) {
                case SET_POSITIONS -> {
                    if (crystalGolem.getMaxSourcePositions() == 0 && crystalGolem.getMaxDestinationPositions() == 0) {
                        if (!player.level().isClientSide()) {
                            player.sendOverlayMessage(Component.literal("Golem does not have the required upgrades to set sources and destinations").withColor(0xFFE65C00));
                        }

                        return InteractionResult.SUCCESS;
                    }
                    List<BlockPosDirection> sourcePositions = stack.getOrDefault(DataComponents.GOLEM_COMMAND_ROD_SOURCE_POSITIONS, List.of());
                    List<BlockPosDirection> destinationPositions = stack.getOrDefault(DataComponents.GOLEM_COMMAND_ROD_DESTINATION_POSITIONS, List.of());

                    int sourcesSet = crystalGolem.setSourcePositions(sourcePositions);
                    int destinationsSet = crystalGolem.setDestinationPositions(destinationPositions);

                    if (!player.level().isClientSide()) {
                        if (sourcesSet != sourcePositions.size() || destinationsSet != destinationPositions.size()) {
                            player.sendOverlayMessage(Component.literal(
                                    String.format("Golem can only have %d sources and %d destinations. Not all stored positions were saved",
                                            crystalGolem.getMaxSourcePositions(), crystalGolem.getMaxDestinationPositions()))
                                    .withColor(0xFFE65C00));
                        } else {
                            player.sendOverlayMessage(Component.literal("Positions Saved to Crystal Golem").withColor(TextColor.AQUA));
                        }
                    }
                }
                case READ_POSITIONS -> {
                    List<BlockPosDirection> sourcePositions = crystalGolem.getSourcePositions();
                    List<BlockPosDirection> destinationPositions = crystalGolem.getDestinationPositions();
                    stack.set(DataComponents.GOLEM_COMMAND_ROD_SOURCE_POSITIONS, sourcePositions);
                    stack.set(DataComponents.GOLEM_COMMAND_ROD_DESTINATION_POSITIONS, destinationPositions);
                    if (!player.level().isClientSide()) {
                        player.sendOverlayMessage(Component.literal("Positions Read from Crystal Golem").withColor(TextColor.AQUA));
                    }
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public enum CommandRodMode implements StringRepresentable {
        SET_POSITIONS,
        READ_POSITIONS;

        public static Codec<CommandRodMode> CODEC = StringRepresentable.fromEnum(CommandRodMode::values);
        public static StreamCodec<FriendlyByteBuf, CommandRodMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(CommandRodMode.class);

        public CommandRodMode next() {
            return values()[(this.ordinal() + 1) % values().length];
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}

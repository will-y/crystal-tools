package dev.willyelton.crystal_tools.utils;

import com.mojang.datafixers.util.Pair;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.client.events.RegisterKeyBindingsEvent;
import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalTorch;
import dev.willyelton.crystal_tools.common.levelable.tool.VeinMinerLevelableTool;
import dev.willyelton.crystal_tools.common.network.data.BlockStripPayload;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

// Use a component and UseItemOnBlockEvent to remove this class?
// Probably doesn't help a lot, surely these move to components eventually
public class ToolUseUtils {
    public static InteractionResult useOnAxe(UseOnContext context, Levelable levelable) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState blockstate = level.getBlockState(blockpos);
        ItemStack stack = context.getItemInHand();


        int durability = stack.getMaxDamage() - stack.getDamageValue();

        if (durability <= 1) {
            return InteractionResult.PASS;
        }

        Optional<BlockState> optional = Optional.ofNullable(blockstate.getToolModifiedState(context, ItemAbilities.AXE_STRIP, false));
        Optional<BlockState> optional1 = Optional.ofNullable(blockstate.getToolModifiedState(context, ItemAbilities.AXE_SCRAPE, false));
        Optional<BlockState> optional2 = Optional.ofNullable(blockstate.getToolModifiedState(context, ItemAbilities.AXE_WAX_OFF, false));
        Optional<BlockState> optional3 = Optional.empty();
        if (optional.isPresent()) {
            level.playSound(player, blockpos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            optional3 = optional;
        } else if (optional1.isPresent()) {
            level.playSound(player, blockpos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3005, blockpos, 0);
            optional3 = optional1;
        } else if (optional2.isPresent()) {
            level.playSound(player, blockpos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, blockpos, 0);
            optional3 = optional2;
        }

        if (optional3.isPresent()) {
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, stack);
            }

            level.setBlock(blockpos, optional3.get(), 11);

            if (player != null) {
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
            }

            levelable.addExp(level, blockpos, player);

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public static <T extends VeinMinerLevelableTool> InteractionResult useOnAxeVeinStrip(UseOnContext context, T tool) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos blockPos = context.getClickedPos();
        BlockState initialState = level.getBlockState(blockPos);
        ItemStack stack = context.getItemInHand();
        Levelable levelable = stack.getCapability(Capabilities.ITEM_SKILL, level);

        if (levelable == null) {
            return InteractionResult.PASS;
        }

        InteractionResult result = ToolUseUtils.useOnAxe(context, levelable);

        if (result == InteractionResult.SUCCESS) {
            if (stack.getOrDefault(DataComponents.VEIN_MINER, 0) > 0
                    && level.isClientSide && RegisterKeyBindingsEvent.VEIN_MINE.isDown()) {
                Collection<BlockPos> blocksToStrip = BlockCollectors.collectVeinMine(blockPos, level, tool.getVeinMinerPredicate(initialState), tool.getMaxBlocks(stack));

                for (BlockPos pos : blocksToStrip) {
                    BlockState blockState = level.getBlockState(pos);
                    Optional<BlockState> optional = Optional.ofNullable(blockState.getToolModifiedState(context, ItemAbilities.AXE_STRIP, false));
                    if (optional.isPresent()) {
                        stripBlock(level, stack, player, pos, context.getHand(), optional.get(), levelable);
                        PacketDistributor.sendToServer(new BlockStripPayload(pos, context.getHand(), optional.get()));
                    }
                }
            }
        }

        return result;
    }

    public static void stripBlock(Level level, ItemStack stack, Player player, BlockPos blockPos,
                                                            InteractionHand hand, BlockState newState, Levelable levelable) {
        if ((stack.getMaxDamage() - stack.getDamageValue()) == 0) {
            return;
        }

        level.setBlock(blockPos, newState, 11);

        if (player != null) {
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }

        levelable.addExp(level, blockPos, player);
    }

    public static InteractionResult useOnShovel(UseOnContext context, BlockPos blockpos) {
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        BlockState blockstate = level.getBlockState(blockpos);

        if (ToolUtils.isBroken(stack)) return InteractionResult.PASS;

        Levelable levelable = stack.getCapability(Capabilities.ITEM_SKILL, level);

        if (levelable == null) {
            return InteractionResult.PASS;
        }

        if (context.getClickedFace() == Direction.DOWN) {
            return InteractionResult.PASS;
        } else {
            Player player = context.getPlayer();
            BlockState blockstate1 = blockstate.getToolModifiedState(context, ItemAbilities.SHOVEL_FLATTEN, false);
            BlockState blockstate2 = null;
            if (blockstate1 != null && level.isEmptyBlock(blockpos.above())) {
                level.playSound(player, blockpos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                blockstate2 = blockstate1;
            } else if (blockstate.getBlock() instanceof CampfireBlock && blockstate.getValue(CampfireBlock.LIT)) {
                if (!level.isClientSide()) {
                    level.levelEvent((Player)null, 1009, blockpos, 0);
                }

                CampfireBlock.dowse(context.getPlayer(), level, blockpos, blockstate);
                blockstate2 = blockstate.setValue(CampfireBlock.LIT, false);
            }

            if (blockstate2 != null) {
                if (!level.isClientSide) {
                    level.setBlock(blockpos, blockstate2, 11);
                    if (player != null) {
                        context.getItemInHand().hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
                    }
                }

                levelable.addExp(level, blockpos, context.getPlayer());

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public static InteractionResult useOnShovel3x3(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (stack.getOrDefault(DataComponents.HAS_3x3, false) && !stack.getOrDefault(DataComponents.DISABLE_3x3, false)) {
            InteractionResult result = null;

            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    InteractionResult shovelResult = useOnShovel(context, context.getClickedPos().offset(i, 0, j));
                    if (!shovelResult.equals(InteractionResult.PASS)) {
                        result = shovelResult;
                    }
                }
            }

            return result == null ? InteractionResult.PASS : result;
        }

        return useOnShovel(context, context.getClickedPos());
    }

    public static InteractionResult useOnTorch(UseOnContext context) {
        ItemStack stack = context.getItemInHand();

        if (stack.getOrDefault(DataComponents.TORCH, false)) {
            Level level = context.getLevel();
            BlockPos position = context.getClickedPos();
            BlockState state = level.getBlockState(position);
            Direction direction = context.getClickedFace();
            position = position.relative(direction);

            Levelable levelable = stack.getCapability(Capabilities.ITEM_SKILL, level);
            if (levelable == null) {
                return InteractionResult.PASS;
            }

            BlockState torchBlockState;

            if (direction.equals(Direction.UP)) {
                torchBlockState = Registration.CRYSTAL_TORCH.get().defaultBlockState().setValue(CrystalTorch.DROP_ITEM, false);
            } else if (direction.equals(Direction.DOWN)) {
                return InteractionResult.PASS;
            } else {
                torchBlockState = Registration.CRYSTAL_WALL_TORCH.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, direction).setValue(CrystalTorch.DROP_ITEM, false);
            }

            if (level.getBlockState(position).isAir() && state.isFaceSturdy(level, position, direction)) {
                level.setBlock(position, torchBlockState, 0);
                level.playSound(context.getPlayer(), position, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            } else {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide && context.getPlayer() != null) {
                stack.hurtAndBreak(10, context.getPlayer(), LivingEntity.getSlotForHand(context.getHand()));
            }

            levelable.addExp(level, context.getClickedPos(), context.getPlayer());

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public static InteractionResult useOnHoe(UseOnContext context) {
        return useOnHoe(context, context.getClickedPos());
    }

    public static InteractionResult useOnHoe(UseOnContext context, BlockPos blockPos) {
        ItemStack stack = context.getItemInHand();
        int durability = stack.getMaxDamage() - stack.getDamageValue();

        if (durability <= 1) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockState toolModifiedState = level.getBlockState(blockPos).getToolModifiedState(context, ItemAbilities.HOE_TILL, false);
        Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = toolModifiedState == null ? null : Pair.of(ctx -> true, changeIntoState(toolModifiedState, blockPos));
        if (pair == null) {
            return InteractionResult.PASS;
        } else {
            Predicate<UseOnContext> predicate = pair.getFirst();
            Consumer<UseOnContext> consumer = pair.getSecond();
            if (predicate.test(context)) {
                Player player = context.getPlayer();
                level.playSound(player, blockPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!level.isClientSide) {
                    consumer.accept(context);
                    if (player != null) {
                        context.getItemInHand().hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
                    }
                }

                Levelable levelable = stack.getCapability(Capabilities.ITEM_SKILL, level);
                if (levelable == null) {
                    return InteractionResult.PASS;
                }

                levelable.addExp(level, blockPos, player);

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public static InteractionResult useOnHoe3x3(UseOnContext context) {
        ItemStack stack = context.getItemInHand();

        if (stack.getOrDefault(DataComponents.HAS_3x3, false) && !stack.getOrDefault(DataComponents.DISABLE_3x3, false)) {
            InteractionResult result = null;

            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    InteractionResult hoeResult = useOnHoe(context, context.getClickedPos().offset(i, 0, j));
                    if (!hoeResult.equals(InteractionResult.PASS)) {
                        result = hoeResult;
                    }
                }
            }

            return result == null ? InteractionResult.PASS : result;
        }

        return useOnHoe(context);
    }

    private static Consumer<UseOnContext> changeIntoState(BlockState blockState, BlockPos blockPos) {
        return (context) -> {
            context.getLevel().setBlock(blockPos, blockState, 11);
            context.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(context.getPlayer(), blockState));
        };
    }
}

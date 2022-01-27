package dev.willyelton.crystal_tools.item.tool;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.minecraft.world.item.HoeItem.changeIntoState;
import static net.minecraft.world.item.HoeItem.changeIntoStateAndDropItem;

public class HoeLevelableTool extends LevelableTool {
    // just took this from HoeItem
    private static final Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> TILLABLES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())), Blocks.DIRT_PATH, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())), Blocks.DIRT, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())), Blocks.COARSE_DIRT, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.DIRT.defaultBlockState())), Blocks.ROOTED_DIRT, Pair.of((p_150861_) -> {
        return true;
    }, changeIntoStateAndDropItem(Blocks.DIRT.defaultBlockState(), Items.HANGING_ROOTS))));

    public HoeLevelableTool() {
        super(new Item.Properties(), BlockTags.MINEABLE_WITH_HOE, "hoe");
    }

    // also just took this from HoeItem
    // don't think I need to change other than give exp
    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack hoe = context.getItemInHand();
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();

        int durability = this.getMaxDamage(hoe) - (int) NBTUtils.getFloatOrAddKey(hoe, "Damage");

        if (durability <= 1) {
            return InteractionResult.PASS;
        }

        Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = TILLABLES.get(level.getBlockState(blockpos).getBlock());
        int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(context);
        if (hook != 0) return hook > 0 ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        if (context.getClickedFace() != Direction.DOWN && level.isEmptyBlock(blockpos.above())) {
            if (pair == null) {
                return InteractionResult.PASS;
            } else {
                Predicate<UseOnContext> predicate = pair.getFirst();
                Consumer<UseOnContext> consumer = pair.getSecond();
                if (predicate.test(context)) {
                    Player player = context.getPlayer();
                    level.playSound(player, blockpos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!level.isClientSide) {
                        consumer.accept(context);
                        if (player != null) {
                            context.getItemInHand().hurtAndBreak(1, player, (p_150845_) -> {
                                p_150845_.broadcastBreakEvent(context.getHand());
                            });
                        }
                    }

                    addExp(hoe, level, blockpos);

                    return InteractionResult.sidedSuccess(level.isClientSide);
                } else {
                    return InteractionResult.PASS;
                }
            }
        }

        return InteractionResult.PASS;
    }
}

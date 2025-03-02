package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.Collection;

public class CrystalFishingRod extends LevelableTool {
    public static final String CRYSTAL_TOOLS_FISHING_MAIN_TAG = "crystal_tools.fishing.main";
    public static final String CRYSTAL_TOOLS_FISHING_OFF_TAG = "crystal_tools.fishing.off";
    public CrystalFishingRod() {
        super(new Item.Properties(), null, "fishing_rod", 1, -2.8F);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.isDisabled()) {
            stack.shrink(1);
        }

        if (player.fishing != null) {
            // Remove bobber
            if (!level.isClientSide) {
                int rodDamage = player.fishing.retrieve(stack);
                stack.hurtAndBreak(rodDamage, (ServerLevel) level, player, null);
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, getRandomPitch(level));
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, getRandomPitch(level));
            if (!level.isClientSide) {
                int fishingSpeedBonus = stack.getOrDefault(DataComponents.LURE, 0);
                int luckBonus = stack.getOrDefault(DataComponents.LUCK_OF_THE_SEA, 0);
                FishingHook fishingHook = new FishingHook(player, level, fishingSpeedBonus, luckBonus);
                if (hand == InteractionHand.MAIN_HAND) {
                    fishingHook.addTag(CRYSTAL_TOOLS_FISHING_MAIN_TAG);
                } else if (hand == InteractionHand.OFF_HAND) {
                    fishingHook.addTag(CRYSTAL_TOOLS_FISHING_OFF_TAG);
                }
                level.addFreshEntity(fishingHook);
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_FISHING_ROD.get();
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_FISHING_ROD_ACTIONS.contains(itemAbility);
    }

    @Override
    public boolean mineBlock(ItemStack tool, Level level, BlockState blockState, BlockPos blockPos, LivingEntity entity) {
       return false;
    }

    private float getRandomPitch(Level level) {
        return 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F);
    }

    public static void dropExtraItems(Collection<ItemStack> stacks, Player player, FishingHook hookEntity) {
        for(ItemStack itemstack : stacks) {
            ItemEntity itementity = new ItemEntity(player.level(), hookEntity.getX(), hookEntity.getY(), hookEntity.getZ(), itemstack);
            double d0 = player.getX() - hookEntity.getX();
            double d1 = player.getY() - hookEntity.getY();
            double d2 = player.getZ() - hookEntity.getZ();
            itementity.setDeltaMovement(d0 * 0.1D, d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);
            player.level().addFreshEntity(itementity);
            if (itemstack.is(ItemTags.FISHES)) {
                player.awardStat(Stats.FISH_CAUGHT, 1);
            }
        }
    }
}

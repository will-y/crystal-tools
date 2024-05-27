package dev.willyelton.crystal_tools.levelable.tool;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import java.util.Collection;

public class CrystalFishingRod extends LevelableTool {
    public static final String CRYSTAL_TOOLS_FISHING_MAIN_TAG = "crystal_tools.fishing.main";
    public static final String CRYSTAL_TOOLS_FISHING_OFF_TAG = "crystal_tools.fishing.off";
    public CrystalFishingRod() {
        super(new Item.Properties(), null, "fishing_rod", 1, -2.8F);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.isDisabled()) {
            stack.shrink(1);
        }

        if (player.fishing != null) {
            // Remove bobber
            if (!level.isClientSide) {
                int rodDamage = player.fishing.retrieve(stack);
                damageItem(stack, rodDamage, player, null);
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, getRandomPitch(level));
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, getRandomPitch(level));
            if (!level.isClientSide) {
                int fishingSpeedBonus = (int) NBTUtils.getFloatOrAddKey(stack, "lure");
                int luckBonus = (int) NBTUtils.getFloatOrAddKey(stack, "luck_of_the_sea");
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

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_FISHING_ROD.get();
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_FISHING_ROD_ACTIONS.contains(toolAction);
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

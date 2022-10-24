package dev.willyelton.crystal_tools.item;

import dev.willyelton.crystal_tools.item.tool.LevelableTool;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrystalApple extends LevelableTool {
    private static final int BASE_NUTRITION = 2;
    private static final float BASE_SATURATION = 0.4F;
    private static final int BASE_EAT_SPEED = 32;

    public CrystalApple(Properties properties, TagKey<Block> mineableBlocks, String itemType, int durability) {
        super(properties, mineableBlocks, itemType, durability);
    }

    public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        return getFoodPropertiesFromNBT(stack);
    }

    private FoodProperties getFoodPropertiesFromNBT(ItemStack stack) {
        int nutrition = BASE_NUTRITION + (int) NBTUtils.getFloatOrAddKey(stack, "nutrition_bonus");
        float saturation = BASE_SATURATION + NBTUtils.getFloatOrAddKey(stack, "saturation_bonus");
        boolean alwaysEat = NBTUtils.getBoolean(stack, "always_eat");
        // TODO: Effects

        FoodProperties.Builder builder = new FoodProperties.Builder();

        builder.nutrition(nutrition).saturationMod(saturation);

        if (alwaysEat) builder.alwaysEat();

        return builder.build();
    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        if (itemstack.getItem() instanceof CrystalApple) {
            if (player.canEat(itemstack.getFoodProperties(player).canAlwaysEat())) {
                player.startUsingItem(usedHand);
                return InteractionResultHolder.consume(itemstack);
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        } else {
            return InteractionResultHolder.pass(itemstack);
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        int eatSpeed = BASE_EAT_SPEED - (int) NBTUtils.getFloatOrAddKey(stack, "eat_speed_bonus");
        return Math.max(eatSpeed, 4);
    }
}

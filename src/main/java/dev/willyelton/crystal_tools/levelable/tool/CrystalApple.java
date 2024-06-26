package dev.willyelton.crystal_tools.levelable.tool;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrystalApple extends LevelableTool {
    private static final int BASE_NUTRITION = 2;
    private static final float BASE_SATURATION = 0.4F;
    private static final int BASE_EAT_SPEED = 32;

    public CrystalApple() {
        super(new Item.Properties().fireResistant(), null, "apple", -4, 0, 50);
    }

    @Override
    public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        return getFoodPropertiesFromNBT(stack);
    }

    @Override
    public FoodProperties getFoodProperties() {
        return new FoodProperties.Builder().build();
    }

    private FoodProperties getFoodPropertiesFromNBT(ItemStack stack) {
        int nutrition = BASE_NUTRITION + (int) NBTUtils.getFloatOrAddKey(stack, "nutrition_bonus");
        float saturation = BASE_SATURATION + NBTUtils.getFloatOrAddKey(stack, "saturation_bonus");
        boolean alwaysEat = NBTUtils.getBoolean(stack, "always_eat");

        FoodProperties.Builder builder = new FoodProperties.Builder();

        builder.nutrition(nutrition).saturationMod(saturation);

        if (alwaysEat) builder.alwaysEat();

        if (stack.getTag() != null) {
            for (int i = 1; i < 34; i++) {
                if (stack.getTag().contains("effect_" + i)) {
                    MobEffect effect = MobEffect.byId(i);
                    if (effect != null) {
                        MobEffectInstance instance = new MobEffectInstance(effect, (int) NBTUtils.getFloatOrAddKey(stack, "effect_" + i) * 20, 1, false, false);
                        builder.effect(() -> instance, 1);
                    }
                }
            }
        }

        return builder.build();
    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        if (this.isDisabled()) {
            itemstack.shrink(1);
            return InteractionResultHolder.fail(itemstack);
        }

        if (itemstack.getItem() instanceof CrystalApple) {
            if (player.canEat(itemstack.getFoodProperties(player).canAlwaysEat()) && !ToolUtils.isBroken(itemstack)) {
                player.startUsingItem(usedHand);
                return InteractionResultHolder.pass(itemstack);
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

    @Override
    public boolean isEdible() {
        return true;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity player) {
        player.eat(level, stack);

        if (!(player instanceof Player) || !((Player) player).getAbilities().instabuild) {
            stack.grow(1);
        }
        int nutrition = BASE_NUTRITION + (int) NBTUtils.getFloatOrAddKey(stack, "nutrition_bonus");
        float saturation = BASE_SATURATION + NBTUtils.getFloatOrAddKey(stack, "saturation_bonus");
        int effectiveHunger = (int) (nutrition * saturation * 2) + nutrition;
        this.addExp(stack, level, player.getOnPos(), player, (int) (effectiveHunger * CrystalToolsConfig.APPLE_EXPERIENCE_BOOST.get()));
        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return stack;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int inventorySlot, boolean inHand) {
        ToolUtils.inventoryTick(itemStack, level, entity, inventorySlot, inHand, CrystalToolsConfig.APPLE_REPAIR_MODIFIER.get());
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_APPLE.get();
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack tool, Level level, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity entity) {
        // If this tool is disabled break on use
        if (this.isDisabled()) {
            tool.shrink(1);
            return false;
        }

        return true;
    }
}

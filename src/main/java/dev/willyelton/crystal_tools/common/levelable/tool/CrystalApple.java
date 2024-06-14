package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

// TODO: Food things seem to be data components now, might need / want to do that?
// Might be fine, ItemStack#getFoodProperties only calls my method
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

    private FoodProperties getFoodPropertiesFromNBT(ItemStack stack) {
        int nutrition = BASE_NUTRITION + stack.getOrDefault(DataComponents.NUTRITION_BONUS, 0);
        float saturation = BASE_SATURATION + stack.getOrDefault(DataComponents.SATURATION_BONUS, 0F);
        boolean alwaysEat = stack.getOrDefault(DataComponents.ALWAYS_EAT, false);

        FoodProperties.Builder builder = new FoodProperties.Builder();

        builder.nutrition(nutrition).saturationModifier(saturation);

        if (alwaysEat) builder.alwaysEdible();

        // TODO: Effect refactor
//        if (stack.getTag() != null) {
//            for (int i = 1; i < 34; i++) {
//                if (stack.getTag().contains("effect_" + i)) {
//                    MobEffect effect = MobEffect.byId(i);
//                    if (effect != null) {
//                        MobEffectInstance instance = new MobEffectInstance(effect, (int) NBTUtils.getFloatOrAddKey(stack, "effect_" + i) * 20, 1, false, false);
//                        builder.effect(() -> instance, 1);
//                    }
//                }
//            }
//        }

        return builder.build();
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        if (this.isDisabled()) {
            itemstack.shrink(1);
            return InteractionResultHolder.fail(itemstack);
        }

        if (itemstack.getItem() instanceof CrystalApple) {
            FoodProperties foodProperties = itemstack.getFoodProperties(player);
            if (foodProperties != null && player.canEat(foodProperties.canAlwaysEat()) && !ToolUtils.isBroken(itemstack)) {
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
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        int eatSpeed = BASE_EAT_SPEED - stack.getOrDefault(DataComponents.EAT_SPEED_BONUS, 0);
        // TODO: Let this be less than 4?
        return Math.max(eatSpeed, 4);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity player) {
        player.eat(level, stack);

        if (!(player instanceof Player) || !((Player) player).getAbilities().instabuild) {
            stack.grow(1);
        }
        int nutrition = BASE_NUTRITION + stack.getOrDefault(DataComponents.NUTRITION_BONUS, 0);
        float saturation = BASE_SATURATION + stack.getOrDefault(DataComponents.SATURATION_BONUS, 0F);
        int effectiveHunger = (int) (nutrition * saturation * 2) + nutrition;
        this.addExp(stack, level, player.getOnPos(), player, (int) (effectiveHunger * CrystalToolsConfig.APPLE_EXPERIENCE_BOOST.get()));
        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean inHand) {
        ToolUtils.inventoryTick(itemStack, level, entity, inventorySlot, inHand, CrystalToolsConfig.APPLE_REPAIR_MODIFIER.get());
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_APPLE.get();
    }

    @Override
    public boolean mineBlock(ItemStack tool, Level level, BlockState blockState, BlockPos blockPos, LivingEntity entity) {
        // If this tool is disabled break on use
        if (this.isDisabled()) {
            tool.shrink(1);
            return false;
        }

        return true;
    }
}

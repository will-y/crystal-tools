package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.components.EffectData;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CrystalApple extends LevelableTool {
    private static final int BASE_NUTRITION = 2;
    private static final float BASE_SATURATION = 0.4F;
    private static final int BASE_EAT_SPEED = 32;

    public CrystalApple(Item.Properties properties) {
        super(properties.repairable(CrystalToolsTags.REPAIRS_CRYSTAL)
                .durability(50), "apple");
    }

    // TODO (PORTING): Probably only a datacomponent now
    public FoodProperties getFoodProperties(ItemStack stack, LivingEntity entity) {
        return getFoodPropertiesFromNBT(stack);
    }

    private FoodProperties getFoodPropertiesFromNBT(ItemStack stack) {
        int nutrition = BASE_NUTRITION + stack.getOrDefault(DataComponents.NUTRITION_BONUS, 0);
        float saturation = BASE_SATURATION + stack.getOrDefault(DataComponents.SATURATION_BONUS, 0F);
        boolean alwaysEat = stack.getOrDefault(DataComponents.ALWAYS_EAT, false);

        Consumable.Builder consumableBuilder = Consumable.builder();

        FoodProperties.Builder builder = new FoodProperties.Builder();

        builder.nutrition(nutrition).saturationModifier(saturation);

        if (alwaysEat) builder.alwaysEdible();

        List<EffectData> effects = stack.getOrDefault(DataComponents.EFFECTS, Collections.emptyList());

        for (EffectData effect : effects) {
            Optional<Holder.Reference<MobEffect>> mobEffect = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.withDefaultNamespace(effect.resourceLocation()));
            if (mobEffect.isPresent()) {
                MobEffectInstance instance = new MobEffectInstance(mobEffect.get(), effect.duration() * 20, 1, false, false);
                consumableBuilder.onConsume(new ApplyStatusEffectsConsumeEffect(instance, 1));
            }
        }

        // TODO (PORTING): This is going away, but this new logic with the consumableBuilder probably needs to go somewhere (maybe it can just be a datacomponent? not sure)

        return builder.build();
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        if (this.isDisabled()) {
            itemstack.shrink(1);
            return InteractionResult.FAIL;
        }

        // TODO (PORTING): This is going to have to go somewhere else (or maybe not be needed with the consumable datacomponent)
//        if (itemstack.getItem() instanceof CrystalApple) {
//            FoodProperties foodProperties = itemstack.getFoodProperties(player);
//            if (foodProperties != null && player.canEat(foodProperties.canAlwaysEat()) && !ToolUtils.isBroken(itemstack)) {
//                player.startUsingItem(usedHand);
//                return InteractionResultHolder.pass(itemstack);
//            } else {
//                return InteractionResultHolder.fail(itemstack);
//            }
//        } else {
//            return InteractionResultHolder.pass(itemstack);
//        }

        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        int eatSpeed = BASE_EAT_SPEED - stack.getOrDefault(DataComponents.EAT_SPEED_BONUS, 0);
        return Math.max(eatSpeed, 4);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity player) {
        // TODO (PORTING): This is probably handled by the consumer data component
//        player.eat(level, stack);

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
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        levelableInventoryTick(itemStack, level, entity, slot, CrystalToolsConfig.APPLE_REPAIR_MODIFIER.get());
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

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.EAT;
    }
}

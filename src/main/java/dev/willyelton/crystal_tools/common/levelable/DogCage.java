package dev.willyelton.crystal_tools.common.levelable;

import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.skill.attachment.EntitySkillData;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import dev.willyelton.crystal_tools.utils.constants.EntitySkills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.function.Consumer;

public class DogCage extends Item implements MobCaptureTool {

    public DogCage(Properties properties) {
        super(properties.fireResistant().stacksTo(1));
    }

    @Override
    public boolean canCapture(ItemStack stack, ServerLevel level, ServerPlayer player, LivingEntity livingEntity) {
        if (stack.has(DataComponents.ENTITY_DATA)) {
            return false;
        }

        if (CrystalToolsConfig.PICK_UP_OTHER_ENTITIES.get()) {
            return !livingEntity.getType().is(CrystalToolsTags.PICKUP_ENTITY_BLACKLIST);
        } else {
            EntitySkillData skillData = livingEntity.getData(ModRegistration.ENTITY_SKILL);
            if (skillData.hasSkill(EntitySkills.CRATE_TRAINING)) {
                if (livingEntity instanceof TamableAnimal tamableAnimal) {
                    return tamableAnimal.isOwnedBy(player);
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public Component cannotCapture(ItemStack stack, ServerLevel level, ServerPlayer player, LivingEntity livingEntity) {
        Component entityName = livingEntity.getDisplayName();

        if (stack.has(DataComponents.ENTITY_DATA)) {
            return Component.translatable("tooltip.crystal_tools.dog_cage_full");
        }
        if (livingEntity instanceof TamableAnimal tamableAnimal) {
            if (!tamableAnimal.isOwnedBy(player)) {
                return Component.translatable("tooltip.crystal_tools.dog_cage_not_owned", entityName);
            }
        }

        EntitySkillData skillData = livingEntity.getData(ModRegistration.ENTITY_SKILL);
        if (!skillData.hasSkill(EntitySkills.CRATE_TRAINING)) {
            return Component.translatable("tooltip.crystal_tools.dog_cage_not_trained", entityName);
        }

        return null;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (blockHitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos relativeBlockPos = blockPos.relative(direction);

            if (!level.mayInteract(player, blockPos) || !player.mayUseItemAt(relativeBlockPos, direction, stack)) {
                return InteractionResult.FAIL;
            } else if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                this.releaseMob(stack, serverLevel, relativeBlockPos, serverPlayer);
                if (stack.getOrDefault(dev.willyelton.crystal_tools.common.components.DataComponents.BREAK_CAGE_ON_USE, false)) {
                    stack.shrink(1);
                }
            }
        }

        return InteractionResult.PASS;
    }

    // TODO: Would like to show more data, probably going to have to get it out of the raw NBT though
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        if (CrystalToolsConfig.PICK_UP_OTHER_ENTITIES.get()) {
            tooltipAdder.accept(Component.translatable("tooltip.crystal_tools.dog_cage_all").withStyle(ChatFormatting.DARK_PURPLE).withStyle(ChatFormatting.ITALIC));
        } else {
            tooltipAdder.accept(Component.translatable("tooltip.crystal_tools.dog_cage").withStyle(ChatFormatting.DARK_PURPLE).withStyle(ChatFormatting.ITALIC));
        }
        TypedEntityData<EntityType<?>> entityData = stack.get(DataComponents.ENTITY_DATA);
        String tooltip = stack.get(dev.willyelton.crystal_tools.common.components.DataComponents.CAPTURED_ENTITY_TOOLTIP);

        if (entityData != null && tooltip != null) {
            tooltipAdder.accept(Component.literal("Holding: " + tooltip).withStyle(ChatFormatting.AQUA));
        } else {
            tooltipAdder.accept(Component.translatable("tooltip.crystal_tools.dog_cage_empty").withStyle(ChatFormatting.DARK_AQUA));
        }
    }
}

package dev.willyelton.crystal_tools.item.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import dev.willyelton.crystal_tools.utils.LevelUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AIOLevelableTool extends DiggerLevelableTool {
    public AIOLevelableTool() {
        super(new Properties(), BlockTags.MINEABLE_WITH_PICKAXE, "aiot");
    }

    @Override
    public boolean correctTool(ItemStack tool, BlockState blockState) {
        return true;
    }

    // From Sword
    @Override
    public boolean hurtEnemy(ItemStack tool, LivingEntity target, LivingEntity attacker) {
        tool.hurtAndBreak(1, attacker, (p_43296_) -> {
            p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });

        if (!ToolUtils.isBroken(tool)) {
            if (NBTUtils.getFloatOrAddKey(tool, "fire") > 0) {
                target.setSecondsOnFire(5);
            }

            int heal = (int) NBTUtils.getFloatOrAddKey(tool, "lifesteal");

            if (heal > 0) {
                attacker.heal(heal);
            }

            addExp(tool, target.getLevel(), attacker.getOnPos(), attacker, (int) (SwordLevelableTool.getAttackDamage(tool)));
        }

        return true;
    }

    // From Sword
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND && !ToolUtils.isBroken(stack)) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", SwordLevelableTool.getAttackDamage(stack), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", SwordLevelableTool.getAttackSpeed(stack), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier("Weapon modifier", SwordLevelableTool.getAttackKnockback(stack), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier("Weapon modifier", SwordLevelableTool.getKnockbackResistance(stack), AttributeModifier.Operation.ADDITION));
            return builder.build();
        } else {
            return super.getAttributeModifiers(slot, stack);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();

        UseMode mode = UseMode.fromString(NBTUtils.getString(stack, "use_mode"));

        switch (mode) {
            case HOE -> {
                return useOnHoe(context);
            }
            case SHOVEL -> {
                return useOnShovel(context);
            }
            case AXE -> {
                return useOnAxe(context);
            }
            case TORCH -> {
                return useOnTorch(context);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, components, flag);
        components.add(new TextComponent("\u00A79" + "Mode: " + NBTUtils.getString(itemStack, "use_mode").toLowerCase(Locale.ROOT)));
    }

    public InteractionResult useOnAxe(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        BlockState blockstate = level.getBlockState(blockpos);
        ItemStack itemStack = pContext.getItemInHand();

        int durability = this.getMaxDamage(itemStack) - (int) NBTUtils.getFloatOrAddKey(itemStack, "Damage");

        if (durability <= 1) {
            return InteractionResult.PASS;
        }
        Optional<BlockState> optional = Optional.ofNullable(blockstate.getToolModifiedState(level, blockpos, player, pContext.getItemInHand(), net.minecraftforge.common.ToolActions.AXE_STRIP));
        Optional<BlockState> optional1 = Optional.ofNullable(blockstate.getToolModifiedState(level, blockpos, player, pContext.getItemInHand(), net.minecraftforge.common.ToolActions.AXE_SCRAPE));
        Optional<BlockState> optional2 = Optional.ofNullable(blockstate.getToolModifiedState(level, blockpos, player, pContext.getItemInHand(), net.minecraftforge.common.ToolActions.AXE_WAX_OFF));
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
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemStack);
            }

            level.setBlock(blockpos, optional3.get(), 11);

            if (player != null) {
                itemStack.hurtAndBreak(1, player, (p_150686_) -> {
                    p_150686_.broadcastBreakEvent(pContext.getHand());
                });
            }

            addExp(itemStack, level, blockpos, player);

            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    public InteractionResult useOnShovel(UseOnContext pContext) {
        ItemStack shovel = pContext.getItemInHand();
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);

        int durability = this.getMaxDamage(shovel) - (int) NBTUtils.getFloatOrAddKey(shovel, "Damage");

        if (durability <= 1) {
            return InteractionResult.PASS;
        }

        if (pContext.getClickedFace() == Direction.DOWN) {
            return InteractionResult.PASS;
        } else {
            Player player = pContext.getPlayer();
            BlockState blockstate1 = blockstate.getToolModifiedState(level, blockpos, player, pContext.getItemInHand(), net.minecraftforge.common.ToolActions.SHOVEL_FLATTEN);
            BlockState blockstate2 = null;
            if (blockstate1 != null && level.isEmptyBlock(blockpos.above())) {
                level.playSound(player, blockpos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                blockstate2 = blockstate1;
            } else if (blockstate.getBlock() instanceof CampfireBlock && blockstate.getValue(CampfireBlock.LIT)) {
                if (!level.isClientSide()) {
                    level.levelEvent((Player)null, 1009, blockpos, 0);
                }

                CampfireBlock.dowse(pContext.getPlayer(), level, blockpos, blockstate);
                blockstate2 = blockstate.setValue(CampfireBlock.LIT, Boolean.valueOf(false));
            }

            if (blockstate2 != null) {
                if (!level.isClientSide) {
                    level.setBlock(blockpos, blockstate2, 11);
                    if (player != null) {
                        pContext.getItemInHand().hurtAndBreak(1, player, (p_43122_) -> {
                            p_43122_.broadcastBreakEvent(pContext.getHand());
                        });
                    }
                }

                addExp(shovel, level, blockpos, pContext.getPlayer());

                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public InteractionResult useOnTorch(UseOnContext context) {
        ItemStack tool = context.getItemInHand();

        if (NBTUtils.getFloatOrAddKey(tool, "torch") > 0) {
            Level level = context.getLevel();
            BlockPos position = context.getClickedPos();
            BlockState state = level.getBlockState(position);
            Direction direction = context.getClickedFace();
            position = position.relative(direction);

            BlockState torchBlockState;

            if (direction.equals(Direction.UP)) {
                torchBlockState = Blocks.TORCH.defaultBlockState();
            } else if (direction.equals(Direction.DOWN)) {
                return InteractionResult.PASS;
            } else {
                torchBlockState = Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, direction);
            }

            if (level.getBlockState(position).is(Blocks.AIR) && state.isFaceSturdy(level, position, direction)) {
                level.setBlock(position, torchBlockState, 0);
            } else {
                return InteractionResult.FAIL;
            }

            if (!level.isClientSide && context.getPlayer() != null) {
                tool.hurtAndBreak(10, context.getPlayer(), (player) -> {
                    player.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                });
            }
        }

        return InteractionResult.FAIL;
    }

    public InteractionResult useOnHoe(UseOnContext context) {
        ItemStack hoe = context.getItemInHand();
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();

        int durability = this.getMaxDamage(hoe) - (int) NBTUtils.getFloatOrAddKey(hoe, "Damage");

        if (durability <= 1) {
            return InteractionResult.PASS;
        }

        Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = HoeLevelableTool.TILLABLES.get(level.getBlockState(blockpos).getBlock());
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

                    addExp(hoe, level, blockpos, context.getPlayer());

                    return InteractionResult.sidedSuccess(level.isClientSide);
                } else {
                    return InteractionResult.PASS;
                }
            }
        }

        return InteractionResult.PASS;
    }
}

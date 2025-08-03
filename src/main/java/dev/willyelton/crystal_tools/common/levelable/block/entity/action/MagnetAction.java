package dev.willyelton.crystal_tools.common.levelable.block.entity.action;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.common.levelable.block.entity.ActionBlockEntity;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: Not supplying a range (or anything) in the codec should fallback to this#getDefaultParameters
public class MagnetAction extends Action {
    private final Set<Integer> pullingEntities;

    public MagnetAction(ActionBlockEntity blockEntity, @Nullable ActionParameters params) {
        super(blockEntity, params);

        pullingEntities = new HashSet<>();
    }

    @Override
    public void tickAction(@NotNull Level level, BlockPos pos, BlockState state) {
        Vec3 centerPos = pos.getCenter();
        ItemStack stack = getItem();

        if (ToolUtils.isBroken(stack) || stack.getOrDefault(DataComponents.DISABLED, false)) return;

        if (level.getGameTime() % 20 == 6) {
            float range = params.range() + stack.getOrDefault(DataComponents.MAGNET_RANGE, 0.0F);
            List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, AABB.ofSize(centerPos, range * 2, range * 2, range * 2));
            pullingEntities.clear();
            pullingEntities.addAll(itemEntities.stream().map(ItemEntity::getId).collect(Collectors.toSet()));
        }

        Iterator<Integer> iterator = pullingEntities.iterator();
        while (iterator.hasNext()) {
            Integer entityId = iterator.next();
            Entity entity = level.getEntity(entityId);

            if (entity instanceof ItemEntity itemEntity) {
                itemEntity.push(centerPos.subtract(itemEntity.position()).normalize().scale(getSpeed(stack)));

                if (canPickup(pos, itemEntity, stack)) {
                    if (storeItem(level, pos, itemEntity)) {
                        if (stack.isDamageableItem()) {
                            stack.hurtAndBreak(Mth.ceil(params.durabilityModifier()), (ServerLevel) level, null, item -> {});
                        }

                        if (stack.getItem() instanceof LevelableItem levelableItem && CrystalToolsConfig.LEVEL_ITEMS_IN_PEDESTAL.get()) {
                            levelableItem.addExp(stack, level, pos, null);
                        }

                        itemEntity.discard();
                        iterator.remove();
                    }
                }
            } else {
                iterator.remove();
            }
        }
    }

    private boolean canPickup(BlockPos pos, ItemEntity itemEntity, ItemStack magnetStack) {
        return magnetStack.getOrDefault(DataComponents.INSTANT_PICKUP, false) || itemEntity.distanceToSqr(pos.getCenter()) <= 2.3F;
    }

    private boolean storeItem(Level level, BlockPos pos, ItemEntity itemEntity) {
        IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        if (itemHandler != null) {
            ItemStack leftOver = ItemHandlerHelper.insertItemStacked(itemHandler, itemEntity.getItem(), false);

            if (leftOver.isEmpty()) {
                return true;
            } else {
                itemEntity.setItem(leftOver);
                return false;
            }
        }

        return false;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.MAGNET;
    }

    @Override
    public ActionParameters getDefaultParameters() {
        return new ActionParameters(5, 5, 5);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    private float getSpeed(ItemStack stack) {
        return 0.01F + stack.getOrDefault(DataComponents.ITEM_SPEED, 0.0F);
    }
}

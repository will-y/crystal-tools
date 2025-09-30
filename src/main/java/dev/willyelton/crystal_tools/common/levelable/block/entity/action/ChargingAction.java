package dev.willyelton.crystal_tools.common.levelable.block.entity.action;

import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.inventory.PortableGeneratorInventory;
import dev.willyelton.crystal_tools.common.levelable.PortableGenerator;
import dev.willyelton.crystal_tools.common.levelable.block.entity.ActionBlockEntity;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChargingAction extends Action {
    private final Set<Integer> chargingEntities;

    public ChargingAction(ActionBlockEntity blockEntity, @Nullable ActionParameters params) {
        super(blockEntity, params);

        chargingEntities = new HashSet<>();
    }

    @Override
    public ActionType getActionType() {
        return ActionType.CHARGE;
    }

    @Override
    public ActionParameters getDefaultParameters() {
        return new ActionParameters(1, -1, 10);
    }

    @Override
    public void tickAction(@NotNull Level level, BlockPos pos, BlockState state) {
        Vec3 centerPos = pos.getCenter();
        ItemStack stack = getItem();

        if (level.getGameTime() % 100 == 32) {
            List<Entity> entities = level.getEntitiesOfClass(Entity.class, AABB.ofSize(centerPos, params.range() * 2, params.range() * 2, params.range() * 2));
            chargingEntities.clear();
            chargingEntities.addAll(entities.stream().map(Entity::getId).collect(Collectors.toSet()));
        }

        if (!stack.isEmpty()) {
            PortableGenerator.tick(stack, level, getEntitiesToCharge(level), pos, () -> getItemStack(stack, level, pos), CrystalToolsConfig.LEVEL_ITEMS_IN_PEDESTAL.get());
        }
    }

    private ItemStack getItemStack(ItemStack stack, Level level, BlockPos pos) {
        ItemStack result = ItemStack.EMPTY;
        IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        if (itemHandler instanceof IItemHandlerModifiable modifiable) {
            result = ItemStackUtils.nextFuelItem(modifiable, burnStack -> PortableGeneratorInventory.canBurn(stack, burnStack, level));
        }

        if (result.isEmpty()) {
            return PortableGenerator.getNextFuelStack(stack, level);
        }

        return result;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    private List<Entity> getEntitiesToCharge(Level level) {
        List<Entity> result = new ArrayList<>();

        Iterator<Integer> iterator = chargingEntities.iterator();
        while (iterator.hasNext()) {
            int entityId = iterator.next();
            Entity entity = level.getEntity(entityId);
            if (entity != null) {
                result.add(entity);
            } else {
                iterator.remove();
            }
        }

        return result;
    }
}

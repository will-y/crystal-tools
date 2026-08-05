package dev.willyelton.crystal.tools.common.levelable.block.entity;

import dev.willyelton.crystal.core.common.block.entity.BaseContainerBlockEntity;
import dev.willyelton.crystal.tools.ModRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class CrystalChestBlockEntity extends BaseContainerBlockEntity implements LidBlockEntity {

    public static final int WIDTH = 18;
    public static final int HEIGHT = 9;

    public CrystalChestBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModRegistration.CRYSTAL_CHEST_BLOCK_ENTITY.get(), worldPosition, blockState,
                Component.translatable("container.crystal_tools.crystal_chest"),
                WIDTH, HEIGHT);

    }

    public ResourceHandler<ItemResource> getItemHandlerCapForSide(Direction side) {
        return getItemHandler();
    }

    @Override
    public float getOpenNess(float a) {
        return 0;
    }
}

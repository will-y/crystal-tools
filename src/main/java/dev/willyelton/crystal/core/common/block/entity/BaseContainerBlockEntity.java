package dev.willyelton.crystal.core.common.block.entity;

import dev.willyelton.crystal.core.common.inventory.container.CrystalContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jspecify.annotations.Nullable;

public abstract class BaseContainerBlockEntity extends BlockEntity implements MenuProvider {
    private final Component displayName;
    protected final ItemStacksResourceHandler itemHandler;

    private final int width;
    private final int height;

    public BaseContainerBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState,
                                    Component displayName, int width, int height) {
        super(type, worldPosition, blockState);

        this.displayName = displayName;
        this.width = width;
        this.height = height;

        itemHandler = new ItemStacksResourceHandler(NonNullList.withSize(width * height, ItemStack.EMPTY));
    }

    @Override
    public Component getDisplayName() {
        return displayName;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.getItemHandler().deserialize(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.getItemHandler().serialize(output);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new CrystalContainerMenu(containerId, player.getInventory(), worldPosition);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ItemStacksResourceHandler getItemHandler() {
        return itemHandler;
    }
}

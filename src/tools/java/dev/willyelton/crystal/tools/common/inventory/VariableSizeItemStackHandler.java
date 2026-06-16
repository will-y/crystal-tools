package dev.willyelton.crystal.tools.common.inventory;

import dev.willyelton.crystal.core.utils.TransferUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.List;
import java.util.function.Predicate;

public class VariableSizeItemStackHandler extends ItemStacksResourceHandler {

    private int currentSize = 1;
    private final String name;
    private final Predicate<ItemStack> isValid;
    private final BlockEntity blockEntity;

    public VariableSizeItemStackHandler(int maxSize, String serializationName, Predicate<ItemStack> isValid, BlockEntity blockEntity) {
        super(maxSize);

        this.name = serializationName;
        this.isValid = isValid;
        this.blockEntity = blockEntity;
    }

    public void load(List<ItemStack> items) {
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            if (item != null && !item.isEmpty()) {
                this.set(i, ItemResource.of(item), item.getCount());
            }
        }
    }

    public void dropAll(ServerLevel level, BlockPos pos) {
        Containers.dropContents(level, pos, this.copyToList());
        TransferUtils.clear(this);
    }

    @Override
    protected int getCapacity(int index, ItemResource resource) {
        if (index >= currentSize) {
            return 0;
        }

        return super.getCapacity(index, resource);
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public void serialize(ValueOutput output) {
        output.store(name, codec, stacks);
    }

    @Override
    public void deserialize(ValueInput input) {
        input.read(name, codec).ifPresent(this::setStacks);
    }

    public void setCurrentSize(int size) {
        this.currentSize = size;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return isValid.test(resource.toStack());
    }

    @Override
    protected void onContentsChanged(int index, ItemStack previousContents) {
        blockEntity.setChanged();
    }
}

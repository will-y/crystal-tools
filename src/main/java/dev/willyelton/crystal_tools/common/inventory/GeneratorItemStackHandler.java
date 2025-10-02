package dev.willyelton.crystal_tools.common.inventory;

import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalGeneratorBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class GeneratorItemStackHandler extends ItemStacksResourceHandler {
    private final CrystalGeneratorBlockEntity generatorBlockEntity;

    public GeneratorItemStackHandler(NonNullList<ItemStack> stacks, CrystalGeneratorBlockEntity generatorBlockEntity) {
        super(stacks);

        this.generatorBlockEntity = generatorBlockEntity;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return generatorBlockEntity.getBurnDuration(resource.toStack()) > 0;
    }
}

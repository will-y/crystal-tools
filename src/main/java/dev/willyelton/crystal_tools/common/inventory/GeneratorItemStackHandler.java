package dev.willyelton.crystal_tools.common.inventory;

import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalGeneratorBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class GeneratorItemStackHandler extends ItemStackHandler {
    private final CrystalGeneratorBlockEntity generatorBlockEntity;

    public GeneratorItemStackHandler(NonNullList<ItemStack> stacks, CrystalGeneratorBlockEntity generatorBlockEntity) {
        super(stacks);

        this.generatorBlockEntity = generatorBlockEntity;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return generatorBlockEntity.getBurnDuration(stack) > 0;
    }
}

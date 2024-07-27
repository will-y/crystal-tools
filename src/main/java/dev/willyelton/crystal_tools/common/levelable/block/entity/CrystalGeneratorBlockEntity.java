package dev.willyelton.crystal_tools.common.levelable.block.entity;

import dev.willyelton.crystal_tools.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CrystalGeneratorBlockEntity extends LevelableBlockEntity implements MenuProvider {
    public static final int DATA_SIZE = 2;
    private static final int SIZE = 1;

    private NonNullList<ItemStack> items;
    private IItemHandler fuelSlots;

    private int litTime;
    private int litTotalTime;

    public CrystalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.CRYSTAL_GENERATOR_BLOCK_ENTITY.get(), pos, state);
        items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        fuelSlots = new ItemStackHandler(items);
    }

    public IItemHandler getItemHandlerCapForSide(Direction side) {
        // TODO: Catalyst different side?
        return fuelSlots;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.crystal_tools.crystal_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return null;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, this.items, registries);

        this.litTime = tag.getInt("LitTime");
        this.litTotalTime = tag.getInt("LitTotalTime");
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        ItemContainerContents contents = componentInput.get(DataComponents.CONTAINER);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        this.fuelSlots = new ItemStackHandler(items);
        if (contents != null) {
            contents.copyInto(this.items);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.putInt("LitTime", this.litTime);
        tag.putInt("LitTotalTime", this.litTotalTime);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        ItemContainerContents contents = ItemContainerContents.fromItems(this.items);
        components.set(DataComponents.CONTAINER, contents);
    }

    @Override
    protected void addToExtraData(String key, float value) {

    }

    protected final ContainerData dataAccess = new LevelableContainerData(this) {
        @Override
        int getExtra(int index) {
            return switch (index) {
                case 3 -> litTime;
                case 4 -> litTotalTime;
                default -> 0;
            };
        }

        @Override
        void setExtra(int index, int value) {
            switch (index) {
                case 3 -> litTime = value;
                case 4 -> litTotalTime = value;
            }
        }

        @Override
        public int getCount() {
            return DATA_SIZE;
        }
    };
}

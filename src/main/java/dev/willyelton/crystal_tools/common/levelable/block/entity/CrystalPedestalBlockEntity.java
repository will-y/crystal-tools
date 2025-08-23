package dev.willyelton.crystal_tools.common.levelable.block.entity;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.datamap.ActionData;
import dev.willyelton.crystal_tools.common.datamap.DataMaps;
import dev.willyelton.crystal_tools.common.inventory.CallbackItemStackHandler;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalPedestalContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalPedestalBlock;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.PedestalClientData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class CrystalPedestalBlockEntity extends ActionBlockEntity implements MenuProvider {
    private final PedestalClientData clientData = new PedestalClientData();
    private final IItemHandler catalystHandler;
    private final IItemHandler contentsHandler;
    private final NonNullList<ItemStack> catalystStacks = NonNullList.withSize(1, ItemStack.EMPTY);
    private final NonNullList<ItemStack> contentsStacks = NonNullList.withSize(27, ItemStack.EMPTY);

    public CrystalPedestalBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.CRYSTAL_PEDESTAL_BLOCK_ENTITY.get(), pos, blockState);

        catalystHandler = new CallbackItemStackHandler(catalystStacks, this::setStack);
        contentsHandler = new ItemStackHandler(contentsStacks);
    }

    public IItemHandler getItemHandlerCapForSide(Direction side) {
        if (this.getBlockState().getValue(CrystalPedestalBlock.FACING) == side) {
            return catalystHandler;
        } else {
            return contentsHandler;
        }
    }

    public IItemHandler getContentsHandler() {
        return contentsHandler;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }


    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("item")) {
            ItemStack.OPTIONAL_CODEC.decode(NbtOps.INSTANCE, tag.getCompound("item")).ifSuccess(p -> {
                this.catalystStacks.set(0, p.getFirst());
            });
        }

        ContainerHelper.loadAllItems(tag, this.contentsStacks, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ItemStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, this.catalystStacks.getFirst()).ifSuccess(p -> {
            tag.put("item", p);
        });

        ContainerHelper.saveAllItems(tag, this.contentsStacks, registries);
    }

    public void clientTick(Level level, BlockPos pos, BlockState state) {
        this.clientData.update();
    }

    /**
     * Sets the itemstack currently in the pedestal.
     * @param stack The stack to insert into the pedestal.
     * @return If there was a stack in this pedestal, it is returned. Otherwise, an empty stack is returned.
     */
    public ItemStack setStack(ItemStack stack) {
        if (this.level == null) return stack;

        ItemStack storedStack = this.catalystStacks.getFirst();
        this.catalystStacks.set(0, stack);
        setStack(stack, storedStack);
        return storedStack;
    }

    private void setStack(ItemStack newStack, ItemStack oldStack) {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }

        this.setActions(newStack, oldStack);
        this.setChanged();
    }

    private void setActions(ItemStack newStack, ItemStack oldStack) {
        ActionData oldActionData = oldStack.getItemHolder().getData(DataMaps.PEDESTAL_ACTIONS);
        if (oldActionData != null) {
            this.removeAction(oldActionData.type());
        }

        ActionData actionData = newStack.getItemHolder().getData(DataMaps.PEDESTAL_ACTIONS);
        if (actionData != null) {
            this.addAction(actionData.type().getActionInstance(this, actionData.params()), actionData.params());
        }
    }

    public @Nullable ItemStack getStack() {
        return this.catalystStacks.getFirst();
    }

    public PedestalClientData getClientData() {
        return this.clientData;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.crystal_tools.crystal_pedestal");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CrystalPedestalContainerMenu(containerId, playerInventory, this.getBlockPos());
    }

    public void dropContents(Level level, BlockPos pos) {
        Containers.dropContents(level, pos, catalystStacks);
        Containers.dropContents(level, pos, contentsStacks);
    }
}

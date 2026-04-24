package dev.willyelton.crystal_tools.api.common.block.entity;

import dev.willyelton.crystal_tools.api.common.block.entity.model.SideConfigOption;
import dev.willyelton.crystal_tools.api.common.datacomponent.DataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static dev.willyelton.crystal_tools.api.common.block.entity.model.SideConfigOption.DIRECTION_MAP_CODEC;

public abstract class SideConfigBlockEntity extends LevelableBlockEntity {
    private Map<Direction, SideConfigOption> configs = new HashMap<>();
    private final Set<SideConfigOption> supportedConfigs;

    public SideConfigBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        for (Direction direction : Direction.values()) {
            configs.put(direction, defaultForSide(direction));
        }

        this.supportedConfigs = SideConfigOption.all();
    }

    public void setSideConfig(Direction side, SideConfigOption option) {
        configs.put(side, option);
        setChanged();
    }

    public Map<Direction, SideConfigOption> getSideConfigs() {
        return configs;
    }

    public Set<SideConfigOption> supportedSideConfigOptions() {
        return supportedConfigs;
    }

    public ResourceHandler<ItemResource> getCapForSide(Direction side) {
        SideConfigOption option = configs.get(side);
        return getHandlerForConfig(Objects.requireNonNullElse(option, SideConfigOption.NONE));
    }

    protected abstract ResourceHandler<ItemResource> getHandlerForConfig(SideConfigOption option);

    protected abstract SideConfigOption defaultForSide(Direction side);

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);

        configs = new HashMap<>(valueInput.read("side_config_options", DIRECTION_MAP_CODEC).orElse(configs));
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentInput) {
        super.applyImplicitComponents(componentInput);

        configs = new HashMap<>(componentInput.getOrDefault(DataComponents.SIDE_CONFIG_OPTIONS, configs));
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);

        valueOutput.store("side_config_options", DIRECTION_MAP_CODEC,  configs);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        components.set(DataComponents.SIDE_CONFIG_OPTIONS, configs);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    public void saveCustomOnly(ValueOutput output) {
        output.store("side_config_options", DIRECTION_MAP_CODEC, configs);
    }

    protected Collection<Direction> getAllSidesOfType(SideConfigOption option) {
        return this.configs.entrySet().stream()
                .filter(entry -> entry.getValue() == option)
                .map(Map.Entry::getKey)
                .toList();
    }
}

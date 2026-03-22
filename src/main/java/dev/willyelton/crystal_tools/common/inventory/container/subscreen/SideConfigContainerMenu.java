package dev.willyelton.crystal_tools.common.inventory.container.subscreen;

import dev.willyelton.crystal_tools.common.levelable.block.entity.SideConfigBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.SideConfigOption;
import dev.willyelton.crystal_tools.common.network.data.UpdateSideConfigPayload;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public interface SideConfigContainerMenu {

    default void setSideConfig(Direction side, SideConfigOption option) {
        getSideConfigBlockEntity().setSideConfig(side, option);
        ClientPacketDistributor.sendToServer(new UpdateSideConfigPayload(getSideConfigBlockEntity().getBlockPos(), side, option));
    }

    default SideConfigOption getSideConfig(Direction side) {
        return getSideConfigBlockEntity().getSideConfigs().get(side);
    }

    default SideConfigOption next(SideConfigOption option) {
        return option.next(getSideConfigBlockEntity().supportedSideConfigOptions());
    }

    default boolean supports(SideConfigOption option) {
        return getSideConfigBlockEntity().supportedSideConfigOptions().contains(option);
    }

    SideConfigBlockEntity getSideConfigBlockEntity();
}

package dev.willyelton.crystal_tools.api.common.inventory.container;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface CrystalToolsMenuProvider {
    void openContainer(Level level, BlockPos pos, Player player);
}

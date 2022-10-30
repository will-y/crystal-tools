package dev.willyelton.crystal_tools.item.tool;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PickaxeLevelableTool extends DiggerLevelableTool {
    public PickaxeLevelableTool() {
        super(new Item.Properties(), BlockTags.MINEABLE_WITH_PICKAXE, "pickaxe");
    }

    public InteractionResult useOn(UseOnContext context) {
        if (this.isDisabled()) {
            context.getItemInHand().shrink(1);
            return InteractionResult.FAIL;
        }

        return ToolUseUtils.useOnTorch(context, this);
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_PICKAXE.get();
    }
}

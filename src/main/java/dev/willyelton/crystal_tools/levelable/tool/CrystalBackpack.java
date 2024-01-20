package dev.willyelton.crystal_tools.levelable.tool;

import dev.willyelton.crystal_tools.levelable.LevelableItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CrystalBackpack extends Item implements LevelableItem {

    public CrystalBackpack() {
        super(new Item.Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        // TODO: Open inventory

        return super.use(level, player, usedHand);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack tool, Level level, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity entity) {
        // If this tool is disabled break on use
        if (this.isDisabled()) {
            tool.shrink(1);
            return false;
        }

        return true;
    }

    @Override
    public String getItemType() {
        return "backpack";
    }

    @Override
    public int getMaxDamage(ItemStack itemStack) {
        return 1;
    }

    @Override
    public boolean isDisabled() {
        // TODO: Config
        return false;
    }
}

package dev.willyelton.crystal_tools;

import dev.willyelton.crystal_tools.levelable.tool.ModTools;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CreativeTabs {
    public static final CreativeModeTab CRYSTAL_TOOLS_TAB = new CreativeModeTab("crystal_tools") {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(ModTools.CRYSTAL_PICKAXE.get());
        }
    };

}

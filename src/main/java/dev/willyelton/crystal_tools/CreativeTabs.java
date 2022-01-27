package dev.willyelton.crystal_tools;

import dev.willyelton.crystal_tools.item.tool.ModTools;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CreativeTabs {
    public static final CreativeModeTab CRYSTAL_TOOLS_TAB = new CreativeModeTab("crystal_tools") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModTools.CRYSTAL_PICKAXE.get());
        }
    };

}

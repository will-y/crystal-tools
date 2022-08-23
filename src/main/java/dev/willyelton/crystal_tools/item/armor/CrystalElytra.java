package dev.willyelton.crystal_tools.item.armor;

import dev.willyelton.crystal_tools.CreativeTabs;
import dev.willyelton.crystal_tools.item.LevelableItem;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class CrystalElytra extends ElytraItem implements LevelableItem {
    public CrystalElytra(Properties pProperties) {
        super(pProperties.tab(CreativeTabs.CRYSTAL_TOOLS_TAB));
    }

    @Override
    public String getItemType() {
        return "crystal_elytra";
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return super.getMaxDamage(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        ToolUtils.appendHoverText(itemStack, level, components, flag, this);
    }

    @Override
    public  EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }
}

package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;

import static dev.willyelton.crystal_tools.common.levelable.LevelableItem.CRYSTAL;

public class CrystalSpear extends Item {
    public CrystalSpear(Item.Properties properties) {
        super(properties
                .durability(CRYSTAL.durability())
                .fireResistant()
                .repairable(CrystalToolsTags.REPAIRS_CRYSTAL)
                .spear(ToolMaterial.NETHERITE, 1.15F, 1.2F, 0.4F, 2.5F, 9.0F, 5.5F, 5.1F, 8.75F, 4.6F));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int dashLevel = stack.getOrDefault(DataComponents.DASH, 0);

        if (dashLevel > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 100, dashLevel - 1, false, false, false));
        }

        return super.use(level, player, hand);
    }
}

package dev.willyelton.crystal_tools.common.levelable.condition;

import dev.willyelton.crystal_tools.ModRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public class WolfCondition implements LevelableEntityCondition {
    @Override
    public boolean check(LivingEntity livingEntity, Player player) {
        if (livingEntity instanceof Wolf wolf) {
            return wolf.isOwnedBy(player) && wolf.getData(ModRegistration.WOLF_COLLAR);
        }

        return false;
    }

    @Override
    public Identifier id() {
        return rl("can_level_wolf");
    }
}

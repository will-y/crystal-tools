package dev.willyelton.crystal_tools.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;

public class CrystalTridentEntity extends ThrownTrident {
    public CrystalTridentEntity(EntityType<? extends CrystalTridentEntity> entityType, Level level) {
        super(entityType, level);
    }
}

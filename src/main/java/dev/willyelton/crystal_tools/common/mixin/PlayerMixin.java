package dev.willyelton.crystal_tools.common.mixin;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: Remove in 1.22 when MC has a cooldown datacomponent
@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    // Does nothing, just makes the compiler happy
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemCooldowns getCooldowns();

    @Inject(method = "disableShield", at = @At("HEAD"), cancellable = true)
    private void handleCrystalShieldBlockingCooldown(CallbackInfo ci) {
        ItemStack usingItem = this.getUseItem();
        int blockCooldownReduction = usingItem.getOrDefault(DataComponents.BLOCK_COOLDOWN_REDUCTION, 0);

        if (blockCooldownReduction > 0) {
            if (blockCooldownReduction < 100) {
                this.getCooldowns().addCooldown(usingItem.getItem(), 100 - blockCooldownReduction);
                this.stopUsingItem();
                this.level().broadcastEntityEvent(this, (byte) 30);
            }

            ci.cancel();
        }
    }
}
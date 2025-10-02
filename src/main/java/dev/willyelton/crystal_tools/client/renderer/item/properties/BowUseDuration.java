package dev.willyelton.crystal_tools.client.renderer.item.properties;

import com.mojang.serialization.MapCodec;
import dev.willyelton.crystal_tools.common.levelable.tool.BowLevelableItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record BowUseDuration() implements RangeSelectItemModelProperty {
    public static final MapCodec<BowUseDuration> MAP_CODEC = MapCodec.unit(new BowUseDuration());

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner itemOwner, int seed) {
        if (itemOwner != null) {
            LivingEntity entity = itemOwner.asLivingEntity();

            if (entity != null && entity.getUseItem() == stack) {
                int timeUsed = UseDuration.useDuration(stack, entity);
                float maxTime = BowLevelableItem.getChargeTime(stack);

                return timeUsed / maxTime;
            }
        }

        return 0F;
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}

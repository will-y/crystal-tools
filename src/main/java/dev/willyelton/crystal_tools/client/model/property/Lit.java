package dev.willyelton.crystal_tools.client.model.property;

import com.mojang.serialization.MapCodec;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.datamap.GeneratorFuelData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record Lit() implements ConditionalItemModelProperty {
    public static final MapCodec<Lit> MAP_CODEC = MapCodec.unit(Lit::new);

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return stack.getOrDefault(DataComponents.BURNING_ITEM_DATA, new GeneratorFuelData(0, 0)).burnTime() > 0;
    }
}

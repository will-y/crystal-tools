package dev.willyelton.crystal_tools.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.levelable.LevelableItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

public class ItemDisabledCondition implements ICondition {
    public static final ResourceLocation NAME = new ResourceLocation("crystal_tools", "item_disabled");
    public static final MapCodec<? extends ICondition> ITEM_DISABLED_CODEC = MapCodec.assumeMapUnsafe(RecordCodecBuilder.<ItemDisabledCondition>create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("itemLocation").forGetter(ItemDisabledCondition::getItemLocation)
    ).apply(instance, ItemDisabledCondition::new)));

    private final ResourceLocation itemLocation;

    public ItemDisabledCondition(ResourceLocation item) {
        this.itemLocation = item;
    }

    @Override
    public boolean test(IContext context) {
        Item item = BuiltInRegistries.ITEM.get(itemLocation);

        if (item instanceof LevelableItem levelableItem) {
            return levelableItem.isDisabled();
        } else {
            return false;
        }
    }

    // TODO: No way this works. Need to register this codec
    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return ITEM_DISABLED_CODEC;
    }

    public ResourceLocation getItemLocation() {
        return itemLocation;
    }

//    public static class Serializer implements IConditionSerializer<ItemDisabledCondition> {
//        @Override
//        public void write(JsonObject json, ItemDisabledCondition value) {
//            json.addProperty("item", value.itemLocation.toString());
//        }
//
//        @Override
//        public ItemDisabledCondition read(JsonObject json) {
//            return new ItemDisabledCondition(new ResourceLocation(GsonHelper.getAsString(json, "item")));
//        }
//
//        @Override
//        public ResourceLocation getID() {
//            return NAME;
//        }
//    }
}

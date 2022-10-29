package dev.willyelton.crystal_tools.crafting;

import com.google.gson.JsonObject;
import dev.willyelton.crystal_tools.item.LevelableItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemDisabledCondition implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation("crystal_tools", "item_disabled");
    private final ResourceLocation itemLocation;

    public ItemDisabledCondition(ResourceLocation item) {
        this.itemLocation = item;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        Item item = ForgeRegistries.ITEMS.getValue(itemLocation);

        if (item instanceof LevelableItem levelableItem) {
            return levelableItem.isDisabled();
        } else {
            return false;
        }
    }

    public static class Serializer implements IConditionSerializer<ItemDisabledCondition> {
        @Override
        public void write(JsonObject json, ItemDisabledCondition value) {
            json.addProperty("item", value.itemLocation.toString());
        }

        @Override
        public ItemDisabledCondition read(JsonObject json) {
            return new ItemDisabledCondition(new ResourceLocation(GsonHelper.getAsString(json, "item")));
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}

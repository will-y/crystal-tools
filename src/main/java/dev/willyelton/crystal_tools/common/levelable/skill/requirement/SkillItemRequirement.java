package dev.willyelton.crystal_tools.common.levelable.skill.requirement;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.CodecUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class SkillItemRequirement implements SkillDataRequirement {
    private final List<Item> items;
    public SkillItemRequirement(String[] items) {
        List<Item> itemList = new ArrayList<>();
        for (String item : items) {
            ResourceLocation r = ResourceLocation.parse(item);
            itemList.add(BuiltInRegistries.ITEM.get(r));
        }

        this.items = itemList;
    }

    public SkillItemRequirement(List<Item> items) {
        this.items = items;
    }

    @Override
    public boolean canLevel(SkillData data, Player player) {
        // If config is disabled, always allow
        if (!CrystalToolsConfig.ENABLE_ITEM_REQUIREMENTS.get()) return true;

        for (Item item : items) {
            if (!hasItem(player, item)) {
                return false;
            }
        }

        return true;
    }

    public boolean hasItem(Player player, Item item) {
        Inventory inventory = player.getInventory();

        return inventory.contains(item.getDefaultInstance());
    }

    @Override
    public RequirementType getRequirementType() {
        return RequirementType.ITEM;
    }

    @Override
    public JsonElement toJson() {
        return CodecUtils.encodeOrThrow(CODEC, this);
    }

    public List<Item> getItems() {
        return this.items;
    }

    public static final Codec<SkillItemRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().listOf().fieldOf("item").forGetter(SkillItemRequirement::getItems)
    ).apply(instance, SkillItemRequirement::new));
}

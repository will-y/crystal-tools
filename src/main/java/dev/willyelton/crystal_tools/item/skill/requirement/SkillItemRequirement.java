package dev.willyelton.crystal_tools.item.skill.requirement;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.item.skill.SkillData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class SkillItemRequirement implements SkillDataRequirement {
    private final List<Item> items;
    public SkillItemRequirement(String[] items) {
        List<Item> itemList = new ArrayList<>();
        for (String item : items) {
            ResourceLocation r = new ResourceLocation(item);
            itemList.add(ForgeRegistries.ITEMS.getValue(r));
        }

        this.items = itemList;
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

    public List<Item> getItems() {
        return this.items;
    }
}

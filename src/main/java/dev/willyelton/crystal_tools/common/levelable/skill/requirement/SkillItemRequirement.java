package dev.willyelton.crystal_tools.common.levelable.skill.requirement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.List;

public class SkillItemRequirement implements SkillDataRequirement {
    public static final MapCodec<SkillItemRequirement> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().listOf().fieldOf("item").forGetter(SkillItemRequirement::getItems)
    ).apply(instance, SkillItemRequirement::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SkillItemRequirement> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(BuiltInRegistries.ITEM.key()).apply(ByteBufCodecs.list()), SkillItemRequirement::getItems,
            SkillItemRequirement::new);

    private final List<Item> items;

    public SkillItemRequirement(List<Item> items) {
        this.items = items;
    }

    @Override
    public boolean canLevel(SkillPoints points, Player player) {
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
    public MapCodec<? extends SkillDataRequirement> codec() {
        return MAP_CODEC;
    }

    public List<Item> getItems() {
        return this.items;
    }
}

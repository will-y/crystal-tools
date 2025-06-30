package dev.willyelton.crystal_tools.utils;

import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RegistryUtils {
    private RegistryUtils() {}

    /**
     * Finds an stack's skill data if it exists
     * @param stack The stack to find the skill data for
     * @param registryAccess Registry access
     * @return The stack's SkillData instance if it exists, null otherwise
     */
    public static @Nullable SkillData getSkillData(ItemStack stack, RegistryAccess registryAccess) {
        Optional<Registry<SkillData>> skillDataOptional = registryAccess.lookup(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS);

        if (skillDataOptional.isPresent()) {
            Registry<SkillData> skillDataRegistry = skillDataOptional.get();
            ResourceKey<?> resourceKey = stack.getItemHolder().getKey();

            if (resourceKey != null) {
                Optional<Holder.Reference<SkillData>> dataOptional = skillDataRegistry.get(resourceKey.location());

                if (dataOptional.isPresent()) {
                    return dataOptional.get().value();
                }
            }

        }

        return null;
    }
}

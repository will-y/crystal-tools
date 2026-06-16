package dev.willyelton.crystal.core.utils;

import dev.willyelton.crystal.core.common.event.DatapackRegistryEvents;
import dev.willyelton.crystal.core.common.skill.SkillData;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

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
            ResourceKey<?> resourceKey = stack.typeHolder().getKey();

            if (resourceKey != null) {
                Optional<Holder.Reference<SkillData>> dataOptional = skillDataRegistry.get(resourceKey.identifier());

                if (dataOptional.isPresent()) {
                    return dataOptional.get().value();
                }
            }

        }

        return null;
    }

    public static <T> @Nullable T getFromRegistry(RegistryAccess registryAccess, ResourceKey<Registry<T>> registryKey, Identifier entryKey) {
        var registryOptional = registryAccess.lookup(registryKey);
        if (registryOptional.isPresent()) {
            var registry = registryOptional.get();
            return registry.getValue(entryKey);
        }

        return null;
    }
}

package dev.willyelton.crystal.core.common.registry;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.LinkedList;
import java.util.List;

public class SkillTreeBlackListRegistry {
    private static final List<SkillTreeBlackListPredicate> PREDICATES = new LinkedList<>();

    /// Returns true if the given item stack has a skill tree registered
    /// but should not be enabled (due to a config for example).
    public static boolean skillTreeBlackListed(ItemStack stack) {
        for (SkillTreeBlackListPredicate predicate : PREDICATES) {
            if (predicate.test(stack)) {
                return true;
            }
        }

        return false;
    }

    @ApiStatus.Internal
    public static void addSkillTreeBlackListRule(SkillTreeBlackListPredicate predicate) {
        PREDICATES.add(predicate);
    }

    @FunctionalInterface
    public interface SkillTreeBlackListPredicate {
        boolean test(ItemStack stack);
    }
}

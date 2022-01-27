package dev.willyelton.crystal_tools.item;

import dev.willyelton.crystal_tools.CreativeTabs;
import dev.willyelton.crystal_tools.item.skill.SkillData;
import dev.willyelton.crystal_tools.item.skill.SkillDataNode;
import dev.willyelton.crystal_tools.item.skill.SkillNodeType;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class LevelableItem extends Item {
    private static final int AUTO_REPAIR_COUNTER = 50;
    private static final int BASE_EXPERIENCE_CAP = 50;
    private static final float EXPERIENCE_CAP_MULTIPLIER = 1.25F;

    // Just used for default values, just at netherite for now
    protected static final Tier tier = Tiers.NETHERITE;

    protected final String itemType;

    public LevelableItem(Properties properties, String itemType) {
        super(properties.defaultDurability(tier.getUses()).fireResistant().tab(CreativeTabs.CRYSTAL_TOOLS_TAB));
        this.itemType = itemType;
    }

    // From TierdItem.java
    @Override
    public int getEnchantmentValue() {
        return tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack tool, @NotNull ItemStack repairItem) {
        return repairItem.is(ModItems.CRYSTAL.get());
    }

    public void addExp(ItemStack tool, Level level, BlockPos blockPos) {
        addExp(tool, level, blockPos, 1);
    }

    public void addExp(ItemStack tool, Level level, BlockPos blockPos, int amount) {
        int newExperience = (int) NBTUtils.addValueToTag(tool, "experience", amount);
        int experienceCap = (int) NBTUtils.getFloatOrAddKey(tool, "experience_cap", BASE_EXPERIENCE_CAP);

        if (experienceCap == 0) {
            // fist time
            NBTUtils.setValue(tool, "experience_cap", BASE_EXPERIENCE_CAP);
            experienceCap = BASE_EXPERIENCE_CAP;
        }

        if (newExperience >= experienceCap) {
            // level up
            NBTUtils.addValueToTag(tool, "skill_points", 1);
            // copied from LivingEntity item breaking sound
            // play level up sound
            level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.8F, 0.8F + level.random.nextFloat() * 0.4F, false);
            // TODO: Add chat message thing

            NBTUtils.setValue(tool, "experience", Math.max(0, newExperience - experienceCap));
            NBTUtils.setValue(tool, "experience_cap", experienceCap * EXPERIENCE_CAP_MULTIPLIER);
        }
    }



    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        int newExperience = (int) NBTUtils.getFloatOrAddKey(itemStack, "experience");
        int experienceCap = (int) NBTUtils.getFloatOrAddKey(itemStack, "experience_cap", BASE_EXPERIENCE_CAP);

        int durability = this.getMaxDamage(itemStack) - (int) NBTUtils.getFloatOrAddKey(itemStack, "Damage");

        if (durability <= 1) {
            components.add(new TextComponent("\u00A7c\u00A7l" + "Broken"));
        }

        components.add(new TextComponent(String.format("%d/%d XP To Next Level", newExperience, experienceCap)));
        int skillPoints = (int) NBTUtils.getFloatOrAddKey(itemStack, "skill_points");
        if (skillPoints > 0) {
            components.add(new TextComponent(String.format("%d Unspent Skill Points", skillPoints)));
        }

        if (!Screen.hasShiftDown()) {
            components.add(new TextComponent("<Hold Shift For Skills>"));
        } else {
            components.add(new TextComponent("Skills:"));
            int[] points = NBTUtils.getIntArray(itemStack, "points");
            SkillData toolData = SkillData.fromResourceLocation(new ResourceLocation("crystal_tools", String.format("skill_trees/%s.json", itemType)), points);
            for (SkillDataNode dataNode : toolData.getAllNodes()) {
                if (dataNode.isComplete()) {
                    components.add(new TextComponent("    " + dataNode.getName()));
                } else if (dataNode.getType().equals(SkillNodeType.INFINITE) && dataNode.getPoints() > 0) {
                    components.add(new TextComponent("    " + dataNode.getName() + " (" + dataNode.getPoints() + " points)"));
                }
            }
        }
    }

    // Just don't ever add the enchantment effect
    @Override
    public boolean isFoil(ItemStack itemStack) {
        return false;
    }

    // I think the int and boolean parameters are right
    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean inHand) {
        if (!inHand) {
            if (NBTUtils.getBoolean(itemStack, "auto_repair", false)) {
                if (NBTUtils.addValueToTag(itemStack, "auto_repair_counter", 1) > AUTO_REPAIR_COUNTER) {
                    NBTUtils.setValue(itemStack, "auto_repair_counter", 0);
                    int repairAmount = Math.min((int) NBTUtils.getFloatOrAddKey(itemStack, "auto_repair_amount"), itemStack.getDamageValue());
                    itemStack.setDamageValue(itemStack.getDamageValue() - repairAmount);
                }
            }
        }
    }

    // Changing these two to what they should be @minecraft
    public int getBarWidth(ItemStack itemStack) {
        return Math.round(13.0F - (float) itemStack.getDamageValue() * 13.0F / (float) itemStack.getMaxDamage());
    }

    public int getBarColor(ItemStack itemStack) {
        float f = Math.max(0.0F, ((float)itemStack.getMaxDamage() - (float)itemStack.getDamageValue()) / (float) itemStack.getMaxDamage());
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public int getMaxDamage(ItemStack itemStack) {
        int bonusDurability = (int) NBTUtils.getFloatOrAddKey(itemStack, "durability_bonus");
        return tier.getUses() + bonusDurability;
    }

    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        int durability = this.getMaxDamage(stack) - (int) NBTUtils.getFloatOrAddKey(stack, "Damage");

        if (durability - amount <= 0) {
            return 0;
        } else {
            return amount;
        }
    }

    public String getItemType() {
        return itemType;
    }
}

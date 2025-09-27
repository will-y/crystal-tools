package dev.willyelton.crystal_tools.common.levelable.block.entity.data;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import net.minecraft.world.item.ItemStack;

public abstract class ItemStackLevelableContainerData implements ILevelableContainerData {
    protected static final int DATA_SIZE = 3;

    private final ItemStack stack;

    public ItemStackLevelableContainerData(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int get(int index) {
        return switch (index) {
            case 0 -> stack.getOrDefault(DataComponents.SKILL_POINTS, 0);
            case 1 -> stack.getOrDefault(DataComponents.SKILL_EXPERIENCE, 0);
            case 2 -> stack.getOrDefault(DataComponents.EXPERIENCE_CAP, 0);
            default -> (index >= getNonSkillDataSize() && index < getCount()) ? stack.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints()).getPoints(index - getNonSkillDataSize()) : getExtra(index);
        };
    }

    protected abstract int getExtra(int index);

    @Override
    public void set(int index, int value) {
        switch(index) {
            case 0 -> stack.set(DataComponents.SKILL_POINTS, value);
            case 1 -> stack.set(DataComponents.SKILL_EXPERIENCE, value);
            case 2 -> stack.set(DataComponents.EXPERIENCE_CAP, value);
            default -> {
                if (index >= getNonSkillDataSize() && index < getCount()) {
                    SkillPoints skillPoints = stack.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints()).copy();
                    skillPoints.setPoints(index - getNonSkillDataSize(), value);
                    stack.set(DataComponents.SKILL_POINT_DATA, skillPoints);
                } else {
                    setExtra(index, value);
                }
            }
        }
    }

    protected abstract void setExtra(int index, int value);

    @Override
    public int getCount() {
        return getNonSkillDataSize() + 100;
    }

    protected abstract int getExtraDataSize();

    private int getNonSkillDataSize() {
        return getExtraDataSize() + DATA_SIZE;
    }

    @Override
    public int getSkillPoints() {
        return stack.getOrDefault(DataComponents.SKILL_POINTS, 0);
    }

    @Override
    public void addSkillPoints(int points) {
        stack.set(DataComponents.SKILL_POINTS, points + getSkillPoints());
    }

    @Override
    public void addToPoints(int nodeId, int value) {
        SkillPoints skillPoints = stack.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints()).copy();
        skillPoints.addPoints(nodeId, value);
        stack.set(DataComponents.SKILL_POINT_DATA, skillPoints);
    }

    @Override
    public int getExp() {
        return stack.getOrDefault(DataComponents.SKILL_EXPERIENCE, 0);
    }

    @Override
    public int getExpCap() {
        return stack.getOrDefault(DataComponents.EXPERIENCE_CAP, 0);
    }

    @Override
    public SkillPoints getPoints() {
        return stack.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints());
    }
}

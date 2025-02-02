package dev.willyelton.crystal_tools.common.levelable.block.entity.data;

import dev.willyelton.crystal_tools.common.levelable.block.entity.LevelableBlockEntity;

public abstract class LevelableContainerData implements ILevelableContainerData {
    protected static final int DATA_SIZE = 3;

    private final LevelableBlockEntity levelableBlockEntity;

    public LevelableContainerData(LevelableBlockEntity levelableBlockEntity) {
        this.levelableBlockEntity = levelableBlockEntity;
    }

    @Override
    public int get(int index) {
        return switch (index) {
            case 0 -> levelableBlockEntity.getSkillPoints();
            case 1 -> levelableBlockEntity.getExp();
            case 2 -> levelableBlockEntity.getExpCap();
            default -> (index >= getNonSkillDataSize() && index < getCount()) ? levelableBlockEntity.getPoint(index - getNonSkillDataSize()) : getExtra(index);
        };
    }

    protected abstract int getExtra(int index);

    @Override
    public void set(int index, int value) {
        switch(index) {
            case 0 -> levelableBlockEntity.setSkillPoints(value);
            case 1 -> levelableBlockEntity.setExp(value);
            case 2 -> levelableBlockEntity.setExpCap(value);
            default -> {
                if (index >= getNonSkillDataSize() && index < getCount()) {
                    levelableBlockEntity.setPoints(index - getNonSkillDataSize(), value);
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

    public int getSkillPoints() {
        return get(0);
    }

    public void addSkillPoints(int points) {
        set(0, this.getSkillPoints() + points);
    }

    public void addToPoints(int nodeId, int value) {
        int index = nodeId + getNonSkillDataSize();
        set(index, get(index) + value);
    }

    public int getExp() {
        return get(1);
    }

    public int getExpCap() {
        return get(2);
    }

    public int[] getPoints() {
        int[] result = new int[100];

        for (int i = 0; i < 100; i++) {
            result[i] = get(i + getNonSkillDataSize());
        }

        return result;
    }

    protected abstract int getExtraDataSize();

    private int getNonSkillDataSize() {
        return getExtraDataSize() + DATA_SIZE;
    }
}

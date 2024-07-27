package dev.willyelton.crystal_tools.common.levelable.block.entity;

import net.minecraft.world.inventory.ContainerData;

// TODO: Getters and setters for levelable things?
public abstract class LevelableContainerData implements ContainerData {
    private static final int DATA_SIZE = 3;

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
            default -> (index >= 100 && index < 200) ? levelableBlockEntity.getPoint(index - 100) : getExtra(index);
        };
    }

    abstract int getExtra(int index);

    @Override
    public void set(int index, int value) {
        switch(index) {
            case 0 -> levelableBlockEntity.setSkillPoints(value);
            case 1 -> levelableBlockEntity.setExp(value);
            case 2 -> levelableBlockEntity.setExpCap(value);
            default -> {
                if (index >= getDataSize() && index < getDataSize() + 100) {
                    levelableBlockEntity.setPoints(index - getDataSize(), value);
                } else {
                    setExtra(index, value);
                }
            }
        }
    }

    // TODO: OVerride get size, use abstract method to get number of fields it has and then just do the actual amount of points not just 200

    abstract void setExtra(int index, int value);

    abstract int getDataSize();

    private int getTotalDataSize() {
        return DATA_SIZE + getDataSize();
    }
}

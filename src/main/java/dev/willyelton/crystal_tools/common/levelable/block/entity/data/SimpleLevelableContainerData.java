package dev.willyelton.crystal_tools.common.levelable.block.entity.data;

public class SimpleLevelableContainerData implements ILevelableContainerData {
    private final int extraData;
    private final int[] data;

    public SimpleLevelableContainerData(int extraData) {
        this.extraData = extraData;
        this.data = new int[extraData + LevelableContainerData.DATA_SIZE + 100];
    }

    @Override
    public int get(int index) {
        return data[index];
    }

    @Override
    public void set(int index, int value) {
        data[index] = value;
    }

    @Override
    public int getCount() {
        return extraData + LevelableContainerData.DATA_SIZE + 100;
    }

    @Override
    public int getSkillPoints() {
        return data[0];
    }

    @Override
    public void addSkillPoints(int points) {
        set(0, this.getSkillPoints() + points);
    }

    @Override
    public void addToPoints(int nodeId, int value) {
        int index = nodeId + LevelableContainerData.DATA_SIZE + extraData;
        set(index, get(index) + value);
    }

    @Override
    public int getExp() {
        return get(1);
    }

    @Override
    public int getExpCap() {
        return get(2);
    }

    @Override
    public int[] getPoints() {
        int[] result = new int[100];

        for (int i = 0; i < 100; i++) {
            result[i] = get(i + LevelableContainerData.DATA_SIZE + extraData);
        }

        return result;
    }
}

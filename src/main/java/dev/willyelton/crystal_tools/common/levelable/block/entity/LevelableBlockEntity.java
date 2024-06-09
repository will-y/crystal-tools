package dev.willyelton.crystal_tools.common.levelable.block.entity;

import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

// TODO: Apparently unused will need to redo when I do generator
public abstract class LevelableBlockEntity extends BlockEntity {
    // Number of current points
    protected int skillPoints;
    // Array of points that are currently spent
    protected int[] points = new int[100];
    protected int exp;
    protected int expCap;

    public LevelableBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public abstract String getBlockType();

    public void addExp() {
        addExp(1);
    }

    public void addExp(int amount) {
        this.exp += amount;

        if (this.exp >= this.expCap) {
            // level up
            this.skillPoints++;
            this.exp -= this.expCap;
            this.expCap = ToolUtils.getNewCap(this.expCap, 1);
        }
    }

    // TODO: Are these not components?
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
//        this.skillPoints = (int) NBTUtils.getFloatOrAddKey(nbt, "skill_points");
//        this.exp = (int) NBTUtils.getFloatOrAddKey(nbt, "experience");
//        this.expCap = (int) NBTUtils.getFloatOrAddKey(nbt, "experience_cap", CrystalToolsConfig.BASE_EXPERIENCE_CAP.get());
//        this.points = NBTUtils.getIntArray(nbt, "points");
    }


    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putInt("skill_points", this.skillPoints);
        nbt.putInt("experience", this.exp);
        nbt.putInt("experience_cap", this.expCap);
        nbt.putIntArray("points", this.points);
    }

    public int getSkillPoints() {
        return this.skillPoints;
    }

    public int[] getPoints() {
        return this.points;
    }

    public void addSkillPoints(int amount) {
        this.skillPoints += amount;
        this.setChanged();
    }

    // TODO: Need to add more as more furnace upgrades are added
    public void addToData(String key, float value) {
        switch(key) {
            case "skill_points" -> this.skillPoints += value;
            case "experience" -> this.exp += value;
            case "experience_cap" -> this.expCap += value;
        }

        this.setChanged();
    }

    public void addToPoints(int id, int value) {
        this.points[id] += value;
        this.setChanged();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }
}

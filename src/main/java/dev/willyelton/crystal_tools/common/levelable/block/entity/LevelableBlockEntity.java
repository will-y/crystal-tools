package dev.willyelton.crystal_tools.common.levelable.block.entity;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.components.LevelableBlockEntityData;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.List;

public abstract class LevelableBlockEntity extends BlockEntity {
    public static final List<String> NBT_TAGS = List.of("SkillPoints", "Points", "Exp", "ExpCap");

    protected int skillPoints = 0;
    protected int[] points = new int[100];
    protected int exp = 0;
    protected int expCap = CrystalToolsConfig.BASE_EXPERIENCE_CAP.get();

    public LevelableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.skillPoints = tag.getInt("SkillPoints");
        this.points = NBTUtils.getIntArray(tag, "Points", 100);
        this.exp = tag.getInt("Exp");
        this.expCap = tag.getInt("ExpCap");

        if (this.expCap == 0) this.expCap = CrystalToolsConfig.BASE_EXPERIENCE_CAP.get();
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        LevelableBlockEntityData levelableBlockEntityData = componentInput.get(DataComponents.LEVELABLE_BLOCK_ENTITY_DATA);
        if (levelableBlockEntityData != null) {
            this.skillPoints = levelableBlockEntityData.skillPoints();
            this.points = levelableBlockEntityData.points().stream().mapToInt(Integer::intValue).toArray();
            this.exp = levelableBlockEntityData.exp();
            this.expCap = levelableBlockEntityData.expCap();
        }

        if (this.expCap == 0) this.expCap = CrystalToolsConfig.BASE_EXPERIENCE_CAP.get();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("SkillPoints", this.skillPoints);
        tag.putIntArray("Points", this.points);
        tag.putInt("Exp", this.exp);
        tag.putInt("ExpCap", this.expCap);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        LevelableBlockEntityData levelableBlockEntityData = new LevelableBlockEntityData(skillPoints,
                Arrays.stream(points).boxed().toList(), exp, expCap);
        components.set(DataComponents.LEVELABLE_BLOCK_ENTITY_DATA, levelableBlockEntityData);
    }

    public void addSkillPoints(int points) {
        this.skillPoints += points;
        this.setChanged();
    }

    public void addToData(String key, float value) {
        switch (key) {
            case "skill_points" -> this.skillPoints += (int) value;
            case "experience" -> this.exp += (int) value;
            case "experience_cap" -> this.expCap += (int) value;
            default -> addToExtraData(key, value);
        }
        this.setChanged();
    }

    protected abstract void addToExtraData(String key, float value);

    public void addToPoints(int id, int value) {
        this.points[id] += value;
        this.setChanged();
    }

    protected void addExp(int toAdd) {
        this.exp += toAdd;

        while (this.exp > this.expCap) {
            this.exp = this.exp - this.expCap;
            this.expCap = ToolUtils.getNewCap(this.expCap, 1);
            this.skillPoints++;
        }
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }

    public int getPoint(int index) {
        return points[index];
    }

    public void setPoints(int index, int value) {
        this.points[index] = value;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getExpCap() {
        return expCap;
    }

    public void setExpCap(int expCap) {
        this.expCap = expCap;
    }

    public void resetSkills() {
        this.skillPoints = skillPoints + (int) Arrays.stream(this.points).asLongStream().sum();
        this.points = new int[100];
        this.exp = 0;
        this.expCap = CrystalToolsConfig.BASE_EXPERIENCE_CAP.get();
        this.resetExtraSkills();
    }

    protected abstract void resetExtraSkills();
}

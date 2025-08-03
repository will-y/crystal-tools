package dev.willyelton.crystal_tools.common.levelable.block.entity;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.components.LevelableBlockEntityData;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.Action;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

public abstract class LevelableBlockEntity extends ActionBlockEntity {
    public static final List<String> NBT_TAGS = List.of("SkillPoints", "Points", "Exp", "ExpCap");

    protected int skillPoints = 0;
    protected SkillPoints points = new SkillPoints();
    protected int exp = 0;
    protected int expCap = getExpCap();

    public LevelableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.skillPoints = valueInput.getInt("SkillPoints").orElse(0);
        this.points = valueInput.read("Points", SkillPoints.CODEC).orElse(new SkillPoints());
        this.exp = valueInput.getInt("Exp").orElse(0);
        this.expCap = valueInput.getInt("ExpCap").orElse(getBaseExpCap());
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentInput) {
        super.applyImplicitComponents(componentInput);

        LevelableBlockEntityData levelableBlockEntityData = componentInput.get(DataComponents.LEVELABLE_BLOCK_ENTITY_DATA);
        if (levelableBlockEntityData != null) {
            this.skillPoints = levelableBlockEntityData.skillPoints();
            this.points = levelableBlockEntityData.points();
            this.exp = levelableBlockEntityData.exp();
            this.expCap = levelableBlockEntityData.expCap();
        }

        if (this.expCap == 0) this.expCap = getBaseExpCap();
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);

        valueOutput.putInt("SkillPoints", this.skillPoints);
        valueOutput.store("Points", SkillPoints.CODEC, this.points);
        valueOutput.putInt("Exp", this.exp);
        valueOutput.putInt("ExpCap", this.expCap);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        LevelableBlockEntityData levelableBlockEntityData = new LevelableBlockEntityData(skillPoints,
                points, exp, expCap);
        components.set(DataComponents.LEVELABLE_BLOCK_ENTITY_DATA, levelableBlockEntityData);
    }

    public void addSkillPoints(int points) {
        this.skillPoints += points;
        this.setChanged();
    }

    public void addToData(String key, float value) {
        for (Action action : getActions()) {
            if (action.addToExtra(key, value)) {
                this.setChanged();
                return;
            }
        }

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
        this.points.addPoints(id, value);
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

    public SkillPoints getPoints() {
        return points;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }

    public int getPoint(int index) {
        return points.getPoints(index);
    }

    public void setPoints(int index, int value) {
        this.points.setPoints(index, value);
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
        this.skillPoints = skillPoints + this.points.getTotalPoints();
        this.points = new SkillPoints();
        this.exp = 0;
        this.expCap = getBaseExpCap();
        this.resetExtraSkills();
    }

    protected void resetExtraSkills() {
        for (Action action : getActions()) {
            action.resetExtra();
        }
    }

    protected int getBaseExpCap() {
        return CrystalToolsConfig.BASE_EXPERIENCE_CAP.get();
    }
}

package dev.willyelton.crystal_tools.gui;

import dev.willyelton.crystal_tools.levelable.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal_tools.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FurnaceUpgradeScreen extends BaseUpgradeScreen {
    private final LevelableBlockEntity levelableBlockEntity;
    private final Screen screen;

    public FurnaceUpgradeScreen(BlockEntity blockEntity, Player player, Screen toOpen) {
        super(blockEntity.getPersistentData(), player, Component.literal("Upgrade Furnace"));
        if (blockEntity instanceof LevelableBlockEntity levelableBlockEntityIn) {
            this.levelableBlockEntity = levelableBlockEntityIn;
        } else {
            levelableBlockEntity = null;
        }

        this.data = this.getSkillData();
        this.screen = toOpen;
    }

    @Override
    protected void initComponents() {

    }

    protected SkillData getSkillData() {
        int[] points = NBTUtils.getIntArray(this.tag, "points");
        String blockType = this.levelableBlockEntity.getBlockType();
        return SkillData.fromResourceLocation(new ResourceLocation("crystal_tools", String.format("skill_trees/%s.json", blockType)), points);
    }

    @Override
    public void onClose() {
        this.minecraft.popGuiLayer();
        this.minecraft.setScreen(this.screen);
    }
}

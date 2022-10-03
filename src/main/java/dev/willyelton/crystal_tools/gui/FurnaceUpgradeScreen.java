package dev.willyelton.crystal_tools.gui;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.gui.component.SkillButton;
import dev.willyelton.crystal_tools.levelable.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal_tools.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.levelable.skill.SkillDataNode;
import dev.willyelton.crystal_tools.levelable.skill.requirement.RequirementType;
import dev.willyelton.crystal_tools.levelable.skill.requirement.SkillDataRequirement;
import dev.willyelton.crystal_tools.levelable.skill.requirement.SkillItemRequirement;
import dev.willyelton.crystal_tools.network.BlockAttributePacket;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.RemoveItemPacket;
import dev.willyelton.crystal_tools.network.ToolAttributePacket;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

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

    @Override
    protected void onSkillButtonPress(SkillDataNode node, Button button) {
        int skillPoints = (int) NBTUtils.getFloatOrAddKey(tag, "skill_points");

        if (skillPoints > 0) {
            NBTUtils.addValueToTag(tag, "skill_points", -1);
            PacketHandler.sendToServer(new BlockAttributePacket("skill_points", -1, -1, this.levelableBlockEntity.getBlockPos()));
            PacketHandler.sendToServer(new BlockAttributePacket(node.getKey(), node.getValue(), node.getId(), this.levelableBlockEntity.getBlockPos()));
            node.addPoint();
            if (node.isComplete()) {
                ((SkillButton) button).setComplete();
            }
        }

        super.onSkillButtonPress(node, button);
    }
}

package dev.willyelton.crystal_tools.gui;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.gui.component.SkillButton;
import dev.willyelton.crystal_tools.levelable.ModItems;
import dev.willyelton.crystal_tools.levelable.skill.SkillDataNode;
import dev.willyelton.crystal_tools.network.*;
import dev.willyelton.crystal_tools.levelable.LevelableItem;
import dev.willyelton.crystal_tools.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class UpgradeScreen extends BaseUpgradeScreen {
    private Button healButton;
    private Button resetButton;
    private final ItemStack stack;
    final CompoundTag tag;

    public UpgradeScreen(ItemStack itemStack, Player player) {
        super(player, Component.literal("Upgrade Screen"));
        this.stack = itemStack;
        this.data = getSkillData();
        this.tag = itemStack.getTag();
    }

    protected SkillData getSkillData() {
        int[] points = NBTUtils.getIntArray(stack, "points");
        if (stack.getItem() instanceof LevelableItem) {
            String toolType = ((LevelableItem) stack.getItem()).getItemType();
            return SkillData.fromResourceLocation(new ResourceLocation("crystal_tools", String.format("skill_trees/%s.json", toolType)), points);
        } else {
            return null;
        }
    }

    /**
     * Used to init things differently from the default item implementation of the upgrade screen
     */
    @Override
    protected void initComponents() {
        // add button to spend skill points to heal tool
        healButton = addRenderableWidget(new Button(5, 15, 30, Y_SIZE, Component.literal("Heal"), (button) -> {
            PacketHandler.sendToServer(new ToolHealPacket());
            // also do client side to update ui, seems to work, might want to test more
            NBTUtils.addValueToTag(this.tag, "skill_points", -1);
            this.updateButtons();
        }, (button, poseStack, mouseX, mouseY) -> {
            Component text = Component.literal("Uses a skill point to fully repair this tool");
            UpgradeScreen.this.renderTooltip(poseStack, UpgradeScreen.this.minecraft.font.split(text, Math.max(UpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
        }));

        resetButton = addRenderableWidget(new Button(width - 40 - 5, 15, 40, Y_SIZE, Component.literal("Reset"), (button) -> {
            boolean requiresCrystal = CrystalToolsConfig.REQUIRE_CRYSTAL_FOR_RESET.get();

            if (!requiresCrystal || this.player.getInventory().hasAnyOf(Set.of(ModItems.CRYSTAL.get()))) {
                // Server
                PacketHandler.sendToServer(new ResetSkillsPacket());
                PacketHandler.sendToServer(new RemoveItemPacket(ModItems.CRYSTAL.get().getDefaultInstance()));

                // Client
                ToolUtils.resetPoints(this.stack);
                InventoryUtils.removeItemFromInventory(this.player.getInventory(), ModItems.CRYSTAL.get().getDefaultInstance());

                int[] points = NBTUtils.getIntArray(stack, "points");
                if (stack.getItem() instanceof LevelableItem) {
                    String toolType = ((LevelableItem) stack.getItem()).getItemType();
                    data = SkillData.fromResourceLocation(new ResourceLocation("crystal_tools", String.format("skill_trees/%s.json", toolType)), points);
                } else {
                    data = null;
                }
            }

            this.onClose();
        }, (button, poseStack, mouseX, mouseY) -> {
            boolean requiresCrystal = CrystalToolsConfig.REQUIRE_CRYSTAL_FOR_RESET.get();
            String text = "Reset Skill Points";
            if (requiresCrystal) text += " (Requires 1 Crystal)";
            Component textComponent = Component.literal(text);
            UpgradeScreen.this.renderTooltip(poseStack, UpgradeScreen.this.minecraft.font.split(textComponent, Math.max(UpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
        }));

    }

    @Override
    protected int getSkillPoints() {
        return (int) NBTUtils.getFloatOrAddKey(tag, "skill_points");
    }

    @Override
    protected void updateButtons() {
        super.updateButtons();
        int skillPoints = (int) NBTUtils.getFloatOrAddKey(tag, "skill_points");
        this.healButton.active = skillPoints > 0;
    }

    @Override
    protected void onSkillButtonPress(SkillDataNode node, Button button) {
        int skillPoints = (int) NBTUtils.getFloatOrAddKey(tag, "skill_points");

        if (skillPoints > 0) {
            NBTUtils.addValueToTag(tag, "skill_points", -1);
            PacketHandler.sendToServer(new ToolAttributePacket("skill_points", -1, -1));
            PacketHandler.sendToServer(new ToolAttributePacket(node.getKey(), node.getValue(), node.getId()));
            node.addPoint();
            if (node.isComplete()) {
                ((SkillButton) button).setComplete();
            }
        }

        super.onSkillButtonPress(node, button);
    }
}

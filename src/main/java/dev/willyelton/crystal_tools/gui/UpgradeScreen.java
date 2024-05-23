package dev.willyelton.crystal_tools.gui;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.gui.component.SkillButton;
import dev.willyelton.crystal_tools.levelable.skill.SkillDataNode;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.packet.RemoveItemPacket;
import dev.willyelton.crystal_tools.network.packet.ResetSkillsPacket;
import dev.willyelton.crystal_tools.network.packet.ToolAttributePacket;
import dev.willyelton.crystal_tools.network.packet.ToolHealPacket;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
        this.data = ToolUtils.getSkillData(itemStack);
        this.tag = itemStack.getTag();
    }


    /**
     * Used to init things differently from the default item implementation of the upgrade screen
     */
    @Override
    protected void initComponents() {
        super.initComponents();
        // add button to spend skill points to heal tool
        healButton = addRenderableWidget(Button.builder(Component.literal("Heal"), (button) -> {
            PacketHandler.sendToServer(new ToolHealPacket());
            // also do client side to update ui, seems to work, might want to test more
            NBTUtils.addValueToTag(this.tag, "skill_points", -1);
            this.updateButtons();
            // Itemstack won't get updated yetD I guess so need to manually disable heal after use
            button.active = false;
        }).bounds(5, 15, 30, Y_SIZE).tooltip(Tooltip.create(Component.literal("Uses a skill point to fully repair this tool"))).build());

        boolean resetRequiresCrystal = CrystalToolsConfig.REQUIRE_CRYSTAL_FOR_RESET.get();
        String text = "Reset Skill Points";
        if (resetRequiresCrystal) text += " (Requires 1 Crystal)";
        Tooltip resetTooltip = Tooltip.create(Component.literal(text));

        resetButton = addRenderableWidget(Button.builder(Component.literal("Reset"), (button) -> {
            boolean requiresCrystal = CrystalToolsConfig.REQUIRE_CRYSTAL_FOR_RESET.get();

            if (!requiresCrystal || this.player.getInventory().hasAnyOf(Set.of(Registration.CRYSTAL.get()))) {
                // Server
                PacketHandler.sendToServer(new ResetSkillsPacket());
                PacketHandler.sendToServer(new RemoveItemPacket(Registration.CRYSTAL.get().getDefaultInstance()));

                // Client
                ToolUtils.resetPoints(this.stack);
                InventoryUtils.removeItemFromInventory(this.player.getInventory(), Registration.CRYSTAL.get().getDefaultInstance());

                data = ToolUtils.getSkillData(this.stack);
            }

            this.onClose();
        }).bounds(width - 40 - 5, 15, 40, Y_SIZE).tooltip(resetTooltip).build());
    }

    @Override
    protected int getSkillPoints() {
        return (int) NBTUtils.getFloatOrAddKey(tag, "skill_points");
    }

    @Override
    protected void updateButtons() {
        super.updateButtons();
        int skillPoints = (int) NBTUtils.getFloatOrAddKey(tag, "skill_points");
        this.healButton.active = skillPoints > 0 && this.stack.isDamaged();
        this.resetButton.active = !CrystalToolsConfig.REQUIRE_CRYSTAL_FOR_RESET.get() || this.player.getInventory().hasAnyOf(Set.of(Registration.CRYSTAL.get()));
    }

    @Override
    protected void onSkillButtonPress(SkillDataNode node, Button button) {
        int skillPoints = (int) NBTUtils.getFloatOrAddKey(tag, "skill_points");

        if (skillPoints > 0) {
            changeSkillPoints(-1);
            PacketHandler.sendToServer(new ToolAttributePacket(node.getKey(), node.getValue(), node.getId()));
            node.addPoint();
            if (node.isComplete()) {
                ((SkillButton) button).setComplete();
            }
        }

        super.onSkillButtonPress(node, button);
    }

    @Override
    protected void changeSkillPoints(int change) {
        NBTUtils.addValueToTag(tag, "skill_points", change);
        PacketHandler.sendToServer(new ToolAttributePacket("skill_points", change, -1));
    }

    @Override
    protected int getXpButtonY() {
        return 35;
    }
}

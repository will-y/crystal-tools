package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.client.gui.component.SkillButton;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.CrystalBackpack;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillDataNode;
import dev.willyelton.crystal_tools.common.network.data.RemoveItemPayload;
import dev.willyelton.crystal_tools.common.network.data.ResetSkillsPayload;
import dev.willyelton.crystal_tools.common.network.data.ToolAttributePayload;
import dev.willyelton.crystal_tools.common.network.data.ToolHealPayload;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Set;

public class UpgradeScreen extends BaseUpgradeScreen {
    private Button healButton;
    private Button resetButton;
    private final ItemStack stack;
    private final Runnable onClose;
    private int slotIndex = -1;

    public UpgradeScreen(ItemStack itemStack, Player player) {
        this(itemStack, player, null);
    }

    public UpgradeScreen(int slotIndex, Player player, Runnable onClose) {
        this(CrystalBackpack.getBackpackFromSlotIndex(player, slotIndex), player, onClose);
        this.slotIndex = slotIndex;
    }

    public UpgradeScreen(ItemStack itemStack, Player player, Runnable onClose) {
        super(player, Component.literal("Upgrade Screen"));
        this.stack = itemStack;
        this.data = ToolUtils.getSkillData(itemStack);
        this.onClose = onClose;
    }

    /**
     * Used to init things differently from the default item implementation of the upgrade screen
     */
    @Override
    protected void initComponents() {
        super.initComponents();
        // add button to spend skill points to heal tool
        healButton = addRenderableWidget(Button.builder(Component.literal("Heal"), (button) -> {
            PacketDistributor.sendToServer(ToolHealPayload.INSTANCE);
            // also do client side to update ui, seems to work, might want to test more
            DataComponents.addToComponent(stack, DataComponents.SKILL_POINTS, -1);
            this.updateButtons();
            // Itemstack won't get updated yet I guess so need to manually disable heal after use
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
                PacketDistributor.sendToServer(ResetSkillsPayload.INSTANCE);
                PacketDistributor.sendToServer(new RemoveItemPayload(Registration.CRYSTAL.get().getDefaultInstance()));

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
        return stack.getOrDefault(DataComponents.SKILL_POINTS, 0);
    }

    @Override
    protected void updateButtons() {
        super.updateButtons();
        int skillPoints = getSkillPoints();
        this.healButton.active = skillPoints > 0 && this.stack.isDamaged();
        this.resetButton.active = !CrystalToolsConfig.REQUIRE_CRYSTAL_FOR_RESET.get() || this.player.getInventory().hasAnyOf(Set.of(Registration.CRYSTAL.get()));
    }

    @Override
    protected void onSkillButtonPress(SkillDataNode node, Button button) {
        int skillPoints = getSkillPoints();
        boolean shift = hasShiftDown();
        boolean control = hasControlDown();

        if (skillPoints > 0) {
            int pointsToSpend = 1;
            if (node.getLimit() == 0) {
                pointsToSpend = getPointsToSpend(skillPoints, shift, control);
            }

            PacketDistributor.sendToServer(new ToolAttributePayload(node.getKey(), node.getValue(), node.getId(), slotIndex, pointsToSpend));
            node.addPoint(pointsToSpend);
            if (node.isComplete()) {
                ((SkillButton) button).setComplete();
            }

            changeSkillPoints(-pointsToSpend);
        }

        super.onSkillButtonPress(node, button);
    }

    @Override
    protected void changeSkillPoints(int change) {
        DataComponents.addToComponent(stack, DataComponents.SKILL_POINTS, change);
        PacketDistributor.sendToServer(new ToolAttributePayload("skill_points", change, -1, slotIndex, 1));
    }

    @Override
    protected int getXpButtonY() {
        return 35;
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.onClose != null) {
            this.onClose.run();
        }
    }
}

package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.client.gui.component.SkillButton;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.CrystalBackpack;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.common.levelable.skill.node.SkillDataNode;
import dev.willyelton.crystal_tools.common.network.data.ResetSkillsPayload;
import dev.willyelton.crystal_tools.common.network.data.ToolHealPayload;
import dev.willyelton.crystal_tools.common.network.data.ToolSkillPayload;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Set;

public class UpgradeScreen extends BaseUpgradeScreen {
    private Button healButton;
    private final ItemStack stack;
    private final Runnable onClose;
    private int slotIndex = -1;

    public UpgradeScreen(ItemStack itemStack, Player player, SkillData data, ResourceKey<SkillData> key) {
        this(itemStack, player, null, data, key);
    }

    // TODO: Wrap this in something for backpack
    public UpgradeScreen(int slotIndex, Player player, Runnable onClose, SkillData data, ResourceKey<SkillData> key) {
        this(CrystalBackpack.getBackpackFromSlotIndex(player, slotIndex), player, onClose, data, key);
        this.slotIndex = slotIndex;
    }

    public UpgradeScreen(ItemStack itemStack, Player player, Runnable onClose, SkillData data, ResourceKey<SkillData> key) {
        super(player, Component.literal("Upgrade Screen"), data, key);
        this.stack = itemStack;
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
    }

    @Override
    protected void resetPoints(boolean crystalRequired) {
        if (!crystalRequired || this.player.getInventory().hasAnyOf(Set.of(Registration.CRYSTAL.get()))) {
            PacketDistributor.sendToServer(ResetSkillsPayload.INSTANCE);
            ToolUtils.resetPoints(this.stack);
        }

        this.onClose();
    }

    @Override
    public SkillPoints getPoints() {
        return this.stack.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints()).copy();
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
    }

    @Override
    protected void onSkillButtonPress(SkillDataNode node, Button button) {
        int skillPoints = getSkillPoints();
        boolean shift = hasShiftDown();
        boolean control = hasControlDown();

        if (skillPoints > 0) {
            int pointsToSpend = 1;
            if (node.getLimit() == 0 || node.getLimit() > 1) {
                pointsToSpend = getPointsToSpend(skillPoints, shift, control);

                if (pointsToSpend > node.getLimit() && node.getLimit() > 1) {
                    pointsToSpend = node.getLimit() - points.getPoints(node.getId());
                }
            }

            PacketDistributor.sendToServer(new ToolSkillPayload(node.getId(), key, pointsToSpend));
            points.addPoints(node.getId(), pointsToSpend);
            DataComponents.addToComponent(stack, DataComponents.SKILL_POINTS, -pointsToSpend);
            if (points.getPoints(node.getId()) >= node.getLimit() && node.getLimit() != 0) {
                ((SkillButton) button).setComplete();
            }
        }

        super.onSkillButtonPress(node, button);
    }

    @Override
    protected void changeClientSkillPoints(int change) {
        DataComponents.addToComponent(stack, DataComponents.SKILL_POINTS, change);
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

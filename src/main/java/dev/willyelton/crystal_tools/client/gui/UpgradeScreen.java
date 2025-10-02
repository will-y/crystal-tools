package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.client.gui.component.SkillButton;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.capability.LevelableStack;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.CrystalBackpack;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.common.levelable.skill.node.SkillDataNode;
import dev.willyelton.crystal_tools.common.network.data.ResetSkillsPayload;
import dev.willyelton.crystal_tools.common.network.data.ToolHealPayload;
import dev.willyelton.crystal_tools.common.network.data.ToolSkillPayload;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class UpgradeScreen extends BaseUpgradeScreen {
    @Nullable
    private Button healButton;
    private final ItemStack stack;
    private final Runnable onClose;
    private final Levelable levelable;

    private int slotIndex = -1;

    public UpgradeScreen(ItemStack itemStack, Player player, Levelable levelable) {
        this(itemStack, player, null, levelable);
    }

    // TODO: Wrap this in something for backpack (for curios)
    public UpgradeScreen(int slotIndex, Player player, Runnable onClose, Levelable levelable) {
        this(CrystalBackpack.getBackpackFromSlotIndex(player, slotIndex), player, onClose, levelable);
        this.slotIndex = slotIndex;
    }

    public UpgradeScreen(ItemStack itemStack, Player player, Runnable onClose, Levelable levelable) {
        super(player, Component.literal("Upgrade Screen"), levelable.getSkillTree(), levelable.getKey());
        this.stack = itemStack;
        this.onClose = onClose;
        this.levelable = levelable;
    }

    /**
     * Used to init things differently from the default item implementation of the upgrade screen
     */
    @Override
    protected void initComponents() {
        super.initComponents();

        if (levelable instanceof LevelableStack levelableStack && !levelableStack.allowRepair()) return;

        // add button to spend skill points to heal tool
        healButton = addRenderableWidget(Button.builder(Component.literal("Heal"), (button) -> {
            ClientPacketDistributor.sendToServer(ToolHealPayload.INSTANCE);
            // also do client side to update ui, seems to work, might want to test more
            DataComponents.addToComponent(stack, DataComponents.SKILL_POINTS, -1);
            this.updateButtons();
            // Itemstack won't get updated yet I guess so need to manually disable heal after use
            button.active = false;
        }).bounds(5, 15, 30, Y_SIZE).tooltip(Tooltip.create(Component.literal("Uses a skill point to fully repair this tool"))).build());
    }

    @Override
    protected void resetPoints(boolean crystalRequired) {
        if (!crystalRequired || this.player.getInventory().hasAnyOf(Set.of(ModRegistration.CRYSTAL.get()))) {
            ClientPacketDistributor.sendToServer(ResetSkillsPayload.INSTANCE);
            ToolUtils.resetPoints(this.stack, this.player);
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
        if (this.healButton != null) {
            this.healButton.active = skillPoints > 0 && this.stack.isDamaged();
        }
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
                int maxSpend = node.getLimit() - points.getPoints(node.getId());
                if (pointsToSpend > maxSpend && node.getLimit() > 1) {
                    pointsToSpend = maxSpend;
                }
            }

            ClientPacketDistributor.sendToServer(new ToolSkillPayload(node.getId(), key, pointsToSpend));
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

    @Override
    protected boolean allowReset() {
        return levelable.allowReset();
    }

    @Override
    protected boolean allowXpLevels() {
        return levelable.allowXpLevels();
    }
}

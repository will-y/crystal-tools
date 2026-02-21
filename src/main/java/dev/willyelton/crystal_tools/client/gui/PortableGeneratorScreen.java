package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.inventory.container.AbstractGeneratorContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.PortableGeneratorContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

import static dev.willyelton.crystal_tools.CrystalTools.rl;
import static dev.willyelton.crystal_tools.common.inventory.container.PortableGeneratorContainerMenu.SLOTS_PER_ROW;
import static dev.willyelton.crystal_tools.common.inventory.container.PortableGeneratorContainerMenu.START_X;
import static dev.willyelton.crystal_tools.common.inventory.container.PortableGeneratorContainerMenu.START_Y;

public class PortableGeneratorScreen extends CrystalGeneratorScreen {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CrystalTools.MODID ,"textures/gui/portable_generator.png");
    private static final int DISABLED_SLOT_TEXTURE_X = 176;
    private static final int DISABLED_SLOT_TEXTURE_Y = 13;
    private static final int DISABLED_SLOT_TEXTURE_SIZE = 18;
    private static final int TOTAL_SLOTS = 27;

    private final int activeSlots;
    private final ItemStack generatorStack;

    public PortableGeneratorScreen(AbstractGeneratorContainerMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);

        this.imageHeight = 218;
        this.inventoryLabelY = this.imageHeight - 94;

        if (container instanceof PortableGeneratorContainerMenu portableGeneratorContainerMenu) {
            this.activeSlots = portableGeneratorContainerMenu.getActiveSlots();
            this.generatorStack = portableGeneratorContainerMenu.getGeneratorStack();
        } else {
            this.activeSlots = 0;
            this.generatorStack = ItemStack.EMPTY;
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        this.renderDisabledSlots(guiGraphics);
    }

    @Override
    protected Optional<Holder.Reference<SkillData>> getSkillData() {
        Level level = this.getMenu().getLevel();

        if (level != null) {
            Optional<Registry<SkillData>> skillDataOptional = level.registryAccess().lookup(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS);
            if (skillDataOptional.isPresent()) {
                Registry<SkillData> skillDataRegistry = skillDataOptional.get();

                return skillDataRegistry.get(rl("portable_crystal_generator"));
            }
        }

        return Optional.empty();
    }

    @Override
    protected Identifier texture() {
        return TEXTURE;
    }

    @Override
    protected int fireY() {
        return 108;
    }

    @Override
    protected Screen upgradeButtonScreen(Holder.Reference<SkillData> skillData) {
        Player player = getMenu().getPlayer();
        Levelable levelable = this.generatorStack.getCapability(Capabilities.ITEM_SKILL, player.level().registryAccess());
        if (levelable != null) {
            return new UpgradeScreen(generatorStack, menu.getPlayer(), levelable);
        }

        return null;
    }

    protected void renderDisabledSlots(GuiGraphics guiGraphics) {
        for (int i = activeSlots; i < TOTAL_SLOTS; i++) {
            int x = i % SLOTS_PER_ROW;
            int y = i / SLOTS_PER_ROW;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture(), leftPos + START_X + x * DISABLED_SLOT_TEXTURE_SIZE - 1, topPos + START_Y + y * DISABLED_SLOT_TEXTURE_SIZE - 1, DISABLED_SLOT_TEXTURE_X, DISABLED_SLOT_TEXTURE_Y, DISABLED_SLOT_TEXTURE_SIZE, DISABLED_SLOT_TEXTURE_SIZE, 256, 256);
        }
    }
}

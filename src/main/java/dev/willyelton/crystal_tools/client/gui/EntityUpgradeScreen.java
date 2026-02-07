package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.common.network.data.EntitySkillPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class EntityUpgradeScreen extends BaseUpgradeScreen {
    private final LivingEntity entity;

    public EntityUpgradeScreen(LivingEntity entity, Player player, Levelable levelable) {
        super(player, Component.literal("Entity Upgrade Entity"), levelable.getSkillTree(), levelable.getKey());
        this.entity = entity;
    }

    @Override
    protected int getXpButtonY() {
        return 20;
    }

    @Override
    protected void resetPoints(boolean crystalRequired) {
        // TODO
    }

    @Override
    public SkillPoints getPoints() {
        return entity.getData(ModRegistration.ENTITY_SKILL).skillPoints();
    }

    @Override
    protected void changeClientSkillPoints(int change) {

    }

    @Override
    protected int getSkillPoints() {
        return entity.getData(ModRegistration.ENTITY_SKILL).unspentPoints();
    }

    @Override
    protected void addPointsForNode(int pointsToSpend, int nodeId) {
        ClientPacketDistributor.sendToServer(new EntitySkillPayload(nodeId, key, pointsToSpend, this.entity.getId()));
    }

    @Override
    protected boolean allowReset() {
        return false;
    }

    @Override
    protected boolean allowXpLevels() {
        // TODO
        return false;
    }
}

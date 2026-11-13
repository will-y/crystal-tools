package dev.willyelton.crystal.tools.client.renderer;

import dev.willyelton.crystal.tools.common.entity.CrystalGolem;
import net.minecraft.client.renderer.entity.CopperGolemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.golem.CopperGolem;

import static dev.willyelton.crystal.tools.CrystalTools.rl;

public class CrystalGolemRenderer extends CopperGolemRenderer {
    private static final Identifier CRYSTAL_GOLEM_TEXTURE = rl("textures/entity/crystal_golem/crystal_golem.png");
    public CrystalGolemRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(CopperGolemRenderState state) {
        return CRYSTAL_GOLEM_TEXTURE;
    }

    @Override
    public void extractRenderState(CopperGolem entity, CopperGolemRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        if (entity instanceof CrystalGolem crystalGolem) {
            state.rightHandItemStack = crystalGolem.firstItem();
        }
    }
}

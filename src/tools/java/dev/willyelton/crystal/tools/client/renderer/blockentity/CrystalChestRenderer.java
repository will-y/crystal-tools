package dev.willyelton.crystal.tools.client.renderer.blockentity;

import dev.willyelton.crystal.tools.common.levelable.block.entity.CrystalChestBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import org.jspecify.annotations.Nullable;

import static dev.willyelton.crystal.tools.CrystalTools.rl;
import static net.minecraft.client.renderer.Sheets.CHEST_MAPPER;

public class CrystalChestRenderer extends ChestRenderer<CrystalChestBlockEntity> {
    public CrystalChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected @Nullable SpriteId getCustomSprite(CrystalChestBlockEntity blockEntity, ChestRenderState renderState) {
        return CHEST_MAPPER.apply(rl("crystal"));
    }
}

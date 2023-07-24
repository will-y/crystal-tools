package dev.willyelton.crystal_tools.levelable.block;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class CrystalTorch extends TorchBlock {
    public static final BooleanProperty DROP_ITEM = BooleanProperty.create("drop_item");

    public CrystalTorch() {
        this(BlockBehaviour.Properties.of());
    }

    public CrystalTorch(BlockBehaviour.Properties properties) {
        super(properties.noCollission().instabreak().lightLevel((state) -> 14).sound(SoundType.WOOD), ParticleTypes.SOUL_FIRE_FLAME);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(DROP_ITEM);
    }
}

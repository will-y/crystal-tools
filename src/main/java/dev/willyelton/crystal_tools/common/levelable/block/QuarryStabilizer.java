package dev.willyelton.crystal_tools.common.levelable.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;

// TODO: Probably want to do a cool model and not extend torch block
public class QuarryStabilizer extends TorchBlock {
    // TODO: Server config
    public static int QUARRY_MAX_SIZE = 16;

    public QuarryStabilizer() {
        super(ParticleTypes.GLOW, BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel((state) -> 14).sound(SoundType.STONE));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // TODO: Put in some static helper, will need other places
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    public static List<BlockPos> findStabilizerSquare(BlockPos startingPos, Level level) {
        List<BlockPos> result = new ArrayList<>();

        return result;
    }
}

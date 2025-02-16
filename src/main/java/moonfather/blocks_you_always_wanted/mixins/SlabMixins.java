package moonfather.blocks_you_always_wanted.mixins;

import moonfather.blocks_you_always_wanted.blocks.FenceVersion3Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlabBlock.class)
public abstract class SlabMixins extends BlockMixinBase
{
    @SuppressWarnings("CancellableInjectionUsage")
    @Override
    public void injected(BlockState state, Level level, BlockPos blockPos, Block other, BlockPos otherPos, boolean movedByPiston, CallbackInfo ci)
    {
        if (level.getBlockState(blockPos.above()).getBlock() instanceof FenceVersion3Block)
        {
            FenceVersion3Block.onSlabNeighborChanged(state, level, blockPos, other, otherPos, movedByPiston);
        }
    }
}

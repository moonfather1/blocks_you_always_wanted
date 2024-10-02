package moonfather.blocks_you_always_wanted.mixins;

import moonfather.blocks_you_always_wanted.blocks.GateRaisedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PoweredRailBlock.class)
public class PoweredRailMixins
{
    @Inject(method = "isSameRailWithPower", at = @At("HEAD"), cancellable = true)
    private void injected(Level level, BlockPos state, boolean searchForward, int recursionCount, RailShape shape, CallbackInfoReturnable<Boolean> cir)
    {
        BlockState blockStateMaybeGate = level.getBlockState(state);
        if (blockStateMaybeGate.getBlock() instanceof GateRaisedBlock && blockStateMaybeGate.getValue(GateRaisedBlock.PROVIDES_RAIL_POWER))
        {
            cir.setReturnValue(true);
        }
    }
}

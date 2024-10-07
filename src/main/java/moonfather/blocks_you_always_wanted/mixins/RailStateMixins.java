package moonfather.blocks_you_always_wanted.mixins;

import moonfather.blocks_you_always_wanted.blocks.GateRaisedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RailState.class)
public class RailStateMixins
{
    @Shadow
    @Final
    private BlockPos pos;

    @Shadow
    @Final
    private Level level;

    @Inject(method = "hasNeighborRail", at = @At("HEAD"), cancellable = true)
    private void injected(BlockPos checkPos, CallbackInfoReturnable<Boolean> cir)
    {
        BlockState maybeGate = this.level.getBlockState(checkPos);
        if (maybeGate.getBlock() instanceof GateRaisedBlock gate && gate.isOnRail(maybeGate))
        {
            if (checkPos.getX() == this.pos.getX() && (maybeGate.getValue(GateRaisedBlock.FACING).equals(Direction.NORTH) || maybeGate.getValue(GateRaisedBlock.FACING).equals(Direction.SOUTH))) { cir.setReturnValue(true); }
            if (checkPos.getZ() == this.pos.getZ() && (maybeGate.getValue(GateRaisedBlock.FACING).equals(Direction.WEST) || maybeGate.getValue(GateRaisedBlock.FACING).equals(Direction.EAST))) { cir.setReturnValue(true); }
        }
    }
}

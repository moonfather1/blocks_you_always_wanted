package moonfather.blocks_you_always_wanted.mixins;

import moonfather.blocks_you_always_wanted.blocks.FenceOnASlabBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FenceBlock.class)
public abstract class FenceMixins extends CrossCollisionBlock
{
    // constructor because java is stupid.
    public FenceMixins(float p_52320_, float p_52321_, float p_52322_, float p_52323_, float p_52324_, Properties p_52325_) { super(p_52320_, p_52321_, p_52322_, p_52323_, p_52324_, p_52325_); }

    @Inject(method = "isSameFence(Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void injected(BlockState other, CallbackInfoReturnable<Boolean> cir)
    {
        boolean oneOursOneNot = (other.getBlock() instanceof FenceOnASlabBlock) != (((FenceBlock)(Object)this) instanceof FenceOnASlabBlock);
        if (oneOursOneNot)
        {
            cir.setReturnValue(false);
        }
    }

}

package moonfather.blocks_you_always_wanted.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moonfather.blocks_you_always_wanted.blocks.FenceVersion3Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LeashFenceKnotEntity.class)
public abstract class LeadKnotMixins extends BlockAttachedEntity
{
    // because stupid
    protected LeadKnotMixins(EntityType<? extends BlockAttachedEntity> entityType, Level level, BlockPos pos) { super(entityType, level, pos); }
    //protected LeadKnotMixins(EntityType<? extends BlockAttachedEntity> entityType, Level level) { super(entityType, level); }


    // allow leads on our fences. used to work automatically, but in V3 we aren't tagged as fences.
    @ModifyExpressionValue(
            method = "survives",
            at = @At(value = "INVOKE", target = "net/minecraft/world/level/block/state/BlockState.is (Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean alsoAllowOurFences(boolean original) {
        return original || this.level().getBlockState(this.pos).getBlock() instanceof FenceVersion3Block;
    }
}

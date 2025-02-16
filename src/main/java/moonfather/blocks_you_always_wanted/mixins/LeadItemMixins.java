package moonfather.blocks_you_always_wanted.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import moonfather.blocks_you_always_wanted.blocks.FenceVersion3Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LeadItem.class)
public abstract class LeadItemMixins extends Item
{
    // because stupid
    public LeadItemMixins(Properties properties) { super(properties); }

    // allow leads on our fences. used to work automatically, but in V3 we aren't tagged as fences.
    @ModifyExpressionValue(
            method = "useOn",
            at = @At(value = "INVOKE", target = "net/minecraft/world/level/block/state/BlockState.is (Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean alsoAllowOurFences(boolean original, @Local(ordinal = 0) BlockState blockState) {
        return original || blockState.getBlock() instanceof FenceVersion3Block;
    }
}

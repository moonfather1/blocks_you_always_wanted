package moonfather.blocks_you_always_wanted.mixins;

import moonfather.blocks_you_always_wanted.blocks.GateRaisedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public abstract class MinecartMixins extends Entity
{
    // constructor because java is stupid.
    public MinecartMixins(EntityType<?> entityType, Level level) { super(entityType, level); }


    @Inject(method = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;comeOffTrack()V", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfo ci)
    {
        int x = Mth.floor(this.getX());  // copied part; change to mixin extras in nf 1.21 and fab
        int y = Mth.floor(this.getY());
        int z = Mth.floor(this.getZ());
        if (this.level().getBlockState(new BlockPos(x, y - 1, z)).is(BlockTags.FENCE_GATES))
        {
            y--;
        }

        BlockPos blockPos = new BlockPos(x, y, z);
        BlockState blockState = this.level().getBlockState(blockPos); // copied part done.
        if (! (blockState.getBlock() instanceof GateRaisedBlock))
        {
            return;
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        this.resetFallDistance();

        // now   this.applyNaturalSlowdown();  it's simple so i'll just copy it:
        double mul = this.isVehicle() ? 0.997D : 0.96D;
        mul += 0.001D;
        Vec3 deltaMovement = this.getDeltaMovement();
        deltaMovement = deltaMovement.multiply(mul, 0.0D, mul);
        if (this.isInWater()) { deltaMovement = deltaMovement.scale((double)0.95F); }
        this.setDeltaMovement(deltaMovement);

        // now powered rail handling
        if (blockState.getValue(GateRaisedBlock.BLOCK_BELOW).equals(GateRaisedBlock.ON_POWERED_RAIL) && blockState.getValue(GateRaisedBlock.POWERED))
        {
            Vec3 delta = this.getDeltaMovement();
            double horizontalDistance = delta.horizontalDistance();
            if (horizontalDistance > 0.01D)
            {
                this.setDeltaMovement(delta.add(delta.x / horizontalDistance * 0.06D, 0.0D, delta.z / horizontalDistance * 0.06D));
            }
            else
            {
                double dx = delta.x;
                double dz = delta.z;
                if (blockState.getValue(GateRaisedBlock.FACING).getAxis().equals(Direction.Axis.X))
                {
                    dx = Math.signum(dx) * 0.02D;
                }
                else
                {
                    dz = Math.signum(dz) * 0.02D;
                }
                this.setDeltaMovement(dx, delta.y, dz);
            }
        }

        // and done.
        ci.cancel();
    }
}

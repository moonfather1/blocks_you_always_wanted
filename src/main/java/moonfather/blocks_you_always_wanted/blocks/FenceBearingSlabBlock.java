package moonfather.blocks_you_always_wanted.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.world.level.block.SlabBlock.TYPE;
import static net.minecraft.world.level.block.SlabBlock.WATERLOGGED;

public class FenceBearingSlabBlock extends Block implements SimpleWaterloggedBlock
{
    public FenceBearingSlabBlock(Block original)
    {
        super(Properties.copy(original));
        this.original = original;
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE).setValue(TYPE, SlabType.BOTTOM));
    }

    private final Block original;

    //-------------------------------

    private static final VoxelShape SHAPE_SLAB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext)
    {
        return SHAPE_SLAB;
    }

    //--------------------------------------------

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return this.original.asItem().getDefaultInstance();
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    //-------------------------------------------------

    @Override
    public BlockState updateShape(BlockState blockState1, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos1, BlockPos blockPos2)
    {
        if (blockState1.getValue(WATERLOGGED))
        {
            levelAccessor.scheduleTick(blockPos1, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        // above is copied. skpping part with connections to the side.
        return blockState1;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos blockPos, Block other, BlockPos otherPos, boolean movedByPiston)
    {
        if (otherPos.getY() == blockPos.getY() + 1)
        {
            if (! (level.getBlockState(otherPos).getBlock() instanceof FenceMainBlock))
            {
                level.setBlockAndUpdate(blockPos, original.withPropertiesOf(state));
            }
            return;
        }
        if (otherPos.getY() == blockPos.getY())
        {
            level.getBlockState(blockPos.above()).neighborChanged(level, blockPos.above(), other, otherPos, movedByPiston);
        }
    }

    //-------------------------------------------------------

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53334_)
    {
        p_53334_.add(TYPE, WATERLOGGED); // i added type in case some other mod does a careless state.getValue
    }

    @Override
    public boolean isPathfindable(BlockState p_60475_, BlockGetter p_60476_, BlockPos p_60477_, PathComputationType p_60478_)
    {
        return  false;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params)
    {
        return original.defaultBlockState().getDrops(params);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.core.Direction direction)
    {
        if (this.flammabilityIgniteOdds < 0)
        {
            if (this.original != null)
            {
                this.flammabilityIgniteOdds = this.original.getFireSpreadSpeed(this.original.defaultBlockState(), level, pos, direction);
            }
            else
            {
                this.flammabilityIgniteOdds = 5;
            }
        }
        return this.flammabilityIgniteOdds;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.core.Direction direction)
    {
        if (this.flammabilityPerishOdds < 0)
        {
            if (this.original != null)
            {
                this.flammabilityPerishOdds = this.original.getFlammability(this.original.defaultBlockState(), level, pos, direction);
            }
            else
            {
                this.flammabilityPerishOdds = 20;
            }
        }
        return this.flammabilityPerishOdds;
    }

    private int flammabilityIgniteOdds = -123, flammabilityPerishOdds = -123;
}

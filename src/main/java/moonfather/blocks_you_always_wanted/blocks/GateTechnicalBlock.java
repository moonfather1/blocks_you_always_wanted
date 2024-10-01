package moonfather.blocks_you_always_wanted.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moonfather.blocks_you_always_wanted.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GateTechnicalBlock extends HorizontalDirectionalBlock
{
    public static final BooleanProperty IN_WALL = BlockStateProperties.IN_WALL;

    public static class ShapeSet
    {
        public static final VoxelShape EMPTY = Shapes.empty();
        public static final VoxelShape Z_SHAPE = Block.box(-5.0D, -8.0D, 6.0D, 21.0D, 11.0D, 10.0D);
        public static final VoxelShape X_SHAPE = Block.box(6.0D, -8.0D, -5.0D, 10.0D, 11.0D, 21.0D);
        public static final VoxelShape Z_SHAPE_NARROW = Block.box(-4.0D, -6.0D, 6.0D, 20.0D, 11.0D, 10.0D);  // 16+11, normal to 16+10, i don't feel like differentiating now.
        public static final VoxelShape X_SHAPE_NARROW = Block.box(6.0D, -6.0D, -4.0D, 10.0D, 11.0D, 20.0D);
        public static final VoxelShape Z_SUPPORT_SHAPE = Z_SHAPE;
        public static final VoxelShape X_SUPPORT_SHAPE = X_SHAPE;
        public static final VoxelShape Z_OCCLUSION_SHAPE = Shapes.or(Block.box(-5.0D, 0.0D, 7.0D, -3.0D, 10.0D, 9.0D), Block.box(19.0D, 0.0D, 7.0D, 21.0D, 10.0D, 9.0D));
        public static final VoxelShape X_OCCLUSION_SHAPE = Shapes.or(Block.box(7.0D, 0.0D, -5.0D, 9.0D, 10.0D, -3.0D), Block.box(7.0D, 0.0D, 19.0D, 9.0D, 10.0D, 21.0D));
        public static final VoxelShape Z_OCCLUSION_SHAPE_NARROW = Shapes.or(Block.box(-4.0D, 0.0D, 7.0D, -2.0D, 10.0D, 9.0D), Block.box(18.0D, 0.0D, 7.0D, 20.0D, 10.0D, 9.0D));
        public static final VoxelShape X_OCCLUSION_SHAPE_NARROW = Shapes.or(Block.box(7.0D, 0.0D, -4.0D, 9.0D, 10.0D, -2.0D), Block.box(7.0D, 0.0D, 18.0D, 9.0D, 10.0D, 20.0D));
    }

    public GateTechnicalBlock()
    {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.NONE).strength(2.0F, 3.0F));
        this.registerDefaultState(this.stateDefinition.any().setValue(IN_WALL, Boolean.FALSE));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec()
    {
        return CODEC;
    }
    private static final MapCodec<GateTechnicalBlock> CODEC = RecordCodecBuilder.mapCodec((p_308823_) -> {
        return p_308823_.group(propertiesCodec()).apply(p_308823_, (prop)->new GateTechnicalBlock());
    });

    ///////////////////////////////////////////////////////////////////////////

    @Override
    public VoxelShape getShape(BlockState p_53391_, BlockGetter p_53392_, BlockPos p_53393_, CollisionContext p_53394_) {
        if (p_53391_.getValue(IN_WALL)) {
            return p_53391_.getValue(FACING).getAxis() == Direction.Axis.X ? ShapeSet.X_SHAPE_NARROW : ShapeSet.Z_SHAPE_NARROW;
        } else {
            return p_53391_.getValue(FACING).getAxis() == Direction.Axis.X ? ShapeSet.X_SHAPE : ShapeSet.Z_SHAPE;
        }
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState p_253862_, BlockGetter p_254569_, BlockPos p_254197_) {
        if (false)
        //if (p_253862_.getValue(OPEN))
        {
            return ShapeSet.EMPTY;
        } else {
            return p_253862_.getValue(FACING).getAxis() == Direction.Axis.Z ? ShapeSet.Z_SUPPORT_SHAPE : ShapeSet.X_SUPPORT_SHAPE;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter p_53397_, BlockPos p_53398_, CollisionContext p_53399_)
    {
        return ShapeSet.EMPTY;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState p_53401_, BlockGetter p_53402_, BlockPos p_53403_) {
        if (p_53401_.getValue(IN_WALL)) {
            return p_53401_.getValue(FACING).getAxis() == Direction.Axis.X ? ShapeSet.X_OCCLUSION_SHAPE_NARROW : ShapeSet.Z_OCCLUSION_SHAPE_NARROW;
        } else {
            return p_53401_.getValue(FACING).getAxis() == Direction.Axis.X ? ShapeSet.X_OCCLUSION_SHAPE : ShapeSet.Z_OCCLUSION_SHAPE;
        }
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType)
    {
        switch (pathComputationType) {
            case LAND:
                return true;
            case WATER:
                return false;
            case AIR:
                return true;
            default:
                return false;
        }
    }

    private boolean isWall(BlockState other)
    {
        return other.is(BlockTags.WALLS);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return belowState.useItemOn(stack, level, player, hand, hitResult.withPosition(below));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return belowState.useWithoutItem(level, player, hitResult.withPosition(below));
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block p_53375_, BlockPos otherPos, boolean movedByPiston)
    {
        if (! level.isClientSide)
        {
            // check block below
            if (blockPos.getX() == otherPos.getX() && blockPos.getZ() == otherPos.getZ() && blockPos.getY() - 1 == otherPos.getY())
            {
                BlockState below = level.getBlockState(otherPos);
                if (below.getBlock() instanceof GateBlock || below.getBlock() instanceof GateBlock_V2)
                {
                    if (blockState.getValue(FACING) != below.getValue(FACING) || blockState.getValue(IN_WALL) != below.getValue(IN_WALL))
                    {
                        level.setBlock(blockPos, blockState.getBlock().withPropertiesOf(below), 2);
                    }
                }
                else
                {
                    level.destroyBlock(blockPos, false);
                }
                return;
            }
            // below done
            // something piston-pushed next to the gate?
            if (otherPos.getY() == blockPos.getY())
            {
                Direction facing = level.getBlockState(blockPos.below()).getValue(FACING);
                boolean onSide = ((blockPos.getX() == otherPos.getX()) && facing.getAxis().equals(Direction.Axis.X))
                        || ((blockPos.getZ() == otherPos.getZ()) && facing.getAxis().equals(Direction.Axis.Z));
                if (onSide) // don't check front of gate
                {
                    BlockState other = level.getBlockState(otherPos);
                    if (! other.isAir() && ! other.is(BlockTags.FENCES) && ! other.is(BlockTags.WALLS) && ! other.getBlock().equals(FenceOnASlabBlock.technical()) && ! other.is(Constants.BlockTags.ALLOWED_NEXT_TO_GATES))
                    {
                        level.destroyBlock(blockPos, true);
                    }
                }
            }
            // walls. not trivial so moving to a separate method.
            this.updateWallProperty(blockState, level, blockPos, p_53375_, otherPos, movedByPiston);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder)
    {
        stateBuilder.add(FACING, IN_WALL);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateWallProperty(BlockState blockState, Level level, BlockPos blockPos, Block p_53375_, BlockPos otherPos, boolean movedByPiston)
    {
        if (otherPos.getY() == blockPos.getY())
        {
            boolean hasWallOnTheSide = this.isWall(level.getBlockState(blockPos.relative(blockState.getValue(FACING).getClockWise()))) || this.isWall(level.getBlockState(blockPos.relative(blockState.getValue(FACING).getCounterClockWise())));
            if (blockState.getValue(IN_WALL) != hasWallOnTheSide)
            {
                BlockPos belowPos = blockPos.below();
                BlockState belowState = level.getBlockState(belowPos);
                belowState.handleNeighborChanged(level, belowPos, Blocks.STONE_BRICK_WALL, blockPos, movedByPiston);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return belowState.getCloneItemStack(target, level, pos, player);
    }

    // fire ///////////////////////////////////////

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.core.Direction direction)
    {
        if (this.flammabilityIgniteOdds < 0)
        {
            this.flammabilityIgniteOdds = level.getBlockState(pos.below()).getFireSpreadSpeed(level, pos, direction);
        }
        return this.flammabilityIgniteOdds;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.core.Direction direction)
    {
        if (this.flammabilityPerishOdds < 0)
        {
            this.flammabilityPerishOdds = level.getBlockState(pos.below()).getFlammability(level, pos, direction);
        }
        return this.flammabilityPerishOdds;
    }
    private int flammabilityIgniteOdds = -123, flammabilityPerishOdds = -123;
}

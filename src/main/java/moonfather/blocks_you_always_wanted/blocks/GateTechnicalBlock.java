package moonfather.blocks_you_always_wanted.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GateTechnicalBlock extends HorizontalDirectionalBlock
{
    public static final BooleanProperty IN_WALL = BlockStateProperties.IN_WALL;
    public static class ShapeSet
    {
        public static final VoxelShape EMPTY = Shapes.empty();
        public static final VoxelShape Z_SHAPE = Block.box(-5.0D, 9.0D, 6.0D, 21.0D, 27.0D, 10.0D);
        public static final VoxelShape X_SHAPE = Block.box(6.0D, 9.0D, -5.0D, 10.0D, 27.0D, 21.0D);
        public static final VoxelShape Z_SHAPE_NARROW = Block.box(-4.0D, 9.0D, 6.0D, 20.0D, 27.0D, 10.0D);
        public static final VoxelShape X_SHAPE_NARROW = Block.box(6.0D, 9.0D, -4.0D, 10.0D, 27.0D, 20.0D);
        public static final VoxelShape Z_COLLISION_SHAPE = Block.box(-4.0D, 0.0D, 6.0D, 20.0D, 28.0D, 10.0D);
        public static final VoxelShape X_COLLISION_SHAPE = Block.box(6.0D, 0.0D, -4.0D, 10.0D, 28.0D, 20.0D);
        public static final VoxelShape Z_SUPPORT_SHAPE = Z_SHAPE;
        public static final VoxelShape X_SUPPORT_SHAPE = X_SHAPE;
        public static final VoxelShape Z_OCCLUSION_SHAPE = Shapes.or(Block.box(-5.0D, 3.0D, 7.0D, -3.0D, 26.0D, 9.0D), Block.box(19.0D, 3.0D, 7.0D, 21.0D, 26.0D, 9.0D));
        public static final VoxelShape X_OCCLUSION_SHAPE = Shapes.or(Block.box(7.0D, 5.0D, -5.0D, 9.0D, 26.0D, -3.0D), Block.box(7.0D, 3.0D, 19.0D, 9.0D, 26.0D, 21.0D));
        public static final VoxelShape Z_OCCLUSION_SHAPE_NARROW = Shapes.or(Block.box(-4.0D, 3.0D, 7.0D, -2.0D, 26.0D, 9.0D), Block.box(18.0D, 3.0D, 7.0D, 20.0D, 26.0D, 9.0D));
        public static final VoxelShape X_OCCLUSION_SHAPE_NARROW = Shapes.or(Block.box(7.0D, 5.0D, -4.0D, 9.0D, 26.0D, -2.0D), Block.box(7.0D, 3.0D, 18.0D, 9.0D, 26.0D, 20.0D));
    }

    public GateTechnicalBlock()
    {
        super(Properties.of());
        this.registerDefaultState(this.stateDefinition.any().setValue(IN_WALL, Boolean.FALSE));
    }

    ///////////////////////////////////////////////////////////////////////////

    public VoxelShape getShape(BlockState p_53391_, BlockGetter p_53392_, BlockPos p_53393_, CollisionContext p_53394_) {
        if (p_53391_.getValue(IN_WALL)) {
            return p_53391_.getValue(FACING).getAxis() == Direction.Axis.X ? ShapeSet.X_SHAPE_NARROW : ShapeSet.Z_SHAPE_NARROW;
        } else {
            return p_53391_.getValue(FACING).getAxis() == Direction.Axis.X ? ShapeSet.X_SHAPE : ShapeSet.Z_SHAPE;
        }
    }

    public BlockState updateShape(BlockState p_53382_, Direction p_53383_, BlockState p_53384_, LevelAccessor p_53385_, BlockPos p_53386_, BlockPos p_53387_) {
        Direction.Axis direction$axis = p_53383_.getAxis();
        if (p_53382_.getValue(FACING).getClockWise().getAxis() != direction$axis) {
            return super.updateShape(p_53382_, p_53383_, p_53384_, p_53385_, p_53386_, p_53387_);
        } else {
            boolean flag = this.isWall(p_53384_) || this.isWall(p_53385_.getBlockState(p_53386_.relative(p_53383_.getOpposite())));
            return p_53382_.setValue(IN_WALL, Boolean.valueOf(flag));
        }
    }

    public VoxelShape getBlockSupportShape(BlockState p_253862_, BlockGetter p_254569_, BlockPos p_254197_) {
        if (false)
        //if (p_253862_.getValue(OPEN))
        {
            return ShapeSet.EMPTY;
        } else {
            return p_253862_.getValue(FACING).getAxis() == Direction.Axis.Z ? ShapeSet.Z_SUPPORT_SHAPE : ShapeSet.X_SUPPORT_SHAPE;
        }
    }

    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter p_53397_, BlockPos p_53398_, CollisionContext p_53399_)
    {
        if (false)
        //if (blockState.getValue(OPEN))
        {
            return ShapeSet.EMPTY;
        }
        return blockState.getValue(FACING).getAxis() == Direction.Axis.Z ? ShapeSet.Z_COLLISION_SHAPE : ShapeSet.X_COLLISION_SHAPE;
    }

    public VoxelShape getOcclusionShape(BlockState p_53401_, BlockGetter p_53402_, BlockPos p_53403_) {
        if (p_53401_.getValue(IN_WALL)) {
            return p_53401_.getValue(FACING).getAxis() == Direction.Axis.X ? ShapeSet.X_OCCLUSION_SHAPE_NARROW : ShapeSet.Z_OCCLUSION_SHAPE_NARROW;
        } else {
            return p_53401_.getValue(FACING).getAxis() == Direction.Axis.X ? ShapeSet.X_OCCLUSION_SHAPE : ShapeSet.Z_OCCLUSION_SHAPE;
        }
    }

    public boolean isPathfindable(BlockState p_53360_, BlockGetter p_53361_, BlockPos p_53362_, PathComputationType p_53363_) {
        switch (p_53363_) {
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

    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult blockHitResult)
    {
        BlockPos below = blockPos.below();
        BlockState belowState = level.getBlockState(below);
        return belowState.getBlock().use(belowState, level, below, player, hand, blockHitResult.withPosition(below));
    }

    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block p_53375_, BlockPos p_53376_, boolean p_53377_)
    {
        if (! level.isClientSide)
        {
            updateGateType(blockState, level, blockPos, p_53375_, p_53376_);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder)
    {
        stateBuilder.add(FACING, IN_WALL);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateGateType(BlockState blockState, Level level, BlockPos blockPos, Block p_53375_, BlockPos p_53376_)
    {

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return belowState.getCloneItemStack(target, level, below, player);
    }

}

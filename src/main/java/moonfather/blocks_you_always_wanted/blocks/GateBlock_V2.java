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
import net.minecraft.world.level.block.Blocks;
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

public class GateBlock_V2 extends HorizontalDirectionalBlock
{
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty IN_WALL = BlockStateProperties.IN_WALL;
    public static class ShapeSet
    {
        public static final VoxelShape EMPTY = Shapes.empty();
        public static final VoxelShape JUST_SLAB = Block.box(0, 0, 0, 16, 8, 16);
        public static final VoxelShape Z_SHAPE = Block.box(-5.0D, 9.0D, 6.0D, 21.0D, 27.0D, 10.0D);
        public static final VoxelShape X_SHAPE = Block.box(6.0D, 9.0D, -5.0D, 10.0D, 27.0D, 21.0D);
        public static final VoxelShape Z_SHAPE_NARROW = Block.box(-4.0D, 9.0D, 6.0D, 20.0D, 27.0D, 10.0D);
        public static final VoxelShape X_SHAPE_NARROW = Block.box(6.0D, 9.0D, -4.0D, 10.0D, 27.0D, 20.0D);
        public static final VoxelShape Z_COLLISION_SHAPE = Block.box(-4.0D, 0.0D, 6.0D, 20.0D, 28.0D, 10.0D);
        public static final VoxelShape X_COLLISION_SHAPE = Block.box(6.0D, 0.0D, -4.0D, 10.0D, 28.0D, 20.0D);
        public static final VoxelShape Z_COLLISION_SHAPE_SLAB = Shapes.or(Z_COLLISION_SHAPE, JUST_SLAB);
        public static final VoxelShape X_COLLISION_SHAPE_SLAB = Shapes.or(X_COLLISION_SHAPE, JUST_SLAB);
        public static final VoxelShape Z_COLLISION_SHAPE_RAIL = Shapes.or(Z_COLLISION_SHAPE, EMPTY);
        public static final VoxelShape X_COLLISION_SHAPE_RAIL = Shapes.or(X_COLLISION_SHAPE, EMPTY);
        public static final VoxelShape Z_SUPPORT_SHAPE = Z_SHAPE;
        public static final VoxelShape X_SUPPORT_SHAPE = X_SHAPE;
        public static final VoxelShape Z_OCCLUSION_SHAPE = Shapes.or(Block.box(-5.0D, 3.0D, 7.0D, -3.0D, 26.0D, 9.0D), Block.box(19.0D, 3.0D, 7.0D, 21.0D, 26.0D, 9.0D));
        public static final VoxelShape X_OCCLUSION_SHAPE = Shapes.or(Block.box(7.0D, 5.0D, -5.0D, 9.0D, 26.0D, -3.0D), Block.box(7.0D, 3.0D, 19.0D, 9.0D, 26.0D, 21.0D));
        public static final VoxelShape Z_OCCLUSION_SHAPE_NARROW = Shapes.or(Block.box(-4.0D, 3.0D, 7.0D, -2.0D, 26.0D, 9.0D), Block.box(18.0D, 3.0D, 7.0D, 20.0D, 26.0D, 9.0D));
        public static final VoxelShape X_OCCLUSION_SHAPE_NARROW = Shapes.or(Block.box(7.0D, 5.0D, -4.0D, 9.0D, 26.0D, -2.0D), Block.box(7.0D, 3.0D, 18.0D, 9.0D, 26.0D, 20.0D));
    }
    private final SoundEvent openSound;
    private final SoundEvent closeSound;
    private final Block matchingSlab;

    public GateBlock_V2(Block original, Block slab, WoodType woodType)
    {
        this(Properties.copy(original).sound(woodType.soundType()), woodType.fenceGateOpen(), woodType.fenceGateClose(), slab);
    }

    public GateBlock_V2(Properties properties, SoundEvent openSound, SoundEvent closeSound, Block slab)
    {
        super(properties);
        this.openSound = openSound;
        this.closeSound = closeSound;
        this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, Boolean.FALSE).setValue(POWERED, Boolean.FALSE).setValue(IN_WALL, Boolean.FALSE));
        this.matchingSlab = slab;
    }

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
    public BlockState updateShape(BlockState p_53382_, Direction p_53383_, BlockState p_53384_, LevelAccessor p_53385_, BlockPos p_53386_, BlockPos p_53387_) {
        return super.updateShape(p_53382_, p_53383_, p_53384_, p_53385_, p_53386_, p_53387_);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState p_253862_, BlockGetter p_254569_, BlockPos p_254197_) {
        if (p_253862_.getValue(OPEN)) {
            return ShapeSet.EMPTY;
        } else {
            return p_253862_.getValue(FACING).getAxis() == Direction.Axis.Z ? ShapeSet.Z_SUPPORT_SHAPE : ShapeSet.X_SUPPORT_SHAPE;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter p_53397_, BlockPos p_53398_, CollisionContext p_53399_)
    {
        int index = blockState.getValue(BLOCK_BELOW);
        if (blockState.getValue(OPEN))
        {
            if (index == ON_STONE_SLAB || index == ON_WOODEN_SLAB)
            {
                return ShapeSet.JUST_SLAB;
            }
            return ShapeSet.EMPTY;
        }
        if (index == ON_STONE_SLAB || index == ON_WOODEN_SLAB)
        {
            return blockState.getValue(FACING).getAxis() == Direction.Axis.Z ? ShapeSet.Z_COLLISION_SHAPE_SLAB : ShapeSet.X_COLLISION_SHAPE_SLAB;
        }
        if (index == ON_POWERED_RAIL || index == ON_REGULAR_RAIL)
        {
            return blockState.getValue(FACING).getAxis() == Direction.Axis.Z ? ShapeSet.Z_COLLISION_SHAPE_RAIL : ShapeSet.X_COLLISION_SHAPE_RAIL;
        }
        return blockState.getValue(FACING).getAxis() == Direction.Axis.Z ? ShapeSet.Z_COLLISION_SHAPE : ShapeSet.X_COLLISION_SHAPE;
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
    public boolean isPathfindable(BlockState p_53360_, BlockGetter p_53361_, BlockPos p_53362_, PathComputationType p_53363_) {
        switch (p_53363_) {
            case LAND:
                return p_53360_.getValue(OPEN);
            case WATER:
                return false;
            case AIR:
                return p_53360_.getValue(OPEN);
            default:
                return false;
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext)
    {
        Level level = blockPlaceContext.getLevel();
        BlockPos blockpos = blockPlaceContext.getClickedPos();
        boolean hasNeighborSignal = level.hasNeighborSignal(blockpos);
        Direction direction = blockPlaceContext.getHorizontalDirection();
        Direction.Axis direction$axis = direction.getAxis();
        boolean nextToWall = direction$axis == Direction.Axis.Z && (this.isWall(level.getBlockState(blockpos.west())) || this.isWall(level.getBlockState(blockpos.east()))) || direction$axis == Direction.Axis.X && (this.isWall(level.getBlockState(blockpos.north())) || this.isWall(level.getBlockState(blockpos.south())));
        return this.defaultBlockState().setValue(FACING, direction).setValue(OPEN, hasNeighborSignal).setValue(POWERED, hasNeighborSignal).setValue(IN_WALL, nextToWall);
    }

    private boolean isWall(BlockState other)
    {
        return other.is(BlockTags.WALLS);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult blockHitResult)
    {
        if (blockState.getValue(OPEN))
        {
            blockState = blockState.setValue(OPEN, Boolean.FALSE);
            level.setBlock(blockPos, blockState, 10);
        }
        else
        {
            Direction direction = player.getDirection();
            if (blockState.getValue(FACING) == direction.getOpposite())
            {
                blockState = blockState.setValue(FACING, direction);
            }
            blockState = blockState.setValue(OPEN, Boolean.TRUE);
            level.setBlock(blockPos, blockState, Block.UPDATE_ALL_IMMEDIATE);
        }
        boolean flag = blockState.getValue(OPEN);
        level.playSound(player, blockPos, flag ? this.openSound : this.closeSound, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        level.gameEvent(player, flag ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, blockPos);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block p_53375_, BlockPos otherPos, boolean p_53377_)
    {
        if (! level.isClientSide)
        {
            // technical block above
            if (blockPos.getX() == otherPos.getX() && blockPos.getZ() == otherPos.getZ() && blockPos.getY() + 1 == otherPos.getY())
            {
                BlockState above = level.getBlockState(otherPos);
                if (! (above.getBlock() instanceof GateTechnicalBlock))
                {
                    level.destroyBlock(blockPos, true);
                }
            }
            // signal
            boolean hasNeighborSignal = level.hasNeighborSignal(blockPos);
            if (blockState.getValue(POWERED) != hasNeighborSignal)
            {
                level.setBlock(blockPos, blockState.setValue(POWERED, hasNeighborSignal).setValue(OPEN, hasNeighborSignal), 2);
                if (blockState.getValue(OPEN) != hasNeighborSignal)
                {
                    level.playSound((Player)null, blockPos, hasNeighborSignal ? this.openSound : this.closeSound, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
                    level.gameEvent((Entity)null, hasNeighborSignal ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, blockPos);
                }
            }
            ///////   removal of block below   ////////
            int index = blockState.getValue(BLOCK_BELOW);
            BlockPos below = blockPos.below();
            if (index == ON_POWERED_RAIL || index == ON_REGULAR_RAIL)
            {
                if (! level.getBlockState(below).isFaceSturdy(level, below, Direction.UP))
                {
                    Block.popResource(level, blockPos, (index == ON_REGULAR_RAIL ? Items.RAIL : Items.POWERED_RAIL).getDefaultInstance());
                    Block replacement = GateBlock.fromRaisedGate(blockState.getBlock());
                    BlockState newState = replacement.withPropertiesOf(blockState);
                    level.setBlockAndUpdate(blockPos, newState);
                }
            }
            // something piston-pushed next to the gate?
            if (otherPos.getY() == blockPos.getY())
            {
                boolean onSide = ((blockPos.getX() == otherPos.getX()) && blockState.getValue(FACING).getAxis().equals(Direction.Axis.X))
                        || ((blockPos.getZ() == otherPos.getZ()) && blockState.getValue(FACING).getAxis().equals(Direction.Axis.Z));
                if (onSide) // don't check front of gate
                {
                    BlockState other = level.getBlockState(otherPos);
                    if (! other.isAir() && ! other.is(BlockTags.FENCES) && ! other.is(BlockTags.WALLS))
                    {
                        level.destroyBlock(blockPos, true);
                    }
                }
            }
            // walls. not trivial so moving to a separate method.
            this.updateWallProperty(blockState, level, blockPos, p_53375_, otherPos, p_53377_);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder)
    {
        stateBuilder.add(FACING, OPEN, POWERED, IN_WALL, BLOCK_BELOW);
    }
    public static final int ON_WOODEN_SLAB = 1;
    public static final int ON_STONE_SLAB = 2;
    public static final int ON_REGULAR_RAIL = 3;
    public static final int ON_POWERED_RAIL = 4;
    public static final IntegerProperty BLOCK_BELOW = IntegerProperty.create("block_below", 0, 4);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean slabMatches(Block existingSlab)
    {
        return this.matchingSlab != null && this.matchingSlab.equals(existingSlab);
    }

    private void updateWallProperty(BlockState blockState, Level level, BlockPos blockPos, Block p_53375_, BlockPos otherPos, boolean p_12345_)
    {
        boolean needFullUpdate = false;
        if (blockPos.getX() == otherPos.getX() && blockPos.getZ() == otherPos.getZ() && blockPos.getY() + 1 == otherPos.getY() && p_53375_.equals(Blocks.STONE_BRICK_WALL))
        {
            needFullUpdate = true;
        }
        if (otherPos.getY() == blockPos.getY())
        {
            boolean hasWallOnTheSide = this.isWall(level.getBlockState(blockPos.relative(blockState.getValue(FACING).getClockWise()))) || this.isWall(level.getBlockState(blockPos.relative(blockState.getValue(FACING).getCounterClockWise())));
            if (blockState.getValue(IN_WALL) == hasWallOnTheSide)
            {
                return;
            }
            if (hasWallOnTheSide)
            {
                // first wall; will propagate above normally
                level.setBlockAndUpdate(blockPos, blockState.setValue(IN_WALL, true));
            }
            else
            {
                // wall removed. but there maybe walls in above row, and we may not need to change state.
                needFullUpdate = true;
            }
        }
        if (! needFullUpdate)
        {
            return;
        }
        boolean hasWallOnTheSide = this.isWall(level.getBlockState(blockPos.relative(blockState.getValue(FACING).getClockWise())))
                || this.isWall(level.getBlockState(blockPos.relative(blockState.getValue(FACING).getCounterClockWise())))
                || this.isWall(level.getBlockState(blockPos.relative(blockState.getValue(FACING).getClockWise()).above()))
                || this.isWall(level.getBlockState(blockPos.relative(blockState.getValue(FACING).getCounterClockWise()).above()));
        level.setBlockAndUpdate(blockPos, blockState.setValue(IN_WALL, hasWallOnTheSide));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder)
    {
        List<ItemStack> result = super.getDrops(blockState, builder);
        int itemIndex = blockState.getValue(BLOCK_BELOW);
        if (itemIndex == ON_WOODEN_SLAB) result.add(this.matchingSlab.asItem().getDefaultInstance());
        if (itemIndex == ON_STONE_SLAB) result.add(Items.SMOOTH_STONE_SLAB.getDefaultInstance());
        if (itemIndex == ON_REGULAR_RAIL) result.add(Items.RAIL.getDefaultInstance());
        if (itemIndex == ON_POWERED_RAIL) result.add(Items.POWERED_RAIL.getDefaultInstance());
        return result;
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return GateBlock.fromRaisedGate(state.getBlock()).asItem().getDefaultInstance();
    }

}

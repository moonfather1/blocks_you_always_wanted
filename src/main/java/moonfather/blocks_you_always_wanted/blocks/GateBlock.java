package moonfather.blocks_you_always_wanted.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class GateBlock extends HorizontalDirectionalBlock
{
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty IN_WALL = BlockStateProperties.IN_WALL;

    public static class ShapeSet
    {
        public static final VoxelShape Z_SHAPE = Block.box(-5.0D, 3.0D, 6.0D, 21.0D, 26.0D, 10.0D);
        public static final VoxelShape X_SHAPE = Block.box(6.0D, 3.0D, -5.0D, 10.0D, 26.0D, 21.0D);
        public static final VoxelShape Z_SHAPE_NARROW = Block.box(-4.0D, 3.0D, 6.0D, 20.0D, 26.0D, 10.0D);
        public static final VoxelShape X_SHAPE_NARROW = Block.box(6.0D, 3.0D, -4.0D, 10.0D, 26.0D, 20.0D);
        public static final VoxelShape Z_COLLISION_SHAPE = Block.box(-4.0D, 2.0D, 6.0D, 20.0D, 28.0D, 10.0D);
        public static final VoxelShape X_COLLISION_SHAPE = Block.box(6.0D, 2.0D, -4.0D, 10.0D, 28.0D, 20.0D);
        public static final VoxelShape Z_SUPPORT_SHAPE = Z_SHAPE;
        public static final VoxelShape X_SUPPORT_SHAPE = X_SHAPE;
        public static final VoxelShape Z_OCCLUSION_SHAPE = Shapes.or(Block.box(-5.0D, 3.0D, 7.0D, -3.0D, 26.0D, 9.0D), Block.box(19.0D, 3.0D, 7.0D, 21.0D, 26.0D, 9.0D));
        public static final VoxelShape X_OCCLUSION_SHAPE = Shapes.or(Block.box(7.0D, 5.0D, -5.0D, 9.0D, 26.0D, -3.0D), Block.box(7.0D, 3.0D, 19.0D, 9.0D, 26.0D, 21.0D));
        public static final VoxelShape Z_OCCLUSION_SHAPE_NARROW = Shapes.or(Block.box(-4.0D, 3.0D, 7.0D, -2.0D, 26.0D, 9.0D), Block.box(18.0D, 3.0D, 7.0D, 20.0D, 26.0D, 9.0D));
        public static final VoxelShape X_OCCLUSION_SHAPE_NARROW = Shapes.or(Block.box(7.0D, 5.0D, -4.0D, 9.0D, 26.0D, -2.0D), Block.box(7.0D, 3.0D, 18.0D, 9.0D, 26.0D, 20.0D));
        public static final VoxelShape EMPTY = Shapes.empty();
    }
    private final net.minecraft.sounds.SoundEvent openSound;
    private final net.minecraft.sounds.SoundEvent closeSound;

    public GateBlock(Block original, WoodType woodType)
    {
        this(Properties.copy(original).sound(woodType.soundType()), woodType.fenceGateOpen(), woodType.fenceGateClose());
    }

    public GateBlock(Properties properties, SoundEvent openSound, SoundEvent closeSound)
    {
        super(properties);
        this.openSound = openSound;
        this.closeSound = closeSound;
        this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, Boolean.FALSE).setValue(POWERED, Boolean.FALSE).setValue(IN_WALL, Boolean.FALSE));
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
        if (p_253862_.getValue(OPEN)) {
            return ShapeSet.EMPTY;
        } else {
            return p_253862_.getValue(FACING).getAxis() == Direction.Axis.Z ? ShapeSet.Z_SUPPORT_SHAPE : ShapeSet.X_SUPPORT_SHAPE;
        }
    }

    public VoxelShape getCollisionShape(BlockState p_53396_, BlockGetter p_53397_, BlockPos p_53398_, CollisionContext p_53399_) {
        if (p_53396_.getValue(OPEN)) {
            return ShapeSet.EMPTY;
        } else {
            return p_53396_.getValue(FACING).getAxis() == Direction.Axis.Z ? ShapeSet.Z_COLLISION_SHAPE : ShapeSet.X_COLLISION_SHAPE;
        }
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
                return p_53360_.getValue(OPEN);
            case WATER:
                return false;
            case AIR:
                return p_53360_.getValue(OPEN);
            default:
                return false;
        }
    }

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

    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult blockHitResult)
    {
        // normal gate to raised gate conversion.
        if (hand.equals(InteractionHand.MAIN_HAND) && (player.getMainHandItem().is(Items.RAIL) || player.getMainHandItem().is(Items.POWERED_RAIL)))
        {
            blockState = GateBlock.toRaisedGate(blockState.getBlock()).withPropertiesOf(blockState).setValue(GateBlock_V2.BLOCK_BELOW, GateBlock.railToStateIndex(player.getMainHandItem().getItem()));
            level.setBlock(blockPos, blockState, 11);
            level.playSound(player, blockPos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        //System.out.println("~~~cli==" + level.isClientSide + ",  dist==" + player.position().distanceTo(blockPos.getCenter()));
        // open and close
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
            level.setBlock(blockPos, blockState, 10);
        }
        boolean flag = blockState.getValue(OPEN);
        level.playSound(player, blockPos, flag ? this.openSound : this.closeSound, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        level.gameEvent(player, flag ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, blockPos);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block p_53375_, BlockPos p_53376_, boolean p_53377_)
    {
        if (! level.isClientSide)
        {
            boolean hasNeighborSignal = level.hasNeighborSignal(blockPos);
            if (blockState.getValue(POWERED) != hasNeighborSignal)
            {
                level.setBlock(blockPos, blockState.setValue(POWERED, hasNeighborSignal).setValue(OPEN, hasNeighborSignal), 2);
                if (blockState.getValue(OPEN) != hasNeighborSignal)
                {
                    level.playSound((Player)null, blockPos, hasNeighborSignal ? this.openSound : this.closeSound, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
                    level.gameEvent((Entity)null, hasNeighborSignal ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, blockPos);
                }
                updateGateType(blockState, level, blockPos, p_53375_, p_53376_);
            }
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder)
    {
        stateBuilder.add(FACING, OPEN, POWERED, IN_WALL);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateGateType(BlockState blockState, Level level, BlockPos blockPos, Block p_53375_, BlockPos p_53376_)
    {

    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    /////////////////////////////////////////////////////////////////////////////////

    public static Block toRaisedGate(Block raisedGate)
    {
        ResourceLocation gateRL = ForgeRegistries.BLOCKS.getKey(raisedGate);
        gateRL = new ResourceLocation(gateRL.getNamespace(), gateRL.getPath().replace("gate_main", "gate_spec"));
        return ForgeRegistries.BLOCKS.getValue(gateRL);
    }

    public static Block fromRaisedGate(Block raisedGate)
    {
        ResourceLocation gateRL = ForgeRegistries.BLOCKS.getKey(raisedGate);
        gateRL = new ResourceLocation(gateRL.getNamespace(), gateRL.getPath().replace("gate_spec", "gate_main"));
        return ForgeRegistries.BLOCKS.getValue(gateRL);
    }

    public static int railToStateIndex(Item rail)
    {
        if (rail.equals(Items.POWERED_RAIL)) return GateBlock_V2.ON_POWERED_RAIL;
        if (rail.equals(Items.RAIL)) return GateBlock_V2.ON_REGULAR_RAIL;
        return 0;
    }

    public static int blockToStateIndex(Block block)
    {
        if (block.equals(Blocks.SMOOTH_STONE_SLAB)) return GateBlock_V2.ON_STONE_SLAB;
        if (block instanceof SlabBlock) return GateBlock_V2.ON_WOODEN_SLAB;
        if (block.equals(Blocks.RAIL)) return GateBlock_V2.ON_REGULAR_RAIL;
        if (block.equals(Blocks.POWERED_RAIL)) return GateBlock_V2.ON_POWERED_RAIL;
        return 0;
    }
    public static int blockToStateIndex(Block block1, Block block2)
    {
        if (block1 != null) return blockToStateIndex(block1);
        return blockToStateIndex(block2);
    }
}

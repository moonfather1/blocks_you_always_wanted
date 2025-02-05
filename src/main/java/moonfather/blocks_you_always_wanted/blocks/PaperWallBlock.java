package moonfather.blocks_you_always_wanted.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class PaperWallBlock extends Block
{
    public PaperWallBlock()
    {
        super(Properties.of().pushReaction(PushReaction.DESTROY).strength(0.2f, 1e-5f).ignitedByLava().sound(SoundType.CROP));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(VERTICAL_VARIANT, 1).setValue(HORIZONTAL_VARIANT, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder)
    {
        stateBuilder.add(BeehiveBlock.FACING, VERTICAL_VARIANT, HORIZONTAL_VARIANT);
    }
    private static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final IntegerProperty VERTICAL_VARIANT = IntegerProperty.create("vertical", 0, 2); // compressed: top, middle, bottom
    private static final IntegerProperty HORIZONTAL_VARIANT = IntegerProperty.create("horizontal", 0, 2); // compressed: left, middle, right

    ////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        if (state.getValue(FACING).equals(Direction.EAST)) return SHAPE_E;
        if (state.getValue(FACING).equals(Direction.WEST)) return SHAPE_W;
        if (state.getValue(FACING).equals(Direction.SOUTH)) return SHAPE_S;
        return SHAPE_N;
    }

    private static final VoxelShape SHAPE_N = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
    private static final VoxelShape SHAPE_W = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_S = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_E = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext)
    {
        Level level = blockPlaceContext.getLevel();
        BlockPos blockpos = blockPlaceContext.getClickedPos();
        Direction direction = blockPlaceContext.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block p_53375_, BlockPos otherPos, boolean movedByPiston)
    {
        if (! level.isClientSide)
        {

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.core.Direction direction) { return 2; }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.core.Direction direction) { return 80; }
}

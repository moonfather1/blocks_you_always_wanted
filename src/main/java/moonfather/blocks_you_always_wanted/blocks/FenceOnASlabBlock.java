package moonfather.blocks_you_always_wanted.blocks;

import moonfather.blocks_you_always_wanted.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FenceOnASlabBlock extends FenceBlock
{
    public FenceOnASlabBlock(Block original)
    {
        super(BlockBehaviour.Properties.of().mapColor(original.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava().sound(SoundType.WOOD));
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(NORTH_DOWN, Boolean.FALSE).setValue(EAST_DOWN, Boolean.FALSE).setValue(SOUTH_DOWN, Boolean.FALSE).setValue(WEST_DOWN, Boolean.FALSE).setValue(NORTH_UP, Boolean.FALSE).setValue(EAST_UP, Boolean.FALSE).setValue(SOUTH_UP, Boolean.FALSE).setValue(WEST_UP, Boolean.FALSE).setValue(WATERLOGGED, Boolean.FALSE));
        this.original = (FenceBlock) original;
        if (collisionShapes == null)
        {
            collisionShapes = new VoxelShape[16*16*16];
            interactionShapes = new VoxelShape[16*16*16];
            makeShapes();
        }
    }
    private final FenceBlock original;

    ////////////////////////////////////////////////

    private static final VoxelShape SHAPE_POST = Block.box(6.0D, 8.0D, 6.0D, 10.0D, 24.0D, 10.0D);
    private static final VoxelShape SHAPE_SLAB = Block.box(0.0D, 0.0D, 0.0D, 16.0D,  8.0D, 16.0D);
    private static final VoxelShape SHAPE_MAIN = Shapes.or(SHAPE_SLAB, SHAPE_POST);
    private static final VoxelShape SHAPE_HOR_WEST = Block.box(00.0D, 14.0D, 6.0D, 06.0D,  23.0D, 10.0D); //7-9
    private static final VoxelShape SHAPE_HOR_EAST = Block.box(10.0D, 14.0D, 6.0D, 16.0D,  23.0D, 10.0D);
    private static final VoxelShape SHAPE_HOR_NORTH = Block.box(6.0D, 14.0D, 00.0D, 10.0D,  23.0D, 06.0D);
    private static final VoxelShape SHAPE_HOR_SOUTH = Block.box(6.0D, 14.0D, 10.0D, 10.0D,  23.0D, 16.0D);
    private static final VoxelShape SHAPE_DN_WEST = Block.box(00.0D, 08.0D, 6.0D, 06.0D,  19.0D, 10.0D);
    private static final VoxelShape SHAPE_DN_EAST = Block.box(10.0D, 08.0D, 6.0D, 16.0D,  19.0D, 10.0D);
    private static final VoxelShape SHAPE_DN_NORTH = Block.box(6.0D, 08.0D, 00.0D, 10.0D,  19.0D, 06.0D);
    private static final VoxelShape SHAPE_DN_SOUTH = Block.box(6.0D, 08.0D, 10.0D, 10.0D,  19.0D, 16.0D);
    private static final VoxelShape SHAPE_UP_WEST = Block.box(00.0D, 15.0D, 6.0D, 06.0D,  24.0D, 10.0D);
    private static final VoxelShape SHAPE_UP_EAST = Block.box(10.0D, 15.0D, 6.0D, 16.0D,  24.0D, 10.0D);
    private static final VoxelShape SHAPE_UP_NORTH = Block.box(6.0D, 15.0D, 00.0D, 10.0D,  24.0D, 06.0D);
    private static final VoxelShape SHAPE_UP_SOUTH = Block.box(6.0D, 15.0D, 10.0D, 10.0D,  24.0D, 16.0D);

    // for collision we will add some more on the top
    private static final VoxelShape SHAPE_UP_WEST_COLL = Block.box(00.0D, 15.0D, 6.0D, 06.0D,  31.0D, 10.0D);
    private static final VoxelShape SHAPE_UP_EAST_COLL = Block.box(10.0D, 15.0D, 6.0D, 16.0D,  31.0D, 10.0D);
    private static final VoxelShape SHAPE_UP_NORTH_COLL = Block.box(6.0D, 15.0D, 00.0D, 10.0D,  31.0D, 06.0D);
    private static final VoxelShape SHAPE_UP_SOUTH_COLL = Block.box(6.0D, 15.0D, 10.0D, 10.0D,  31.0D, 16.0D);
    private static final VoxelShape SHAPE_DN_WEST_COLL = Block.box(-2.0D, 08.0D, 6.0D, 06.0D,  25.0D, 10.0D);
    private static final VoxelShape SHAPE_DN_EAST_COLL = Block.box(10.0D, 08.0D, 6.0D, 18.0D,  25.0D, 10.0D);
    private static final VoxelShape SHAPE_DN_NORTH_COLL = Block.box(6.0D, 08.0D, -2.0D, 10.0D,  25.0D, 06.0D);
    private static final VoxelShape SHAPE_DN_SOUTH_COLL = Block.box(6.0D, 08.0D, 10.0D, 10.0D,  25.0D, 18.0D);
    private static final VoxelShape SHAPE_HOR_WEST_COLL = Block.box(00.0D, 14.0D, 6.0D, 06.0D,  31.0D, 10.0D);
    private static final VoxelShape SHAPE_HOR_EAST_COLL = Block.box(10.0D, 14.0D, 6.0D, 16.0D,  31.0D, 10.0D);
    private static final VoxelShape SHAPE_HOR_NORTH_COLL = Block.box(6.0D, 14.0D, 00.0D, 10.0D,  31.0D, 06.0D);
    private static final VoxelShape SHAPE_HOR_SOUTH_COLL = Block.box(6.0D, 14.0D, 10.0D, 10.0D,  31.0D, 16.0D);
    private static final VoxelShape SHAPE_POST_COLL = Block.box(6.0D, 8.0D, 6.0D, 10.0D, 31.0D, 10.0D);
    private static final VoxelShape SHAPE_MAIN_COLL = Shapes.or(SHAPE_SLAB, SHAPE_POST_COLL);

    private static int getShapeIndex(BlockState blockState)
    {
        int result = 0;
        result += blockState.getValue(NORTH) ? 1 : 0;
        result += blockState.getValue(WEST) ? 2 : 0;
        result += blockState.getValue(EAST) ? 4 : 0;
        result += blockState.getValue(SOUTH) ? 8 : 0;
        result += blockState.getValue(NORTH_DOWN) ? 16 : 0;
        result += blockState.getValue(WEST_DOWN) ? 32 : 0;
        result += blockState.getValue(EAST_DOWN) ? 64 : 0;
        result += blockState.getValue(SOUTH_DOWN) ? 128 : 0;
        result += blockState.getValue(NORTH_UP) ? 256 : 0;
        result += blockState.getValue(WEST_UP) ? 512 : 0;
        result += blockState.getValue(EAST_UP) ? 1024 : 0;
        result += blockState.getValue(SOUTH_UP) ? 2048 : 0;
        return result;
    }

    private static void makeShapes()
    {
        for (int i = 0; i < 16*16*16; i++)
        {
            interactionShapes[i] = SHAPE_MAIN;
            if ((i &    1) > 0) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_HOR_NORTH);
            if ((i &    2) > 0) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_HOR_WEST);
            if ((i &    4) > 0) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_HOR_EAST);
            if ((i &    8) > 0) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_HOR_SOUTH);
            if ((i &   16) > 0) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_DN_NORTH);
            if ((i &   32) > 0) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_DN_WEST);
            if ((i &   64) > 0) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_DN_EAST);
            if ((i &  128) > 0) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_DN_SOUTH);
            if ((i &  256) > 0) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_UP_NORTH);
            if ((i &  512) > 0) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_UP_WEST);
            if ((i & 1024) > 0) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_UP_EAST);
            if ((i & 2048) > 0) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_UP_SOUTH);
        }
        for (int i = 0; i < 16*16*16; i++)
        {
            collisionShapes[i] = SHAPE_MAIN_COLL;
            if ((i &    1) > 0) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_HOR_NORTH_COLL);
            if ((i &    2) > 0) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_HOR_WEST_COLL);
            if ((i &    4) > 0) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_HOR_EAST_COLL);
            if ((i &    8) > 0) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_HOR_SOUTH_COLL);
            if ((i &   16) > 0) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_DN_NORTH_COLL);
            if ((i &   32) > 0) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_DN_WEST_COLL);
            if ((i &   64) > 0) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_DN_EAST_COLL);
            if ((i &  128) > 0) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_DN_SOUTH_COLL);
            if ((i &  256) > 0) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_UP_NORTH_COLL);
            if ((i &  512) > 0) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_UP_WEST_COLL);
            if ((i & 1024) > 0) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_UP_EAST_COLL);
            if ((i & 2048) > 0) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_UP_SOUTH_COLL);
        }
    }
    private static VoxelShape[] collisionShapes = null, interactionShapes = null;

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext)
    {
        return interactionShapes[getShapeIndex(blockState)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext)
    {
        return collisionShapes[getShapeIndex(blockState)];
    }

    ////////////////////////////////////////////////


    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        return this.original.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    ////////////////////////////////////////////////



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
    public void neighborChanged(BlockState state, Level level, BlockPos blockPos, Block other, BlockPos otherPos, boolean p_60514_)
    {
        boolean changed = false;
        BlockPos.MutableBlockPos pos = blockPos.mutable();
        // E
        Direction direction = Direction.EAST;
        BlockState adjacentState = level.getBlockState(pos.move(direction));
        BlockState adjacentAbove = adjacentState.isFaceSturdy(level, pos, direction.getOpposite()) ? level.getBlockState(pos.move(Direction.UP)) : Blocks.AIR.defaultBlockState();
        Block adjacent = adjacentState.getBlock();
        boolean levelValue = (adjacent instanceof FenceOnASlabBlock) || adjacentState.isFaceSturdy(level, pos.move(Direction.DOWN), direction.getOpposite()) && adjacentAbove.isFaceSturdy(level, pos.move(Direction.UP), direction.getOpposite());
        boolean levelValueDn = adjacent instanceof FenceBlock && ! (adjacent instanceof FenceOnASlabBlock);
        changed |= state.getValue(EAST) != levelValue;
        changed |= state.getValue(EAST_DOWN) != levelValueDn;
        state = state.setValue(EAST, levelValue);
        state = state.setValue(EAST_DOWN, levelValueDn);
        pos.set(blockPos);
        // W
        direction = Direction.WEST;
        adjacentState = level.getBlockState(pos.move(direction));
        adjacentAbove = adjacentState.isFaceSturdy(level, pos, direction.getOpposite()) ? level.getBlockState(pos.move(Direction.UP)) : Blocks.AIR.defaultBlockState();
        adjacent = adjacentState.getBlock();
        levelValue = (adjacent instanceof FenceOnASlabBlock) || adjacentState.isFaceSturdy(level, pos.move(Direction.DOWN), direction.getOpposite()) && adjacentAbove.isFaceSturdy(level, pos.move(Direction.UP), direction.getOpposite());
        levelValueDn = adjacent instanceof FenceBlock && ! (adjacent instanceof FenceOnASlabBlock);
        changed |= state.getValue(WEST) != levelValue;
        changed |= state.getValue(WEST_DOWN) != levelValueDn;
        state = state.setValue(WEST, levelValue);
        state = state.setValue(WEST_DOWN, levelValueDn);
        pos.set(blockPos);
        // N
        direction = Direction.NORTH;
        adjacentState = level.getBlockState(pos.move(direction));
        adjacentAbove = adjacentState.isFaceSturdy(level, pos, direction.getOpposite()) ? level.getBlockState(pos.move(Direction.UP)) : Blocks.AIR.defaultBlockState();
        adjacent = adjacentState.getBlock();
        levelValue = (adjacent instanceof FenceOnASlabBlock) || adjacentState.isFaceSturdy(level, pos.move(Direction.DOWN), direction.getOpposite()) && adjacentAbove.isFaceSturdy(level, pos.move(Direction.UP), direction.getOpposite());
        levelValueDn = adjacent instanceof FenceBlock && ! (adjacent instanceof FenceOnASlabBlock);
        changed |= state.getValue(NORTH) != levelValue;
        changed |= state.getValue(NORTH_DOWN) != levelValueDn;
        state = state.setValue(NORTH, levelValue);
        state = state.setValue(NORTH_DOWN, levelValueDn);
        pos.set(blockPos);
        // S
        direction = Direction.SOUTH;
        adjacentState = level.getBlockState(pos.move(direction));
        adjacentAbove = adjacentState.isFaceSturdy(level, pos, direction.getOpposite()) ? level.getBlockState(pos.move(Direction.UP)) : Blocks.AIR.defaultBlockState();
        adjacent = adjacentState.getBlock();
        levelValue = (adjacent instanceof FenceOnASlabBlock) || adjacentState.isFaceSturdy(level, pos.move(Direction.DOWN), direction.getOpposite()) && adjacentAbove.isFaceSturdy(level, pos.move(Direction.UP), direction.getOpposite());
        levelValueDn = adjacent instanceof FenceBlock && ! (adjacent instanceof FenceOnASlabBlock);
        changed |= state.getValue(SOUTH) != levelValue;
        changed |= state.getValue(SOUTH_DOWN) != levelValueDn;
        state = state.setValue(SOUTH, levelValue);
        state = state.setValue(SOUTH_DOWN, levelValueDn);
        pos.set(blockPos);
        // upper level
        adjacent = level.getBlockState(pos.move(Direction.EAST).move(Direction.UP)).getBlock();
        changed |= state.getValue(EAST_UP) != (adjacent instanceof FenceBlock && ! (adjacent instanceof FenceOnASlabBlock));
        state = state.setValue(EAST_UP, adjacent instanceof FenceBlock && ! (adjacent instanceof FenceOnASlabBlock));
        adjacent = level.getBlockState(pos.move(Direction.WEST).move(Direction.WEST)).getBlock();
        changed |= state.getValue(WEST_UP) != (adjacent instanceof FenceBlock && ! (adjacent instanceof FenceOnASlabBlock));
        state = state.setValue(WEST_UP, adjacent instanceof FenceBlock && ! (adjacent instanceof FenceOnASlabBlock));
        adjacent = level.getBlockState(pos.move(Direction.EAST).move(Direction.NORTH)).getBlock();
        changed |= state.getValue(NORTH_UP) != (adjacent instanceof FenceBlock && ! (adjacent instanceof FenceOnASlabBlock));
        state = state.setValue(NORTH_UP, adjacent instanceof FenceBlock && ! (adjacent instanceof FenceOnASlabBlock));
        adjacent = level.getBlockState(pos.move(Direction.SOUTH).move(Direction.SOUTH)).getBlock();
        changed |= state.getValue(SOUTH_UP) != (adjacent instanceof FenceBlock && ! (adjacent instanceof FenceOnASlabBlock));
        state = state.setValue(SOUTH_UP, adjacent instanceof FenceBlock && ! (adjacent instanceof FenceOnASlabBlock));
        if (changed)
        {
            level.setBlockAndUpdate(blockPos, state);
        }
    }

    ////////////////////////////////////

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        double y = hitResult.getLocation().y;
        y = y - blockPos.getY();
        if (y <= 0.5)
        {
            //slab right-clicked. pass result will probably result in a block being placed next to the slab.
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (y > 1.0 && (hitResult.getLocation().x > 6/16d && hitResult.getLocation().x < 10/16d) && (hitResult.getLocation().z > 6/16d && hitResult.getLocation().z < 10/16d))
        {
            // top part right-clicked. let's have tech block handle it.
            BlockState stateAbove = level.getBlockState(blockPos.above());
            return stateAbove.useItemOn(stack, level, player, hand, hitResult.withPosition(blockPos.above()));
        }
        // middle or sides. no action at the moment.
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    ////////////////////////////////////


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53334_)
    {
        p_53334_.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
        p_53334_.add(NORTH_UP, EAST_UP, WEST_UP, SOUTH_UP);
        p_53334_.add(NORTH_DOWN, EAST_DOWN, WEST_DOWN, SOUTH_DOWN);
    }

    public static final BooleanProperty NORTH_UP = BooleanProperty.create("north_up");
    public static final BooleanProperty EAST_UP = BooleanProperty.create("east_up");
    public static final BooleanProperty SOUTH_UP = BooleanProperty.create("south_up");
    public static final BooleanProperty WEST_UP = BooleanProperty.create("west_up");
    public static final BooleanProperty NORTH_DOWN = BooleanProperty.create("north_dn");
    public static final BooleanProperty EAST_DOWN = BooleanProperty.create("east_dn");
    public static final BooleanProperty SOUTH_DOWN = BooleanProperty.create("south_dn");
    public static final BooleanProperty WEST_DOWN = BooleanProperty.create("west_dn");

    ///////////////////////////////////////////////////////////////////////////////

    public static FenceTechnicalBlock technical()
    {
        if (technicalBlockInstance == null)
        {
            technicalBlockInstance = (FenceTechnicalBlock) BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Constants.MODID, "fence_technical_block"));
        }
        return technicalBlockInstance;
    }
    private static FenceTechnicalBlock technicalBlockInstance = null;
}

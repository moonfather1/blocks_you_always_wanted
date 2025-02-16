package moonfather.blocks_you_always_wanted.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FenceVersion3Block extends Block implements SimpleWaterloggedBlock
{
    public FenceVersion3Block(Block original)
    {
        super(Properties.ofFullCopy(original).lightLevel(FenceVersion3Block::getLightLevel));
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH_COMPRESSED, 0).setValue(EAST_COMPRESSED, 0)
            .setValue(SOUTH_COMPRESSED, 0).setValue(WEST_COMPRESSED, 0)
            .setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE).setValue(TOP_VARIANT, 0));
        this.original = original;
        if (collisionShapes == null)
        {
            collisionShapes = new VoxelShape[5*5*5*5*3]; // 5s are connections, 3 is top addon
            interactionShapes = new VoxelShape[5*5*5*5*3];
            makeShapes();
        }
    }
    private final Block original;

    //-------------------------------------------------------------------------

    private static final VoxelShape SHAPE_POST = Block.box(6.0D, -8.0D, 6.0D, 10.0D, 8.0D, 10.0D);
    private static final VoxelShape SHAPE_HOR_WEST = Block.box(00.0D, -2.0D, 6.0D, 06.0D,  7.0D, 10.0D);
    private static final VoxelShape SHAPE_HOR_EAST = Block.box(10.0D, -2.0D, 6.0D, 16.0D,  7.0D, 10.0D);
    private static final VoxelShape SHAPE_HOR_NORTH = Block.box(6.0D, -2.0D, 00.0D, 10.0D,  7.0D, 06.0D);
    private static final VoxelShape SHAPE_HOR_SOUTH = Block.box(6.0D, -2.0D, 10.0D, 10.0D,  7.0D, 16.0D);
    private static final VoxelShape SHAPE_DN_WEST = Block.box(00.0D, -8.0D, 6.0D, 06.0D,  3.0D, 10.0D);
    private static final VoxelShape SHAPE_DN_EAST = Block.box(10.0D, -8.0D, 6.0D, 16.0D,  3.0D, 10.0D);
    private static final VoxelShape SHAPE_DN_NORTH = Block.box(6.0D, -8.0D, 00.0D, 10.0D,  3.0D, 06.0D);
    private static final VoxelShape SHAPE_DN_SOUTH = Block.box(6.0D, -8.0D, 10.0D, 10.0D,  3.0D, 16.0D);
    private static final VoxelShape SHAPE_UP_WEST = Block.box(00.0D, -1.0D, 6.0D, 06.0D,  8.0D, 10.0D);
    private static final VoxelShape SHAPE_UP_EAST = Block.box(10.0D, -1.0D, 6.0D, 16.0D,  8.0D, 10.0D);
    private static final VoxelShape SHAPE_UP_NORTH = Block.box(6.0D, -1.0D, 00.0D, 10.0D,  8.0D, 06.0D);
    private static final VoxelShape SHAPE_UP_SOUTH = Block.box(6.0D, -1.0D, 10.0D, 10.0D,  8.0D, 16.0D);

    private static final VoxelShape SHAPE_ON_TOP_TORCH = Block.box(7.0D, 8.0D, 7.0D, 9.0D,  16.0D, 9.0D);
    private static final VoxelShape SHAPE_ON_TOP_LAMP = Block.box(5.0D, 8.0D, 5.0D, 11.0D,  16.0D, 11.0D);

    // for collision we will add some more on the top
    private static final VoxelShape SHAPE_UP_WEST_COLL = Block.box(00.0D, -1.0D, 6.0D, 06.0D,  15.0D, 10.0D);
    private static final VoxelShape SHAPE_UP_EAST_COLL = Block.box(10.0D, -1.0D, 6.0D, 16.0D,  15.0D, 10.0D);
    private static final VoxelShape SHAPE_UP_NORTH_COLL = Block.box(6.0D, -1.0D, 00.0D, 10.0D,  15.0D, 06.0D);
    private static final VoxelShape SHAPE_UP_SOUTH_COLL = Block.box(6.0D, -1.0D, 10.0D, 10.0D,  15.0D, 16.0D);
    private static final VoxelShape SHAPE_DN_WEST_COLL = Block.box(-2.0D, -8.0D, 6.0D, 06.0D,  9.0D, 10.0D);
    private static final VoxelShape SHAPE_DN_EAST_COLL = Block.box(10.0D, -8.0D, 6.0D, 18.0D,  9.0D, 10.0D);
    private static final VoxelShape SHAPE_DN_NORTH_COLL = Block.box(6.0D, -8.0D, -2.0D, 10.0D,  9.0D, 06.0D);
    private static final VoxelShape SHAPE_DN_SOUTH_COLL = Block.box(6.0D, -8.0D, 10.0D, 10.0D,  9.0D, 18.0D);
    private static final VoxelShape SHAPE_HOR_WEST_COLL = Block.box(00.0D, -2.0D, 6.0D, 06.0D,  15.0D, 10.0D);
    private static final VoxelShape SHAPE_HOR_EAST_COLL = Block.box(10.0D, -2.0D, 6.0D, 16.0D,  15.0D, 10.0D);
    private static final VoxelShape SHAPE_HOR_NORTH_COLL = Block.box(6.0D, -2.0D, 00.0D, 10.0D,  15.0D, 06.0D);
    private static final VoxelShape SHAPE_HOR_SOUTH_COLL = Block.box(6.0D, -2.0D, 10.0D, 10.0D,  15.0D, 16.0D);
    private static final VoxelShape SHAPE_POST_COLL = Block.box(6.0D, -8.0D, 6.0D, 10.0D, 15.5D, 10.0D);

    private static int getShapeIndex(BlockState blockState)
    {
        int result = 0;
        int top_addon = blockState.getValue(TOP_VARIANT);
        int top_addon_resolved = (top_addon == HAS_LANTERN1 || top_addon == HAS_LANTERN2) ? 1 : ((top_addon == HAS_TORCH1 || top_addon == HAS_TORCH2 || top_addon == HAS_TORCH3) ? 2 : 0);
        result += blockState.getValue(NORTH_COMPRESSED);
        result += blockState.getValue(WEST_COMPRESSED) * 5;
        result += blockState.getValue(EAST_COMPRESSED) * 5 * 5;
        result += blockState.getValue(SOUTH_COMPRESSED) * 5 * 5 * 5;
        result += top_addon_resolved * 5 * 5 * 5 * 5;
        return result; // limit is 5*5*5*5*3,   5s are connections, 3 is top addon
    }

    private static void makeShapes()
    {
        int compressed;
        for (int i = 0; i < 5*5*5*5*3; i++) // 5s are connections, 3 is top addon
        {
            interactionShapes[i] = SHAPE_POST;
            compressed = i % 5;
            if (compressed == 1 || compressed == 3) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_DN_NORTH);
            if (compressed == 2 || compressed == 3) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_UP_NORTH);
            if (compressed == 4) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_HOR_NORTH);
            compressed = (i / 5) % 5;
            if (compressed == 1 || compressed == 3) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_DN_WEST);
            if (compressed == 2 || compressed == 3) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_UP_WEST);
            if (compressed == 4) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_HOR_WEST);
            compressed = (i / 5 / 5) % 5;
            if (compressed == 1 || compressed == 3) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_DN_EAST);
            if (compressed == 2 || compressed == 3) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_UP_EAST);
            if (compressed == 4) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_HOR_EAST);
            compressed = (i / 5 / 5 / 5) % 5;
            if (compressed == 1 || compressed == 3) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_DN_SOUTH);
            if (compressed == 2 || compressed == 3) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_UP_SOUTH);
            if (compressed == 4) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_HOR_SOUTH);
            compressed = i / 5 / 5 / 5 / 5;
            if (compressed == 1) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_ON_TOP_LAMP);
            if (compressed == 2) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_ON_TOP_TORCH);
        }
        for (int i = 0; i < 5*5*5*5*3; i++) // 5s are connections, 3 is top addon
        {
            collisionShapes[i] = SHAPE_POST_COLL;
            compressed = i % 5;
            if (compressed == 1 || compressed == 3) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_DN_NORTH_COLL);
            if (compressed == 2 || compressed == 3) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_UP_NORTH_COLL);
            if (compressed == 4) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_HOR_NORTH_COLL);
            compressed = (i / 5) % 5;
            if (compressed == 1 || compressed == 3) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_DN_WEST_COLL);
            if (compressed == 2 || compressed == 3) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_UP_WEST_COLL);
            if (compressed == 4) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_HOR_WEST_COLL);
            compressed = (i / 5 / 5) % 5;
            if (compressed == 1 || compressed == 3) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_DN_EAST_COLL);
            if (compressed == 2 || compressed == 3) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_UP_EAST_COLL);
            if (compressed == 4) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_HOR_EAST_COLL);
            compressed = (i / 5 / 5 / 5) % 5;
            if (compressed == 1 || compressed == 3) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_DN_SOUTH_COLL);
            if (compressed == 2 || compressed == 3) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_UP_SOUTH_COLL);
            if (compressed == 4) collisionShapes[i] = Shapes.or(collisionShapes[i], SHAPE_HOR_SOUTH_COLL);
            // there are two more bits for lamps and torches; those don't affect collision.
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

    //-------------------------------------------------------------------------

    @Override
    protected List<ItemStack> getDrops(BlockState blockState, LootParams.Builder params)
    {
        List<ItemStack> result = original.defaultBlockState().getDrops(params);
        ItemStack drop = stateToItem(blockState.getValue(TOP_VARIANT));
        if (! drop.isEmpty())
        {
            result.add(drop);
        }
        return result;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        double y = target.getLocation().y - pos.getY();
        if (y > 0.5)
        {
            ItemStack addon = stateToItem(state.getValue(TOP_VARIANT));
            if (! addon.isEmpty())
            {
                return addon;
            }
        }
        return this.original.getCloneItemStack(state, target, level, pos, player); // fence
    }

    private static ItemStack stateToItem(int state)
    {
        if (state == HAS_TORCH2) return Items.REDSTONE_TORCH.getDefaultInstance();
        if (state == HAS_LANTERN1) return Items.LANTERN.getDefaultInstance();
        if (state == HAS_LANTERN2) return Items.SOUL_LANTERN.getDefaultInstance();
        if (state == HAS_TORCH3) return Items.SOUL_TORCH.getDefaultInstance();
        if (state == HAS_TORCH1) return Items.TORCH.getDefaultInstance();
        return ItemStack.EMPTY;
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    @Override
    public BlockState updateShape(BlockState blockState1, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos1, BlockPos blockPos2)
    {
        if (blockState1.getValue(BlockStateProperties.WATERLOGGED))
        {
            levelAccessor.scheduleTick(blockPos1, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        // above is copied. skpping part with connections to the side.
        return blockState1;
    }

    //-------------------------------------------------------------------------

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos blockPos, Block other, BlockPos otherPos, boolean p_60514_)
    {
        if (otherPos.getY() == blockPos.getY() - 1 && otherPos.getX() == blockPos.getX() && otherPos.getZ() == blockPos.getZ())
        {
            if (! level.getBlockState(otherPos).is(BlockTags.SLABS))
            {
                level.destroyBlock(blockPos, true);
            }
            return;
        }
        ///////////////
        // main part, joined both otherPos.getY() == blockPos.getY() and otherPos.getY() == blockPos.getY()-1 cases together
        boolean isOurFenceTagged = this.defaultBlockState().is(Tags.Blocks.FENCES_WOODEN);  // undecided. let's avoid bugs and unneeded instanceof checks.
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        pos.set(blockPos);
        BlockState stateNew = state; BlockState stateSideBelow, stateSide;
        boolean changed = false;
        //// fences above
        //// also solid blocks above and below
        // N
        Direction direction = Direction.NORTH;
        Property<Integer> dirProperty = FenceVersion3Block.NORTH_COMPRESSED;
        stateSide = level.getBlockState(pos.move(direction));
        stateSideBelow = level.getBlockState(pos.move(Direction.DOWN));
        boolean horizontal = stateSide.getBlock() instanceof FenceVersion3Block;
        boolean upward = stateSide.is(Tags.Blocks.FENCES_WOODEN) && ! horizontal;
        boolean downward = ! horizontal && stateSideBelow.is(Tags.Blocks.FENCES_WOODEN) && ! (isOurFenceTagged && (stateSideBelow.getBlock() instanceof FenceVersion3Block)); // last part not needed if ours isn't tagged and probably isn't.
        boolean sturdy = ! horizontal && stateSideBelow.isFaceSturdy(level, pos, direction.getOpposite()) && stateSide.isFaceSturdy(level, pos.move(Direction.UP), direction.getOpposite());
        pos.set(blockPos); // move up in the above row may or may not happen
        int newValueCompressed = state.getValue(dirProperty);
        if (upward) newValueCompressed = newValueCompressed & ~4 | 2; else newValueCompressed = newValueCompressed & ~2;
        if (downward) newValueCompressed = newValueCompressed & ~4 | 1; else newValueCompressed = newValueCompressed & ~1;
        if (horizontal || sturdy) newValueCompressed = 4; else newValueCompressed = newValueCompressed & ~4;
        changed |= state.getValue(dirProperty) != newValueCompressed;
        stateNew = stateNew.setValue(dirProperty, newValueCompressed);
        // E
        direction = Direction.EAST;
        dirProperty = FenceVersion3Block.EAST_COMPRESSED;
        stateSide = level.getBlockState(pos.move(direction));
        stateSideBelow = level.getBlockState(pos.move(Direction.DOWN));
        horizontal = stateSide.getBlock() instanceof FenceVersion3Block;
        upward = stateSide.is(Tags.Blocks.FENCES_WOODEN) && ! horizontal;
        downward = ! horizontal && stateSideBelow.is(Tags.Blocks.FENCES_WOODEN) && ! (isOurFenceTagged && (stateSideBelow.getBlock() instanceof FenceVersion3Block));
        sturdy = ! horizontal && stateSideBelow.isFaceSturdy(level, pos, direction.getOpposite()) && stateSide.isFaceSturdy(level, pos.move(Direction.UP), direction.getOpposite());
        pos.set(blockPos);
        newValueCompressed = state.getValue(dirProperty);
        if (upward) newValueCompressed = newValueCompressed & ~4 | 2; else newValueCompressed = newValueCompressed & ~2;
        if (downward) newValueCompressed = newValueCompressed & ~4 | 1; else newValueCompressed = newValueCompressed & ~1;
        if (horizontal || sturdy) newValueCompressed = 4; else newValueCompressed = newValueCompressed & ~4;
        changed |= state.getValue(dirProperty) != newValueCompressed;
        stateNew = stateNew.setValue(dirProperty, newValueCompressed);
        // W
        direction = Direction.WEST;
        dirProperty = FenceVersion3Block.WEST_COMPRESSED;
        stateSide = level.getBlockState(pos.move(direction));
        stateSideBelow = level.getBlockState(pos.move(Direction.DOWN));
        horizontal = stateSide.getBlock() instanceof FenceVersion3Block;
        upward = stateSide.is(Tags.Blocks.FENCES_WOODEN) && ! horizontal;
        downward = ! horizontal && stateSideBelow.is(Tags.Blocks.FENCES_WOODEN) && ! (isOurFenceTagged && (stateSideBelow.getBlock() instanceof FenceVersion3Block));
        sturdy = ! horizontal && stateSideBelow.isFaceSturdy(level, pos, direction.getOpposite()) && stateSide.isFaceSturdy(level, pos.move(Direction.UP), direction.getOpposite());
        pos.set(blockPos);
        newValueCompressed = state.getValue(dirProperty);
        if (upward) newValueCompressed = newValueCompressed & ~4 | 2; else newValueCompressed = newValueCompressed & ~2;
        if (downward) newValueCompressed = newValueCompressed & ~4 | 1; else newValueCompressed = newValueCompressed & ~1;
        if (horizontal || sturdy) newValueCompressed = 4; else newValueCompressed = newValueCompressed & ~4;
        changed |= state.getValue(dirProperty) != newValueCompressed;
        stateNew = stateNew.setValue(dirProperty, newValueCompressed);
        // S
        direction = Direction.SOUTH;
        dirProperty = FenceVersion3Block.SOUTH_COMPRESSED;
        stateSide = level.getBlockState(pos.move(direction));
        stateSideBelow = level.getBlockState(pos.move(Direction.DOWN));
        horizontal = stateSide.getBlock() instanceof FenceVersion3Block;
        upward = stateSide.is(Tags.Blocks.FENCES_WOODEN) && ! horizontal;
        downward = ! horizontal && stateSideBelow.is(Tags.Blocks.FENCES_WOODEN) && ! (isOurFenceTagged && (stateSideBelow.getBlock() instanceof FenceVersion3Block));
        sturdy = ! horizontal && stateSideBelow.isFaceSturdy(level, pos, direction.getOpposite()) && stateSide.isFaceSturdy(level, pos.move(Direction.UP), direction.getOpposite());
        pos.set(blockPos);
        newValueCompressed = state.getValue(dirProperty);
        if (upward) newValueCompressed = newValueCompressed & ~4 | 2; else newValueCompressed = newValueCompressed & ~2;
        if (downward) newValueCompressed = newValueCompressed & ~4 | 1; else newValueCompressed = newValueCompressed & ~1;
        if (horizontal || sturdy) newValueCompressed = 4; else newValueCompressed = newValueCompressed & ~4;
        changed |= state.getValue(dirProperty) != newValueCompressed;
        stateNew = stateNew.setValue(dirProperty, newValueCompressed);
        // compare
        if (changed)
        {
            level.setBlockAndUpdate(blockPos, stateNew);
        }
        //System.out.println("neighborChanged,  " + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ());
        super.neighborChanged(state, level, blockPos, other, otherPos, p_60514_); // debug packet. probably don't need it.
    }

    public static void onSlabNeighborChanged(BlockState state, Level level, BlockPos blockPos, Block other, BlockPos otherPos, boolean movedByPiston)
    {
        // just call the above thing
        if (otherPos.getY() == blockPos.getY())
        {
            level.getBlockState(blockPos.above()).handleNeighborChanged(level, blockPos.above(), other, otherPos, movedByPiston);
        }
        //System.out.println("onSlabNeighborChanged,  " + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        int variant = blockState.getValue(TOP_VARIANT);
        if (variant != 0)
        {
            return super.useItemOn(stack, blockState, level, blockPos, player, hand, hitResult);
        }
        if (stack.is(Items.TORCH)) variant = 1;
        else if (stack.is(Items.LANTERN)) variant = 2;
        else if (stack.is(Items.SOUL_LANTERN)) variant = 3;
        else if (stack.is(Items.REDSTONE_TORCH)) variant = 4;
        else if (stack.is(Items.SOUL_TORCH)) variant = 5;
        if (variant == 0) // new variant
        {
            return super.useItemOn(stack, blockState, level, blockPos, player, hand, hitResult);
        }
        if (! player.isCreative())
        {
            stack.shrink(1);
        }
        if (! level.isClientSide())
        {
            level.setBlockAndUpdate(blockPos, blockState.setValue(TOP_VARIANT, variant));
            return ItemInteractionResult.CONSUME;
        }
        else
        {
            return ItemInteractionResult.SUCCESS;
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        if (state.getValue(TOP_VARIANT) == 0)
        {
            //this.playerWillDestroy(level, pos, state, player);
            // earlier we'd destroy the slab here, but now they are separate
            //return true;
            return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        }
        else
        {
            if (! level.isClientSide())
            {
                ItemStack drop = stateToItem(state.getValue(TOP_VARIANT));
                Block.popResource(level, pos, drop);
            }
            level.setBlock(pos, state.setValue(TOP_VARIANT, 0), level.isClientSide ? 11 : 3);
            return false;
        }
    }

    private static int getLightLevel(BlockState state)
    {
        int variant = state.getValue(TOP_VARIANT);
        if (variant == HAS_TORCH1) return 14;
        if (variant == HAS_LANTERN1) return 15;
        if (variant == HAS_LANTERN2) return 10;
        if (variant == HAS_TORCH2) return 7;
        if (variant == HAS_TORCH3) return 10;
        return 0;
    }

    //-------------------------------------------------------------------------

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder)
    {
        stateBuilder.add(NORTH_COMPRESSED, EAST_COMPRESSED, SOUTH_COMPRESSED, WEST_COMPRESSED);
        stateBuilder.add(TOP_VARIANT, BlockStateProperties.WATERLOGGED);
    }

    private static final int HAS_TORCH1 = 1;
    private static final int HAS_LANTERN1 = 2;
    private static final int HAS_LANTERN2 = 3;
    private static final int HAS_TORCH2 = 4;
    private static final int HAS_TORCH3 = 5;
    public static final IntegerProperty TOP_VARIANT = IntegerProperty.create("top_variant", 0, 5);
    public static final IntegerProperty NORTH_COMPRESSED = IntegerProperty.create("north_c", 0, 4);
    public static final IntegerProperty SOUTH_COMPRESSED = IntegerProperty.create("south_c", 0, 4);
    public static final IntegerProperty EAST_COMPRESSED = IntegerProperty.create("east_c", 0, 4);
    public static final IntegerProperty WEST_COMPRESSED = IntegerProperty.create("west_c", 0, 4);

    //-------------------------------------------------------------------------
    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) { return !(Boolean)state.getValue(BlockStateProperties.WATERLOGGED); }
    @Override
    protected FluidState getFluidState(BlockState state) { return (Boolean)state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
    //-------------------------------------------------------------------------

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
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
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
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

    //-----------------------------------------------------------------------------------------

    @Override
    public void animateTick(BlockState blockState, Level p_222594_, BlockPos p_222595_, RandomSource p_222596_)
    {
        int variant = blockState.getValue(TOP_VARIANT);
        if (variant == HAS_TORCH1 || variant == HAS_TORCH3 || variant == HAS_TORCH2)
        {
            ParticleOptions flame = variant == HAS_TORCH2 ? DustParticleOptions.REDSTONE : variant == HAS_TORCH1 ? ParticleTypes.FLAME : ParticleTypes.SOUL_FIRE_FLAME;
            double d0 = (double) p_222595_.getX() + 0.5D;
            double d1 = (double) p_222595_.getY() + 1.1D;
            double d2 = (double) p_222595_.getZ() + 0.5D;
            p_222594_.addParticle(flame, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            if (variant != HAS_TORCH2)
            {
                p_222594_.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    //-------------------------------------------------------------------------

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction)
    {
        if (blockState.getValue(TOP_VARIANT) != HAS_TORCH2)
        {
            return 0;
        }
        return 8;
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction)
    {
        return 0;
    }

    @Override
    public boolean isSignalSource(BlockState blockState)
    {
        return blockState.getValue(TOP_VARIANT) == HAS_TORCH2;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction)
    {
        return false;
    }
}

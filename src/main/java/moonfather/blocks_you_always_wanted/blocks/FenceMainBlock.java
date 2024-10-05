package moonfather.blocks_you_always_wanted.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
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

public class FenceMainBlock extends FenceBlock
{
    public FenceMainBlock(Block original)
    {
        super(Properties.ofFullCopy(original).lightLevel(FenceMainBlock::getLightLevel));
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(NORTH_DOWN, Boolean.FALSE).setValue(EAST_DOWN, Boolean.FALSE).setValue(SOUTH_DOWN, Boolean.FALSE).setValue(WEST_DOWN, Boolean.FALSE).setValue(NORTH_UP, Boolean.FALSE).setValue(EAST_UP, Boolean.FALSE).setValue(SOUTH_UP, Boolean.FALSE).setValue(WEST_UP, Boolean.FALSE).setValue(WATERLOGGED, Boolean.FALSE).setValue(TOP_VARIANT, 0));
        this.original = (FenceBlock) original;
        if (collisionShapes == null)
        {
            collisionShapes = new VoxelShape[16*16*16*4]; // 16s are connections, 4 is top addon
            interactionShapes = new VoxelShape[16*16*16*4];
            makeShapes();
        }
    }
    private final FenceBlock original;

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
        result += (top_addon == HAS_LANTERN1 || top_addon == HAS_LANTERN2) ? 4096 : 0;
        result += (top_addon == HAS_TORCH1 || top_addon == HAS_TORCH2 || top_addon == HAS_TORCH3) ? 8192 : 0;
        return result;
    }

    private static void makeShapes()
    {
        for (int i = 0; i < 16*16*16*4; i++) // 16s are connections, 4 is top addon
        {
            interactionShapes[i] = SHAPE_POST;
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
            if ((i & 4096) > 0) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_ON_TOP_LAMP);
            if ((i & 8192) > 0) interactionShapes[i] = Shapes.or(interactionShapes[i], SHAPE_ON_TOP_TORCH); // could have fit one more shape into these two bits. don't have one now.
        }
        for (int i = 0; i < 16*16*16*4; i++)  // 16s are connections, 4 is top addon
        {
            collisionShapes[i] = SHAPE_POST_COLL;
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
        if (blockState1.getValue(WATERLOGGED))
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
            if (! (level.getBlockState(otherPos).getBlock() instanceof FenceBearingSlabBlock))
            {
                level.destroyBlock(blockPos, true);
            }
            return;
        }
        if (otherPos.getY() == blockPos.getY())
        {
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            pos.set(blockPos);
            BlockState stateNew = state; BlockState stateSideBelow, stateSide;
            boolean changed = false;
            //// fences above
            // N
            stateSide = level.getBlockState(pos.move(Direction.NORTH));
            boolean levelValue = stateSide.is(Tags.Blocks.FENCES_WOODEN) && ! (stateSide.getBlock() instanceof FenceMainBlock);
            changed |= state.getValue(FenceMainBlock.NORTH_UP) != levelValue;
            stateNew = stateNew.setValue(FenceMainBlock.NORTH_UP, levelValue);
            // E
            stateSide = level.getBlockState(pos.move(Direction.SOUTH).move(Direction.EAST));
            levelValue = stateSide.is(Tags.Blocks.FENCES_WOODEN) && ! (stateSide.getBlock() instanceof FenceMainBlock);
            changed |= state.getValue(FenceMainBlock.EAST_UP) != levelValue;
            stateNew = stateNew.setValue(FenceMainBlock.EAST_UP, levelValue);
            // W
            stateSide = level.getBlockState(pos.move(Direction.WEST).move(Direction.WEST));
            levelValue = stateSide.is(Tags.Blocks.FENCES_WOODEN) && ! (stateSide.getBlock() instanceof FenceMainBlock);
            changed |= state.getValue(FenceMainBlock.WEST_UP) != levelValue;
            stateNew = stateNew.setValue(FenceMainBlock.WEST_UP, levelValue);
            // S
            stateSide = level.getBlockState(pos.move(Direction.EAST).move(Direction.SOUTH));
            levelValue = stateSide.is(Tags.Blocks.FENCES_WOODEN) && ! (stateSide.getBlock() instanceof FenceMainBlock);
            changed |= state.getValue(FenceMainBlock.SOUTH_UP) != levelValue;
            stateNew = stateNew.setValue(FenceMainBlock.SOUTH_UP, levelValue);
            //// solid blocks above
            // N
            stateSide = level.getBlockState(pos.move(Direction.NORTH).move(Direction.NORTH));
            stateSideBelow = level.getBlockState(pos.move(Direction.DOWN));
            levelValue = (stateSide.getBlock() instanceof FenceMainBlock) || stateSideBelow.isFaceSturdy(level, pos, Direction.SOUTH) && stateSide.isFaceSturdy(level, pos.move(Direction.UP), Direction.SOUTH);
            changed |= state.getValue(FenceBlock.NORTH) != levelValue;
            stateNew = stateNew.setValue(FenceBlock.NORTH, levelValue);
            pos.setY(blockPos.getY()); // move up in the above row may or may not happen
            // E
            stateSide = level.getBlockState(pos.move(Direction.SOUTH).move(Direction.EAST));
            stateSideBelow = level.getBlockState(pos.move(Direction.DOWN));
            levelValue = (stateSide.getBlock() instanceof FenceMainBlock) || stateSideBelow.isFaceSturdy(level, pos, Direction.SOUTH) && stateSide.isFaceSturdy(level, pos.move(Direction.UP), Direction.SOUTH);
            changed |= state.getValue(FenceBlock.EAST) != levelValue;
            stateNew = stateNew.setValue(FenceBlock.EAST, levelValue);
            pos.setY(blockPos.getY());
            // W
            stateSide = level.getBlockState(pos.move(Direction.WEST).move(Direction.WEST));
            stateSideBelow = level.getBlockState(pos.move(Direction.DOWN));
            levelValue = (stateSide.getBlock() instanceof FenceMainBlock) || stateSideBelow.isFaceSturdy(level, pos, Direction.SOUTH) && stateSide.isFaceSturdy(level, pos.move(Direction.UP), Direction.SOUTH);
            changed |= state.getValue(FenceBlock.WEST) != levelValue;
            stateNew = stateNew.setValue(FenceBlock.WEST, levelValue);
            pos.setY(blockPos.getY());
            // E
            stateSide = level.getBlockState(pos.move(Direction.EAST).move(Direction.SOUTH));
            stateSideBelow = level.getBlockState(pos.move(Direction.DOWN));
            levelValue = (stateSide.getBlock() instanceof FenceMainBlock) || stateSideBelow.isFaceSturdy(level, pos, Direction.SOUTH) && stateSide.isFaceSturdy(level, pos.move(Direction.UP), Direction.SOUTH);
            changed |= state.getValue(FenceBlock.SOUTH) != levelValue;
            stateNew = stateNew.setValue(FenceBlock.SOUTH, levelValue);
            pos.setY(blockPos.getY());
            // compare
            if (changed)
            {
                level.setBlockAndUpdate(blockPos, stateNew);
            }
        }
        if (otherPos.getY() == blockPos.getY() - 1)
        {
            // down and to the side. invoked by slab.
            boolean changed = false;
            BlockPos.MutableBlockPos pos = blockPos.mutable();
            pos.move(Direction.DOWN);
            // E
            Direction direction = Direction.EAST;
            BlockState adjacentBelow = level.getBlockState(pos.move(direction));
            BlockState adjacentSameY = level.getBlockState(pos.move(Direction.UP));
            Block adjacent = adjacentBelow.getBlock();
            boolean levelValue = adjacentSameY.getBlock() instanceof FenceMainBlock || (adjacentBelow.isFaceSturdy(level, pos.move(Direction.DOWN), direction.getOpposite()) && adjacentSameY.isFaceSturdy(level, pos.move(Direction.UP), direction.getOpposite()));
            boolean levelValueDn = adjacent instanceof FenceBlock && ! (adjacent instanceof FenceMainBlock);
            changed |= state.getValue(EAST) != levelValue;
            changed |= state.getValue(EAST_DOWN) != levelValueDn;
            state = state.setValue(EAST, levelValue);
            state = state.setValue(EAST_DOWN, levelValueDn);
            pos.set(blockPos); pos.move(Direction.DOWN);
            // N
            direction = Direction.NORTH;
            adjacentBelow = level.getBlockState(pos.move(direction));
            adjacentSameY = level.getBlockState(pos.move(Direction.UP));
            adjacent = adjacentBelow.getBlock();
            levelValue = adjacentSameY.getBlock() instanceof FenceMainBlock || (adjacentBelow.isFaceSturdy(level, pos.move(Direction.DOWN), direction.getOpposite()) && adjacentSameY.isFaceSturdy(level, pos.move(Direction.UP), direction.getOpposite()));
            levelValueDn = adjacent instanceof FenceBlock && ! (adjacent instanceof FenceMainBlock);
            changed |= state.getValue(NORTH) != levelValue;
            changed |= state.getValue(NORTH_DOWN) != levelValueDn;
            state = state.setValue(NORTH, levelValue);
            state = state.setValue(NORTH_DOWN, levelValueDn);
            pos.set(blockPos); pos.move(Direction.DOWN);
            // S
            direction = Direction.SOUTH;
            adjacentBelow = level.getBlockState(pos.move(direction));
            adjacentSameY = level.getBlockState(pos.move(Direction.UP));
            adjacent = adjacentBelow.getBlock();
            levelValue = adjacentSameY.getBlock() instanceof FenceMainBlock || (adjacentBelow.isFaceSturdy(level, pos.move(Direction.DOWN), direction.getOpposite()) && adjacentSameY.isFaceSturdy(level, pos.move(Direction.UP), direction.getOpposite()));
            levelValueDn = adjacent instanceof FenceBlock && ! (adjacent instanceof FenceMainBlock);
            changed |= state.getValue(SOUTH) != levelValue;
            changed |= state.getValue(SOUTH_DOWN) != levelValueDn;
            state = state.setValue(SOUTH, levelValue);
            state = state.setValue(SOUTH_DOWN, levelValueDn);
            pos.set(blockPos); pos.move(Direction.DOWN);
            // W
            direction = Direction.WEST;
            adjacentBelow = level.getBlockState(pos.move(direction));
            adjacentSameY = level.getBlockState(pos.move(Direction.UP));
            adjacent = adjacentBelow.getBlock();
            levelValue = adjacentSameY.getBlock() instanceof FenceMainBlock || (adjacentBelow.isFaceSturdy(level, pos.move(Direction.DOWN), direction.getOpposite()) && adjacentSameY.isFaceSturdy(level, pos.move(Direction.UP), direction.getOpposite()));
            levelValueDn = adjacent instanceof FenceBlock && ! (adjacent instanceof FenceMainBlock);
            changed |= state.getValue(WEST) != levelValue;
            changed |= state.getValue(WEST_DOWN) != levelValueDn;
            state = state.setValue(WEST, levelValue);
            state = state.setValue(WEST_DOWN, levelValueDn);
            pos.set(blockPos); pos.move(Direction.DOWN);
            /////////////
            if (changed)
            {
                level.setBlockAndUpdate(blockPos, state);
            }
        }
        super.neighborChanged(state, level, blockPos, other, otherPos, p_60514_); // debug packet. probably don't need it.
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

    //-------------------------------------------------------------------------

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53334_)
    {
        p_53334_.add(NORTH, EAST, WEST, SOUTH);
        p_53334_.add(NORTH_UP, EAST_UP, WEST_UP, SOUTH_UP);
        p_53334_.add(NORTH_DOWN, EAST_DOWN, WEST_DOWN, SOUTH_DOWN);
        p_53334_.add(TOP_VARIANT, WATERLOGGED);
    }

    public static final BooleanProperty NORTH_UP = BooleanProperty.create("north_up");
    public static final BooleanProperty EAST_UP = BooleanProperty.create("east_up");
    public static final BooleanProperty SOUTH_UP = BooleanProperty.create("south_up");
    public static final BooleanProperty WEST_UP = BooleanProperty.create("west_up");
    public static final BooleanProperty NORTH_DOWN = BooleanProperty.create("north_dn");
    public static final BooleanProperty EAST_DOWN = BooleanProperty.create("east_dn");
    public static final BooleanProperty SOUTH_DOWN = BooleanProperty.create("south_dn");
    public static final BooleanProperty WEST_DOWN = BooleanProperty.create("west_dn");
    public static final int HAS_TORCH1 = 1;
    public static final int HAS_LANTERN1 = 2;
    public static final int HAS_LANTERN2 = 3;
    public static final int HAS_TORCH2 = 4;
    public static final int HAS_TORCH3 = 5;
    public static final IntegerProperty TOP_VARIANT = IntegerProperty.create("top_variant", 0, 5);

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

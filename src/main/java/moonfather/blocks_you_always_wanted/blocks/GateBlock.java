package moonfather.blocks_you_always_wanted.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moonfather.blocks_you_always_wanted.Constants;
import moonfather.blocks_you_always_wanted.initialization.RegistrationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GateBlock extends HorizontalDirectionalBlock
{
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty IN_WALL = BlockStateProperties.IN_WALL;

    public static class ShapeSet
    {
        public static final VoxelShape EMPTY = Shapes.empty();
        public static final VoxelShape Z_SHAPE = Block.box(-5.0D, 3.0D, 6.0D, 21.0D, 26.0D, 10.0D);
        public static final VoxelShape X_SHAPE = Block.box(6.0D, 3.0D, -5.0D, 10.0D, 26.0D, 21.0D);
        public static final VoxelShape Z_SHAPE_NARROW = Block.box(-4.0D, 3.0D, 6.0D, 20.0D, 26.0D, 10.0D);
        public static final VoxelShape X_SHAPE_NARROW = Block.box(6.0D, 3.0D, -4.0D, 10.0D, 26.0D, 20.0D);
        public static final VoxelShape Z_COLLISION_SHAPE = Block.box(-4.0D, 2.0D, 6.0D, 20.0D, 31.5D, 10.0D);
        public static final VoxelShape X_COLLISION_SHAPE = Block.box(6.0D, 2.0D, -4.0D, 10.0D, 31.5D, 20.0D);
        public static final VoxelShape Z_SUPPORT_SHAPE = EMPTY;
        public static final VoxelShape X_SUPPORT_SHAPE = EMPTY;
        public static final VoxelShape Z_OCCLUSION_SHAPE = Shapes.or(Block.box(-5.0D, 3.0D, 7.0D, -3.0D, 16.0D, 9.0D), Block.box(19.0D, 3.0D, 7.0D, 21.0D, 16.0D, 9.0D));
        public static final VoxelShape X_OCCLUSION_SHAPE = Shapes.or(Block.box(7.0D, 5.0D, -5.0D, 9.0D, 16.0D, -3.0D), Block.box(7.0D, 3.0D, 19.0D, 9.0D, 16.0D, 21.0D));
        public static final VoxelShape Z_OCCLUSION_SHAPE_NARROW = Shapes.or(Block.box(-4.0D, 3.0D, 7.0D, -2.0D, 16.0D, 9.0D), Block.box(18.0D, 3.0D, 7.0D, 20.0D, 16.0D, 9.0D));
        public static final VoxelShape X_OCCLUSION_SHAPE_NARROW = Shapes.or(Block.box(7.0D, 5.0D, -4.0D, 9.0D, 16.0D, -2.0D), Block.box(7.0D, 3.0D, 18.0D, 9.0D, 16.0D, 20.0D));
    }
    private final net.minecraft.sounds.SoundEvent openSound;
    private final net.minecraft.sounds.SoundEvent closeSound;
    private Block original = null;

    public GateBlock(Block original, WoodType woodType)
    {
        this(Properties.ofFullCopy(original).sound(woodType.soundType()), woodType.fenceGateOpen(), woodType.fenceGateClose());
        this.original = original;
    }

    public GateBlock(Properties properties, SoundEvent openSound, SoundEvent closeSound)
    {
        super(properties);
        this.openSound = openSound != null ? openSound : WoodType.OAK.fenceGateOpen();
        this.closeSound = closeSound != null ? closeSound : WoodType.OAK.fenceGateClose();
        this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, Boolean.FALSE).setValue(POWERED, Boolean.FALSE).setValue(IN_WALL, Boolean.FALSE));
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
    public VoxelShape getCollisionShape(BlockState p_53396_, BlockGetter p_53397_, BlockPos p_53398_, CollisionContext p_53399_) {
        if (p_53396_.getValue(OPEN)) {
            return ShapeSet.EMPTY;
        } else {
            return p_53396_.getValue(FACING).getAxis() == Direction.Axis.Z ? ShapeSet.Z_COLLISION_SHAPE : ShapeSet.X_COLLISION_SHAPE;
        }
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
    public boolean isPathfindable(BlockState p_53360_, PathComputationType p_53363_) {
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

    //////////////////////////////////////////////////////

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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        // normal gate to raised gate conversion.
        if (hand.equals(InteractionHand.MAIN_HAND) && (player.getMainHandItem().is(Items.RAIL) || player.getMainHandItem().is(Items.POWERED_RAIL)))
        {
            BlockPos below = blockPos.below();
            if (! level.getBlockState(below).isFaceSturdy(level, below, Direction.UP))
            {
                return ItemInteractionResult.FAIL;
            }
            blockState = GateBlock.toRaisedGate(blockState.getBlock()).withPropertiesOf(blockState).setValue(GateRaisedBlock.BLOCK_BELOW, GateBlock.railToStateIndex(player.getMainHandItem().getItem()));
            level.setBlock(blockPos, blockState, 11);
            level.playSound(player, blockPos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
            if (! player.isCreative())
            {
                player.getMainHandItem().shrink(1);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, blockState, level, blockPos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult)
    {
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



    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block p_53375_, BlockPos otherPos, boolean movedByPiston)
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
            // something piston-pushed next to the gate?
            if (otherPos.getY() == blockPos.getY())
            {
                boolean onSide = ((blockPos.getX() == otherPos.getX()) && blockState.getValue(FACING).getAxis().equals(Direction.Axis.X))
                        || ((blockPos.getZ() == otherPos.getZ()) && blockState.getValue(FACING).getAxis().equals(Direction.Axis.Z));
                if (onSide) // don't check front of gate
                {
                    BlockState other = level.getBlockState(otherPos);
                    if (other.is(BlockTags.SLABS) && other.getValue(SlabBlock.TYPE).equals(SlabType.BOTTOM))
                    {
                        // allow lower slabs
                        blockState = GateBlock.toRaisedGate(blockState.getBlock()).withPropertiesOf(blockState);
                        level.setBlock(blockPos, blockState, 11);
                    }
                    else if (! other.isAir() && ! other.is(BlockTags.FENCES) && ! other.is(BlockTags.WALLS) && ! other.is(Constants.BlockTags.ALLOWED_NEXT_TO_GATES))
                    {
                        // destroy gate if block not supported
                        level.destroyBlock(blockPos, true);
                    }
                }
            }
            // walls. not trivial so moving to a separate method.
            this.updateWallProperty(blockState, level, blockPos, p_53375_, otherPos, movedByPiston);
        }
    }



    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState p_60569_, boolean p_60570_)
    {
        super.onPlace(blockState, level, blockPos, p_60569_, p_60570_);
        BlockState pt2 = RegistrationManager.GATE_TECHNICAL.get().withPropertiesOf(blockState);
        level.setBlockAndUpdate(blockPos.above(), pt2);
        this.checkForSlabs(blockState, level, blockPos);
    }


    private void checkForSlabs(BlockState blockState, Level level, BlockPos blockPos)
    {
        if (level.isClientSide())
        {
            return;
        }
        BlockPos otherPos = blockPos.relative(blockState.getValue(FACING).getClockWise());
        BlockState other = level.getBlockState(otherPos);
        if (other.is(BlockTags.SLABS))
        {
            this.neighborChanged(blockState, level, blockPos, other.getBlock(), otherPos, true);
        }
        otherPos = blockPos.relative(blockState.getValue(FACING).getCounterClockWise());
        other = level.getBlockState(otherPos);
        if (other.is(BlockTags.SLABS))
        {
            this.neighborChanged(blockState, level, blockPos, other.getBlock(), otherPos, true);
        }
    }



    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder)
    {
        stateBuilder.add(FACING, OPEN, POWERED, IN_WALL);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    /////////////////////////////////////////////////////////////////////////////////

    public static Block toRaisedGate(Block normalGate)
    {
        ResourceLocation gateRL = BuiltInRegistries.BLOCK.getKey(normalGate);
        gateRL = ResourceLocation.fromNamespaceAndPath(gateRL.getNamespace(), gateRL.getPath().replace("gate_main", "gate_spec"));
        return BuiltInRegistries.BLOCK.get(gateRL);
    }

    public static Block fromRaisedGate(Block raisedGate)
    {
        ResourceLocation gateRL = BuiltInRegistries.BLOCK.getKey(raisedGate);
        gateRL = ResourceLocation.fromNamespaceAndPath(gateRL.getNamespace(), gateRL.getPath().replace("gate_spec", "gate_main"));
        return BuiltInRegistries.BLOCK.get(gateRL);
    }

    public static int railToStateIndex(Item rail)
    {
        if (rail.equals(Items.POWERED_RAIL)) return GateRaisedBlock.ON_POWERED_RAIL;
        if (rail.equals(Items.RAIL)) return GateRaisedBlock.ON_REGULAR_RAIL;
        return 0;
    }

    public static int blockToStateIndex(Block block)
    {
        if (block.equals(Blocks.SMOOTH_STONE_SLAB)) return GateRaisedBlock.ON_STONE_SLAB;
        if (block instanceof SlabBlock) return GateRaisedBlock.ON_WOODEN_SLAB;
        if (block.equals(Blocks.RAIL)) return GateRaisedBlock.ON_REGULAR_RAIL;
        if (block.equals(Blocks.POWERED_RAIL)) return GateRaisedBlock.ON_POWERED_RAIL;
        return 0;
    }
    public static int blockToStateIndex(Block block1, Block block2)
    {
        if (block1 != null) return blockToStateIndex(block1);
        return blockToStateIndex(block2);
    }

    /////////////////////////////////////////////////////

    protected MapCodec<? extends HorizontalDirectionalBlock> codec() { return CODEC; }
    private static final MapCodec<GateBlock> CODEC = RecordCodecBuilder.mapCodec((p_308823_) -> {
        return p_308823_.group(
            propertiesCodec(),
            SoundEvent.DIRECT_CODEC.optionalFieldOf("openSound").forGetter((gate) -> Optional.of(gate.openSound)),
            SoundEvent.DIRECT_CODEC.optionalFieldOf("closeSound").forGetter((gate) -> Optional.of(gate.closeSound))
        ).apply(p_308823_, (prop, sound1, sound2) -> new GateBlock(prop, sound1.orElse(null), sound2.orElse(null)));
    });

    ///////////    i don't even need the crap above in 1.21.1   //////////////////

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

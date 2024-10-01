package moonfather.blocks_you_always_wanted.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FenceTechnicalBlock extends Block
{
    public FenceTechnicalBlock()
    {
        super(BlockBehaviour.Properties.of().strength(1.2F, 3.0F).ignitedByLava().sound(SoundType.WOOD).lightLevel(FenceTechnicalBlock::getLightLevel));
        this.registerDefaultState(this.stateDefinition.any().setValue(TOP_VARIANT, 0).setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE));
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

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
    {
        int variant = state.getValue(TOP_VARIANT);
        if (variant == HAS_TORCH1 || variant == HAS_TORCH2 || variant == HAS_TORCH3)
        {
            return SHAPE_WITH_TORCH;
        }
        if (variant == HAS_LANTERN1 || variant == HAS_LANTERN2)
        {
            return SHAPE_WITH_LAMP;
        }
        return SHAPE_POST;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter p_60573_, BlockPos p_60574_, CollisionContext p_60575_)
    {
        int variant = state.getValue(TOP_VARIANT);
        if (variant == HAS_LANTERN1 || variant == HAS_LANTERN2)
        {
            return SHAPE_WITH_LAMP;
        }
        return SHAPE_POST;
    }

    private static final VoxelShape SHAPE_POST = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 8.0D, 10.0D);
    private static final VoxelShape SHAPE_TORCH = Block.box(7.0D, 8.0D, 7.0D, 9.0D,  16.0D, 9.0D);
    private static final VoxelShape SHAPE_LAMP = Block.box(5.0D, 8.0D, 5.0D, 11.0D,  16.0D, 11.0D);
    private static final VoxelShape SHAPE_WITH_TORCH = Shapes.or(SHAPE_TORCH, SHAPE_POST);
    private static final VoxelShape SHAPE_WITH_LAMP = Shapes.or(SHAPE_LAMP, SHAPE_POST);


    /////////////////////////////////////////////////////



    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        ItemStack result = stateToItem(state.getValue(TOP_VARIANT));
        if (! result.isEmpty())
        {
            return result;
        }
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return belowState.getCloneItemStack(target, level, below, player);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
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
    ////////////////////////////////////////////////

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(TOP_VARIANT, BlockStateProperties.WATERLOGGED);
    }

    public static final int HAS_TORCH1 = 1;
    public static final int HAS_LANTERN1 = 2;
    public static final int HAS_LANTERN2 = 3;
    public static final int HAS_TORCH2 = 4;
    public static final int HAS_TORCH3 = 5;
    public static final IntegerProperty TOP_VARIANT = IntegerProperty.create("top_variant", 0, 5);

    //////////////////////////////////////////////


    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        if (state.getValue(TOP_VARIANT) == 0)
        {
            this.playerWillDestroy(level, pos, state, player);
            level.destroyBlock(pos.below(), true, player);
            level.setBlock(pos, fluid.createLegacyBlock(), level.isClientSide ? 11 : 3);
            return true;
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

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder)
    {
        List<ItemStack> result = new ArrayList<>();
        ItemStack drop = stateToItem(blockState.getValue(TOP_VARIANT));
        if (! drop.isEmpty())
        {
            result.add(drop);
        }
        return result;
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block p_60512_, BlockPos other, boolean p_60514_)
    {
        if (other.getY() == blockPos.getY() - 1)
        {
            if (! (level.getBlockState(other).getBlock() instanceof FenceOnASlabBlock))
            {
                level.destroyBlock(blockPos, true);
                return;
            }
        }

        if (other.getY() == blockPos.getY())
        {
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            pos.set(blockPos);
            BlockState stateBelow = level.getBlockState(pos.move(Direction.DOWN));
            BlockState stateSideBelow;
            //// fences above
            // N
            BlockState stateSide = level.getBlockState(pos.move(Direction.UP).move(Direction.NORTH));
            boolean levelValue = stateSide.is(Tags.Blocks.FENCES_WOODEN) && ! (stateSide.getBlock() instanceof FenceOnASlabBlock);
            stateBelow = stateBelow.setValue(FenceOnASlabBlock.NORTH_UP, levelValue);
            // E
            stateSide = level.getBlockState(pos.move(Direction.SOUTH).move(Direction.EAST));
            levelValue = stateSide.is(Tags.Blocks.FENCES_WOODEN) && ! (stateSide.getBlock() instanceof FenceOnASlabBlock);
            stateBelow = stateBelow.setValue(FenceOnASlabBlock.EAST_UP, levelValue);
            // W
            stateSide = level.getBlockState(pos.move(Direction.WEST).move(Direction.WEST));
            levelValue = stateSide.is(Tags.Blocks.FENCES_WOODEN) && ! (stateSide.getBlock() instanceof FenceOnASlabBlock);
            stateBelow = stateBelow.setValue(FenceOnASlabBlock.WEST_UP, levelValue);
            // S
            stateSide = level.getBlockState(pos.move(Direction.EAST).move(Direction.SOUTH));
            levelValue = stateSide.is(Tags.Blocks.FENCES_WOODEN) && ! (stateSide.getBlock() instanceof FenceOnASlabBlock);
            stateBelow = stateBelow.setValue(FenceOnASlabBlock.SOUTH_UP, levelValue);
            //// solid blocks above
            // N
            stateSide = level.getBlockState(pos.move(Direction.NORTH).move(Direction.NORTH));
            stateSideBelow = level.getBlockState(pos.move(Direction.DOWN));
            levelValue = (stateSideBelow.getBlock() instanceof FenceOnASlabBlock) || stateSideBelow.isFaceSturdy(level, pos, Direction.SOUTH) && stateSide.isFaceSturdy(level, pos.move(Direction.UP), Direction.SOUTH);
            stateBelow = stateBelow.setValue(FenceOnASlabBlock.NORTH, levelValue);
            pos.setY(blockPos.getY()); // move up in the above row may or may not happen
            // E
            stateSide = level.getBlockState(pos.move(Direction.SOUTH).move(Direction.EAST));
            stateSideBelow = level.getBlockState(pos.move(Direction.DOWN));
            levelValue = (stateSideBelow.getBlock() instanceof FenceOnASlabBlock) || stateSideBelow.isFaceSturdy(level, pos, Direction.SOUTH) && stateSide.isFaceSturdy(level, pos.move(Direction.UP), Direction.SOUTH);
            stateBelow = stateBelow.setValue(FenceOnASlabBlock.EAST, levelValue);
            pos.setY(blockPos.getY());
            // W
            stateSide = level.getBlockState(pos.move(Direction.WEST).move(Direction.WEST));
            stateSideBelow = level.getBlockState(pos.move(Direction.DOWN));
            levelValue = (stateSideBelow.getBlock() instanceof FenceOnASlabBlock) || stateSideBelow.isFaceSturdy(level, pos, Direction.SOUTH) && stateSide.isFaceSturdy(level, pos.move(Direction.UP), Direction.SOUTH);
            stateBelow = stateBelow.setValue(FenceOnASlabBlock.WEST, levelValue);
            pos.setY(blockPos.getY());
            // E
            stateSide = level.getBlockState(pos.move(Direction.EAST).move(Direction.SOUTH));
            stateSideBelow = level.getBlockState(pos.move(Direction.DOWN));
            levelValue = (stateSideBelow.getBlock() instanceof FenceOnASlabBlock) || stateSideBelow.isFaceSturdy(level, pos, Direction.SOUTH) && stateSide.isFaceSturdy(level, pos.move(Direction.UP), Direction.SOUTH);
            stateBelow = stateBelow.setValue(FenceOnASlabBlock.SOUTH, levelValue);
            pos.setY(blockPos.getY());
            // don't feel like comparing
            pos.move(Direction.NORTH);
            level.setBlockAndUpdate(pos.move(Direction.DOWN), stateBelow);
        }
        super.neighborChanged(blockState, level, blockPos, p_60512_, other, p_60514_);
    }

    /////////////////////////////////


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

    ///////////////////////////////////////////

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

    ////////////////////////////////////////


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

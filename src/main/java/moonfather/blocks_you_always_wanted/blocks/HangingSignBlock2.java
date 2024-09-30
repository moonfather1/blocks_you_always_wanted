package moonfather.blocks_you_always_wanted.blocks;

import moonfather.blocks_you_always_wanted.Constants;
import moonfather.blocks_you_always_wanted.storage.ShopSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public class HangingSignBlock2 extends WallHangingSignBlock
{
    public HangingSignBlock2(SignBlock originalBlock)
    {
        super(originalBlock.type(), defaultProperties().mapColor(originalBlock.defaultMapColor()));
        this.originalBlock = originalBlock;
    }

    private static Properties defaultProperties()
    {
        return Properties.of().forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava();
    }

    private final SignBlock originalBlock;

    ////// use ////////////////////////

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (level.getBlockEntity(blockPos) instanceof ShopSignBlockEntity signBlockEntity)
        {
            if (this.shouldTryToChainAnotherHangingSign2(player, hitResult, itemStack))
            {
                return super.useItemOn(itemStack, state, level, blockPos, player, hand, hitResult);
            }
            if (hand.equals(InteractionHand.MAIN_HAND))
            {
                if (signBlockEntity.isWaxed())
                {
                    return ItemInteractionResult.FAIL;
                }
                if (signBlockEntity.getItem().isEmpty() && itemStack.isEmpty())
                {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;  // allows UseWithoutItem method
                }
                if (signBlockEntity.getItem().isEmpty() || ! (player.getItemInHand(hand).is(Items.HONEYCOMB) || player.getItemInHand(hand).is(Constants.ItemTags.GC_WAX)))
                {
                    // change item
                    signBlockEntity.setItem(player.getItemInHand(hand));
                }
                else
                {
                    // wax on
                    signBlockEntity.setWaxed(true);
                    level.levelEvent(player, 3003, blockPos, 0);
                    player.getItemInHand(hand).shrink(1);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        // shouldn't be here
        return super.useItemOn(itemStack, state, level, blockPos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult)
    {

        if (level.getBlockEntity(blockPos) instanceof ShopSignBlockEntity signBlockEntity)
        {
            if (signBlockEntity.getItem().isEmpty())
            {
                // revert
                BlockState newState = this.originalBlock.withPropertiesOf(state);
                level.setBlockAndUpdate(blockPos, newState);
                newState.useWithoutItem(level, player, hitResult);
            }
            else
            {
                // change item
                signBlockEntity.setItem(ItemStack.EMPTY);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        // shouldn't be here
        return super.useWithoutItem(state, level, blockPos, player, hitResult);
    }

    private boolean shouldTryToChainAnotherHangingSign2(Player player, BlockHitResult blockHitResult, ItemStack itemStack)
    {
        return itemStack.getItem() instanceof HangingSignItem && blockHitResult.getDirection().equals(Direction.DOWN);
    }

    ////////////////////////////////////////////////


    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        return this.originalBlock.getCloneItemStack(state, target, level, pos, player);
    }

    /////////////////////////////////////////////

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new ShopSignBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType)
    {
        return null;
    }
}

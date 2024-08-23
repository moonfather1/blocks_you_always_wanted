package moonfather.blocks_you_always_wanted.blocks;

import moonfather.blocks_you_always_wanted.storage.ShopSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class HangingSignBlock extends CeilingHangingSignBlock
{
    public HangingSignBlock(SignBlock originalBlock)
    {
        super(properties().mapColor(originalBlock.defaultMapColor()), originalBlock.type());
        this.originalBlock = originalBlock;
    }

    private static Properties properties()
    {
        return BlockBehaviour.Properties.of().forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava();
    }

    private final SignBlock originalBlock;

    ////// use ////////////////////////

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult blockHitResult)
    {
        if (level.getBlockEntity(blockPos) instanceof ShopSignBlockEntity signBlockEntity)
        {
            ItemStack itemstack = player.getItemInHand(hand);
            if (this.shouldTryToChainAnotherHangingSign2(player, blockHitResult, itemstack))
            {
                return super.use(blockState, level, blockPos, player, hand, blockHitResult);
            }
            // insert item or wax
            if (hand.equals(InteractionHand.MAIN_HAND) && ! signBlockEntity.isWaxed())
            {
                if (signBlockEntity.getItem().isEmpty() || ! (player.getItemInHand(hand).is(Items.HONEYCOMB) || player.getItemInHand(hand).is(GC_WAX)))
                {
                    signBlockEntity.setItem(player.getItemInHand(hand));
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
                else
                {
                    signBlockEntity.setWaxed(true);
                    player.getItemInHand(hand).shrink(1);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
        }
        // shouldn't be here
        return super.use(blockState, level, blockPos, player, hand, blockHitResult);
    }
    private static final TagKey<Item> GC_WAX = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("growthcraft_apiary:bees_wax"));

    private boolean shouldTryToChainAnotherHangingSign2(Player player, BlockHitResult blockHitResult, ItemStack itemStack)
    {
        return itemStack.getItem() instanceof HangingSignItem && blockHitResult.getDirection().equals(Direction.DOWN);
    }

    ////////////////////////////////////////////////

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
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

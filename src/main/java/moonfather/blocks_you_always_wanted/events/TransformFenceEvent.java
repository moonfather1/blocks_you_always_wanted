package moonfather.blocks_you_always_wanted.events;

import moonfather.blocks_you_always_wanted.Constants;
import moonfather.blocks_you_always_wanted.MainConfig;
import moonfather.blocks_you_always_wanted.initialization.RegistrationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TransformFenceEvent
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (! MainConfig.COMMON.FencesEnabled.get())
        {
            return; // todo: don't register event on 1.21
        }

        BlockState state = event.getLevel().getBlockState(event.getHitVec().getBlockPos());
        if (event.getItemStack() != ItemStack.EMPTY && state.getBlock() instanceof SlabBlock slab && event.getFace() != null && event.getFace().equals(Direction.UP) && event.getItemStack().is(ItemTags.FENCES))
        {
            if (! state.getValue(SlabBlock.TYPE).equals(SlabType.BOTTOM))
            {
                return;
            }
            Block replacementSlab = RegistrationManager.getSlabFromOriginal(slab);
            if (replacementSlab == null)
            {
                event.getEntity().displayClientMessage(Constants.Messages.MESSAGE_SLAB_TYPE, true);
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            Block fence = RegistrationManager.getFenceFromOriginal(((BlockItem) event.getItemStack().getItem()).getBlock());
            if (fence == null)
            {
                event.getEntity().displayClientMessage(Constants.Messages.MESSAGE_FENCE_TYPE, true);
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            BlockPos above = event.getHitVec().getBlockPos().above();
            if (! event.getLevel().getBlockState(above).canBeReplaced())
            {
                event.getEntity().displayClientMessage(Constants.Messages.MESSAGE_NO_ROOM, true);
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            if (! event.getLevel().isClientSide)
            {
                boolean aboveIsWater = event.getLevel().getFluidState(above).isSourceOfType(Fluids.WATER);
                BlockState newFence = fence.defaultBlockState().setValue(FenceBlock.WATERLOGGED, aboveIsWater);
                BlockState newSlab = replacementSlab.defaultBlockState().setValue(SlabBlock.WATERLOGGED, state.getValue(SlabBlock.WATERLOGGED) || aboveIsWater);
                event.getLevel().setBlockAndUpdate(event.getHitVec().getBlockPos(), newSlab);
                event.getLevel().setBlockAndUpdate(above, newFence);
                event.getLevel().getBlockState(above).neighborChanged(event.getLevel(), above, Blocks.COPPER_BLOCK, above.north(), false);          // make connections.
                event.getLevel().getBlockState(above).neighborChanged(event.getLevel(), above, Blocks.COPPER_BLOCK, above.north().below(), false);  // make connections. can't just invoke from newfence, that one is old news. could have used it in above row.
            }
            if (! event.getEntity().isCreative())
            {
                event.getItemStack().shrink(1);
            }
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
            event.setCanceled(true);
        }
    }
}

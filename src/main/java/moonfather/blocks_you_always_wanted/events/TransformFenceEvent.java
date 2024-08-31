package moonfather.blocks_you_always_wanted.events;

import moonfather.blocks_you_always_wanted.blocks.FenceOnASlabBlock;
import moonfather.blocks_you_always_wanted.initialization.RegistrationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
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
    private static final Component MESSAGE_NO_ROOM = Component.translatable("messages.blocks_you_always_wanted.no_room_above").withStyle(Style.EMPTY.withColor(0xff999988));
    private static final Component MESSAGE_WRONG = Component.translatable("messages.blocks_you_always_wanted.wrong_slab").withStyle(Style.EMPTY.withColor(0xff998855));

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInteract(PlayerInteractEvent.RightClickBlock event)
    {
        BlockState state = event.getLevel().getBlockState(event.getHitVec().getBlockPos());
        if (event.getItemStack() != ItemStack.EMPTY && state.getBlock() instanceof SlabBlock slab && event.getFace() != null && event.getFace().equals(Direction.UP) && event.getItemStack().is(ItemTags.FENCES))
        {
            if (! state.getValue(SlabBlock.TYPE).equals(SlabType.BOTTOM))
            {
                return;
            }
            Block replacement = RegistrationManager.getFenceFromOriginal(((BlockItem) event.getItemStack().getItem()).getBlock(), slab);
            if (replacement == null)
            {
                event.getEntity().displayClientMessage(MESSAGE_WRONG, true);
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            BlockPos above = event.getHitVec().getBlockPos().above();
            if (! event.getLevel().getBlockState(above).canBeReplaced())
            {
                event.getEntity().displayClientMessage(MESSAGE_NO_ROOM, true);
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            if (! event.getLevel().isClientSide)
            {
                BlockState newState = replacement.defaultBlockState().setValue(SlabBlock.WATERLOGGED, state.getValue(SlabBlock.WATERLOGGED));
                boolean aboveIsWater = event.getLevel().getFluidState(above).isSourceOfType(Fluids.WATER);
                event.getLevel().setBlockAndUpdate(event.getHitVec().getBlockPos(), newState);
                event.getLevel().setBlockAndUpdate(above, RegistrationManager.FENCE_TECHNICAL.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, aboveIsWater));
                replacement.use(newState, event.getLevel(), event.getHitVec().getBlockPos(), event.getEntity(), InteractionHand.MAIN_HAND, event.getHitVec());
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

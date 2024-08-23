package moonfather.blocks_you_always_wanted.events;

import moonfather.blocks_you_always_wanted.initialization.RegistrationManager;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TransformSignEvent
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInteract(PlayerInteractEvent.RightClickBlock event)
    {
        BlockState state = event.getLevel().getBlockState(event.getHitVec().getBlockPos());
        if (event.getItemStack() != ItemStack.EMPTY && state.getBlock() instanceof CeilingHangingSignBlock original && event.getFace() != null && ! event.getFace().equals(Direction.DOWN))
        {
            Block replacement = RegistrationManager.getFromOriginal(original);
            if (replacement == null) { return; }
            if (! event.getLevel().isClientSide)
            {
                BlockState newState = replacement.defaultBlockState()
                                             .setValue(CeilingHangingSignBlock.ROTATION, state.getValue(CeilingHangingSignBlock.ROTATION))
                                             .setValue(CeilingHangingSignBlock.ATTACHED, state.getValue(CeilingHangingSignBlock.ATTACHED))
                                             .setValue(CeilingHangingSignBlock.WATERLOGGED, state.getValue(CeilingHangingSignBlock.WATERLOGGED));
                event.getLevel().setBlockAndUpdate(event.getHitVec().getBlockPos(), newState);
                replacement.use(newState, event.getLevel(), event.getHitVec().getBlockPos(), event.getEntity(), InteractionHand.MAIN_HAND, event.getHitVec());
            }
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
            event.setCanceled(true);
        }
    }
}

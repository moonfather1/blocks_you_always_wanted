package moonfather.blocks_you_always_wanted.events;

import moonfather.blocks_you_always_wanted.Constants;
import moonfather.blocks_you_always_wanted.MainConfig;
import moonfather.blocks_you_always_wanted.initialization.RegistrationManager;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class TransformSignEvent
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (! MainConfig.COMMON.HangingSignsEnabled.get())
        {
            return; // todo: don't register event on 1.21
        }
        BlockState state = event.getLevel().getBlockState(event.getHitVec().getBlockPos());
        if (event.getItemStack() != ItemStack.EMPTY && state.getBlock() instanceof SignBlock original && event.getFace() != null && ! event.getFace().equals(Direction.DOWN) && ! event.getItemStack().is(Items.HONEYCOMB) && ! event.getItemStack().is(Constants.ItemTags.GC_WAX))
        {
            BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getHitVec().getBlockPos());
            if (blockEntity instanceof SignBlockEntity sbe && sbe.isWaxed())
            {
                return;
            }
            Block replacement = RegistrationManager.getSignFromOriginal(original);
            if (replacement == null) { return; }
            if (! event.getLevel().isClientSide)
            {
                BlockState newState;
                if (original instanceof CeilingHangingSignBlock || original instanceof WallHangingSignBlock)
                {
                    newState = replacement.withPropertiesOf(state);
                }
                else
                {
                    return;
                }
                event.getLevel().setBlockAndUpdate(event.getHitVec().getBlockPos(), newState);
                newState.useItemOn(event.getItemStack(), event.getLevel(), event.getEntity(), event.getHand(), event.getHitVec());
            }
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
            event.setCanceled(true);
        }
    }
}

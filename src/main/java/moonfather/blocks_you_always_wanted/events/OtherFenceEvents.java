package moonfather.blocks_you_always_wanted.events;

import moonfather.blocks_you_always_wanted.blocks.FenceMainBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class OtherFenceEvents
{
    @SubscribeEvent
    public static void onBreakSpeedCheck(PlayerEvent.BreakSpeed event)
    {
        if (event.getState().getBlock() instanceof FenceMainBlock fence)
        {
            if (event.getState().getValue(FenceMainBlock.TOP_VARIANT) != 0)
            {
                event.setNewSpeed(5 * event.getOriginalSpeed());
            }
        }
    }
}

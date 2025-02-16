package moonfather.blocks_you_always_wanted.events.obsolete2;

import moonfather.blocks_you_always_wanted.blocks.FenceVersion3Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class OtherFenceEvents
{
    @SubscribeEvent
    public static void onBreakSpeedCheck(PlayerEvent.BreakSpeed event)
    {
        if (event.getState().getBlock() instanceof FenceVersion3Block fence)
        {
            if (event.getState().getValue(FenceVersion3Block.TOP_VARIANT) != 0)
            {
                event.setNewSpeed(5 * event.getOriginalSpeed());
            }
        }
    }
}

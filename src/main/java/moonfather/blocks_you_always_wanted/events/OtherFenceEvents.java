package moonfather.blocks_you_always_wanted.events;

import moonfather.blocks_you_always_wanted.blocks.FenceMainBlock;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
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
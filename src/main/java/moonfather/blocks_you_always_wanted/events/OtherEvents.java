package moonfather.blocks_you_always_wanted.events;

import moonfather.blocks_you_always_wanted.Constants;
import moonfather.blocks_you_always_wanted.blocks.GateBlock;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class OtherEvents
{
    @SubscribeEvent
    public static void onFurnaceCheck(FurnaceFuelBurnTimeEvent event)
    {
        if (! event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof BlockItem bi && bi.getBlock() instanceof GateBlock)
        {
            event.setBurnTime(300 * 6); // six planks. crafted from eight, i wanted some loss.
        }
    }

    // message when placing normal gate. once per player per session. we'll just remember in a static list here.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlaceGate(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getItemStack() != ItemStack.EMPTY && event.getItemStack().is(ItemTags.FENCE_GATES))
        {
            if (! (event.getItemStack().getItem() instanceof BlockItem bi) || ! (bi.getBlock() instanceof GateBlock gate))
            {
                if (seenMessage.contains(event.getEntity()))
                {
                    return; // once per session
                }
                seenMessage.add(event.getEntity());
                event.getEntity().displayClientMessage(Constants.Messages.MESSAGE_CRAFT_WIDE_GATE, true);
            }
        }
    }
    private static final List<Player> seenMessage = new ArrayList<>();
}

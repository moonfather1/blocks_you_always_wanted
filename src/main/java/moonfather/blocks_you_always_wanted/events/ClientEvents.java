package moonfather.blocks_you_always_wanted.events;

import moonfather.blocks_you_always_wanted.Constants;
import moonfather.blocks_you_always_wanted.MainConfig;
import moonfather.blocks_you_always_wanted.blocks.GateBlock;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.BlockItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvents
{
    @SubscribeEvent
    public static void onGateTooltip(ItemTooltipEvent event)
    {
        if (! MainConfig.COMMON.GatesEnabled.get())
        {
            return; // todo: don't register event on 1.21
        }
        if (event.getEntity() != null && event.getItemStack().is(ItemTags.FENCE_GATES))
        {
            if (event.getItemStack().getItem() instanceof BlockItem bi && ! (bi.getBlock() instanceof GateBlock))
            {
                if (event.getEntity().containerMenu instanceof CraftingMenu)
                {
                    event.getToolTip().add(Constants.Messages.MESSAGE_CRAFT_WIDE_GATE);
                }
            }
        }
    }
}

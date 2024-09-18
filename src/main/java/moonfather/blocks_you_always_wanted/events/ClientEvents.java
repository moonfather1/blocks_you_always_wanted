package moonfather.blocks_you_always_wanted.events;

import moonfather.blocks_you_always_wanted.Constants;
import moonfather.blocks_you_always_wanted.blocks.GateBlock;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvents
{
    @SubscribeEvent
    public static void onGateTooltip(ItemTooltipEvent event)
    {
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

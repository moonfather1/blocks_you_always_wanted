package moonfather.blocks_you_always_wanted.initialization;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class GateHolderItem extends BlockItem
{
    public GateHolderItem(Block block)
    {
        super(block, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS));
    }
}

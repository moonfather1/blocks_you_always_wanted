package moonfather.blocks_you_always_wanted.initialization;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class GateHolderItem extends BlockItem
{
    public GateHolderItem(Block block, Item original)
    {
        super(block, new Item.Properties().stacksTo(1));
        this.original = original;
    }
    private final Item original;

    public Item getOriginal()
    {
        return this.original;
    }
}

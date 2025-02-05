package moonfather.blocks_you_always_wanted.initialization;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class PaperWallItem extends BlockItem
{
    public PaperWallItem(Block block, @Nullable Item craftingRemainder)
    {
        super(block, new Properties());
        this.craftingRemainder = craftingRemainder;
    }
    private final Item craftingRemainder;

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack)
    {
        return this.craftingRemainder != null;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack)
    {
        return this.craftingRemainder != null ? this.craftingRemainder.getDefaultInstance() : ItemStack.EMPTY;
    }
}

package moonfather.blocks_you_always_wanted;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class Constants
{
    public static final String MODID = "blocks_you_always_wanted";

    public static class Tags
    {
        public static final TagKey<Item> GC_WAX = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("growthcraft_apiary:bees_wax"));
    }
} 

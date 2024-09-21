package moonfather.blocks_you_always_wanted;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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

    public static class Messages
    {
        public static final Component MESSAGE_NO_ROOM = Component.translatable("messages.blocks_you_always_wanted.no_room_above").withStyle(Style.EMPTY.withColor(0xff999988));
        public static final Component MESSAGE_NO_ROOM_SIDE = Component.translatable("messages.blocks_you_always_wanted.no_room_on_side").withStyle(Style.EMPTY.withColor(0xffaa9988));
        public static final Component MESSAGE_SLAB_TYPE = Component.translatable("messages.blocks_you_always_wanted.wrong_slab").withStyle(Style.EMPTY.withColor(0xff998855));
        public static final Component MESSAGE_CRAFT_WIDE_GATE = Component.translatable("messages.blocks_you_always_wanted.craft_wide_gate").withStyle(Style.EMPTY.withColor(0xff998855));
        public static final Component MESSAGE_RAIL_TYPE = Component.translatable("messages.blocks_you_always_wanted.rail_type").withStyle(Style.EMPTY.withColor(0xff998855));
        public static final Component MESSAGE_STRAIGHT_ONLY = Component.translatable("messages.blocks_you_always_wanted.straight_rails_only").withStyle(Style.EMPTY.withColor(0xff998855));
        public static final Component MESSAGE_WRONG_ANGLE = Component.translatable("messages.blocks_you_always_wanted.rails_wrong_angle").withStyle(Style.EMPTY.withColor(0xff998855));
    }
} 

package moonfather.blocks_you_always_wanted;

import moonfather.blocks_you_always_wanted.initialization.GateHolderItem;
import moonfather.blocks_you_always_wanted.initialization.RegistrationManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.function.Supplier;

@Mod(Constants.MODID)
public class BlocksMod
{
    public BlocksMod(IEventBus modEventBus, ModContainer modContainer)
    {
		modContainer.registerConfig(ModConfig.Type.COMMON, MainConfig.COMMON_SPEC);
        RegistrationManager.init(modEventBus);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::commonSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event)
    {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (MainConfig.COMMON.GatesEnabled.get() && event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
        {
            for (Supplier<Item> item : RegistrationManager.itemsForCreativeTabs)
            {
                event.insertAfter(((GateHolderItem) item.get()).getOriginal().getDefaultInstance(), item.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }
}

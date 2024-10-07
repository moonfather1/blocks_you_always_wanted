package moonfather.blocks_you_always_wanted;

import moonfather.blocks_you_always_wanted.initialization.RegistrationManager;
import moonfather.blocks_you_always_wanted.initialization.config.StartupConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Supplier;

@Mod(Constants.MODID)
public class BlocksMod
{
    public BlocksMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        RegistrationManager.init(modEventBus);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::commonSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event)
    {
        CraftingHelper.register(new OptionalRecipeCondition.Serializer(new ResourceLocation(Constants.MODID, "optional")));;
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (StartupConfig.wideGatesEnabled() && event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
        {
            for (Item item : RegistrationManager.itemsForCreativeTabs)
            {
                event.accept(item);
            }
        }
    }
}

package moonfather.blocks_you_always_wanted;

import moonfather.blocks_you_always_wanted.initialization.RegistrationManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Supplier;

@Mod(Constants.MODID)
public class BlocksMod
{
    public BlocksMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MainConfig.COMMON_SPEC);

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
        if (MainConfig.COMMON.GatesEnabled.get() && event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
        {
            for (Supplier<Item> item : RegistrationManager.itemsForCreativeTabs)
            {
                event.accept(item);
            }
        }
    }
}

package moonfather.blocks_you_always_wanted;

import moonfather.blocks_you_always_wanted.initialization.RegistrationManager;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MODID)
public class BlocksMod
{
    public BlocksMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //todo: collision
        //+odo: initial state
        //+odo: fence technical top, 3 states, 2 glow
        //+odo: detect side+up change
        //+odo: model torch and lantern
        //+odo: Component.literal
        //+odo: technical particles
        //+odo: top variants swap
        //+odo: top loot table
        //+odo: upwards link
        //+odo: event ignores fence type
        //+odo: stone base?
        //+odo: disap top
        //+odo: message color
        //no:   gates
        //+odo: redstone signal
        //todo: add fake collision above so that i can't jump over
        //+odo: connect to solid posts
        RegistrationManager.init(modEventBus);
//        modEventBus.addListener(this::addCreative);
//        modEventBus.addListener(this::commonSetup);
    }

//    // Add the example block item to the building blocks tab
//    private void addCreative(BuildCreativeModeTabContentsEvent event)
//    {
//        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
//        { }    //event.accept(EXAMPLE_BLOCK_ITEM);
//    }
}

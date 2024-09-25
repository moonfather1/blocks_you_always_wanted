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

        //+odo: collision
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
        //+odo: add fake collision above so that i can't jump over
        //+odo: connect to solid posts

        //+odo: gate: inventory
        //+odo: gate: rail variant
        //+odo: gate: slab variant
        //+odo: gate: drops
        //+odo: gate: furnace value
        //+odo: gate: piston reaction
        //+odo: gate: pick on spec gate
        //+odo: gate: normal: interaction and collision
        //+odo: gate: spec: interaction and collision, same likely
        //+odo: gate: spec: lift model 1px
        //+odo: gate: main: hole in connection side
        //+odo: gate: rail functionality
        //+odo: gate: acceptable blocks on sides          REFUSE DIRT++         BREAK ON PISTON MOVING SOMETHING++    ++DISALLOW PLACING GATE NEXT TO NON-FENCE-NON-WALL
        //+odo: gate: spec on below removed
        //+odo: gate: rail click on normal wide                   RAIL DUPE ISSUE++          NO CHECK FOR SOLID BELOW++
        //+odo: gate: sloped rails?
        //+odo: craft gate msg in craft gui and on place
        //+odo: gate technical. has facing and just that. split interaction shape. test for "Rejecting UseItemOnPacket"
        //+odo: try carry on, try packing tape.
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
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
        {
            for (Supplier<Item> item : RegistrationManager.itemsForCreativeTabs)
            {
                event.accept(item);
            }
        }
    }
}

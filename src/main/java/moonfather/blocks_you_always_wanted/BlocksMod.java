package moonfather.blocks_you_always_wanted;

import moonfather.blocks_you_always_wanted.initialization.RegistrationManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(Constants.MODID)
public class BlocksMod
{
    public BlocksMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        RegistrationManager.init(modEventBus);
        modEventBus.addListener(this::commonSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event)
    {
        CraftingHelper.register(new OptionalRecipeCondition.Serializer(new ResourceLocation(Constants.MODID, "optional")));;
    }
}

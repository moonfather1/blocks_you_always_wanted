package moonfather.blocks_you_always_wanted.initialization;

import moonfather.blocks_you_always_wanted.Constants;
import moonfather.blocks_you_always_wanted.rendering.ShopSignRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Constants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup
{
    @SubscribeEvent
    public static void Initialize(FMLClientSetupEvent event)
    {

    }

    @SubscribeEvent
    public static void RegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(RegistrationManager.SIGN_BE.get(), ShopSignRenderer::new);
    }
}

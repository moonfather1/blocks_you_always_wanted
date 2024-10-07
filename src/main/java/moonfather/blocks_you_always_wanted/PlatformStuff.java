package moonfather.blocks_you_always_wanted;

import net.minecraftforge.fml.loading.FMLConfig;

import java.nio.file.Path;

public class PlatformStuff
{
    public static Path getConfigPath()
    {
        return Path.of(FMLConfig.defaultConfigPath(), "../config", Constants.MODID + "-special.json");
    }
}

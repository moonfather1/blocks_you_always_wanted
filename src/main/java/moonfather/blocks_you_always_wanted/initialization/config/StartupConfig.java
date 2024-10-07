package moonfather.blocks_you_always_wanted.initialization.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import moonfather.blocks_you_always_wanted.PlatformStuff;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class StartupConfig
{
    public static boolean wideGatesEnabled()
    {
        return getStored().wide_gates_enabled;
    }

    public static boolean signsEnabled()
    {
        return getStored().hanging_signs_enabled;
    }

    public static boolean fencesEnabled()
    {
        return getStored().fences_enabled;
    }

    ///////////////////////////////////////////////////////////////

    private static StoredConfig getStored()
    {
        if (storedConfig == null)
        {
            Path configPath = PlatformStuff.getConfigPath();
            boolean readingFailed = false;
            if (configPath.toFile().exists())
            {
                try
                {
                    Gson gson = new Gson();
                    storedConfig = gson.fromJson(Files.readString(configPath), StoredConfig.class);
                }
                catch (IOException ignored)
                {
                    readingFailed = true; //this would cause overwrites on error. not sure if i want that.
                }
            }
            if (storedConfig == null)
            {
                storedConfig = new StoredConfig();
            }
            if (readingFailed || ! configPath.toFile().exists())
            {
                try
                {
                    Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
                    String text = gson.toJson(storedConfig, StoredConfig.class);
                    Files.writeString(configPath, text, StandardCharsets.US_ASCII, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                }
                catch (IOException e)
                {
                    readingFailed = true;
                }
            }
        }
        return storedConfig;
    }
    private static StoredConfig storedConfig = null;

    ///////////////////////////////////

    private static class StoredConfig
    {
        public String about_this_file = "This line is just an explanation of this uncommon config system. Config is implemented like this because normal config can not be used to not-register blocks. Registration happens before pretty-much-anything. Never mind the details, we have one limitation to go through: THIS FILE MUST BE THE SAME ON CLIENT AND SERVER SIDE. There is no synchronization. If you want to turn fences off, that is fine, but it must be done on the server and all clients.";
        public boolean hanging_signs_enabled = true;
        public boolean fences_enabled = true;
        public boolean wide_gates_enabled = true;
    }
}

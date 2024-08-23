package moonfather.blocks_you_always_wanted.initialization;

import moonfather.blocks_you_always_wanted.Constants;
import moonfather.blocks_you_always_wanted.blocks.HangingSignBlock;
import moonfather.blocks_you_always_wanted.storage.ShopSignBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RegistrationManager
{
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MODID);

    public static void init(IEventBus modBus)
    {
        BLOCKS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
    }

    /////////////////////////////////////////////////////////////

    private static final List<Supplier<Block>> signBlocks = new ArrayList<>();
    private static final Map<Block, Supplier<Block>> signBlocksByOriginal = new HashMap<>(); // remove if original is made public
    public static final RegistryObject<BlockEntityType<ShopSignBlockEntity>> SIGN_BE = BLOCK_ENTITIES.register("sign_be", () -> BlockEntityType.Builder.of(ShopSignBlockEntity::new, listToArray(signBlocks)).build(null));

    private static Block[] listToArray(List<Supplier<Block>> list)
    {
        Block[] result = new Block[list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            result[i] = list.get(i).get();
        }
        return result;
    }

    //////////////////////////////////////////////////////////////////////////

    // static initialization
    static
    {
        addVariant(Blocks.OAK_HANGING_SIGN);
        addVariant(Blocks.SPRUCE_HANGING_SIGN);
    }
    private static void addVariant(Block original)
    {
        SignBlock originalCast = (SignBlock) original;
        RegistryObject<Block> ourBlock = BLOCKS.register("hanging_sign_" + originalCast.type().name(), () -> new HangingSignBlock(originalCast));
        signBlocks.add(ourBlock);
        signBlocksByOriginal.put(original, ourBlock);
    }

    /////////////////////////////////////////////////////////////////////

    public static Block getFromOriginal(Block original)
    {
        return signBlocksByOriginal.getOrDefault(original, ()->null).get();
    }
}

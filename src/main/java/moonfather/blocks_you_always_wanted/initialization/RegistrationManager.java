package moonfather.blocks_you_always_wanted.initialization;

import moonfather.blocks_you_always_wanted.Constants;
import moonfather.blocks_you_always_wanted.blocks.FenceOnASlabBlock;
import moonfather.blocks_you_always_wanted.blocks.FenceTechnicalBlock;
import moonfather.blocks_you_always_wanted.blocks.HangingSignBlock1;
import moonfather.blocks_you_always_wanted.blocks.HangingSignBlock2;
import moonfather.blocks_you_always_wanted.storage.ShopSignBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
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
    private static final Map<Integer, Supplier<Block>> fenceBlocksByOriginal = new HashMap<>();
    public static final RegistryObject<BlockEntityType<ShopSignBlockEntity>> SIGN_BE = BLOCK_ENTITIES.register("sign_be", () -> BlockEntityType.Builder.of(ShopSignBlockEntity::new, listToArray(signBlocks)).build(null));
    public static final RegistryObject<Block> FENCE_TECHNICAL = BLOCKS.register("fence_technical_block", FenceTechnicalBlock::new);

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
        addSignVariant(Blocks.OAK_HANGING_SIGN);
        addSignVariant(Blocks.OAK_WALL_HANGING_SIGN);
        addSignVariant(Blocks.SPRUCE_HANGING_SIGN);
        addSignVariant(Blocks.SPRUCE_WALL_HANGING_SIGN);
        addFenceVariant(Blocks.OAK_FENCE, Blocks.OAK_SLAB, "oak", "fence_on_slab_");
        addFenceVariant(Blocks.SPRUCE_FENCE, Blocks.SPRUCE_SLAB, "spruce", "fence_on_slab_");
        addFenceVariant(Blocks.OAK_FENCE, Blocks.SMOOTH_STONE_SLAB, "oak", "fence_on_stone_slab_");
        addFenceVariant(Blocks.SPRUCE_FENCE, Blocks.SMOOTH_STONE_SLAB, "spruce", "fence_on_stone_slab_");
    }

    private static void addSignVariant(Block original)
    {
        SignBlock originalCast = (SignBlock) original;
        RegistryObject<Block> ourBlock;
        if (! (original instanceof WallHangingSignBlock))
        {
            ourBlock = BLOCKS.register("hanging_sign_1_" + originalCast.type().name(), () -> new HangingSignBlock1(originalCast));
        }
        else
        {
            ourBlock = BLOCKS.register("hanging_sign_2_" + originalCast.type().name(), () -> new HangingSignBlock2(originalCast));
        }
        signBlocks.add(ourBlock);
        signBlocksByOriginal.put(original, ourBlock);
    }

    private static void addFenceVariant(Block original, Block slab, String woodType, String prefix)
    {
        RegistryObject<Block> ourBlock = BLOCKS.register(prefix + woodType, () -> new FenceOnASlabBlock(original));
        cacheFenceBlock(original, slab, ourBlock);
    }
    /////////////////////////////////////////////////////////////////////

    public static Block getSignFromOriginal(Block original)
    {
        return signBlocksByOriginal.getOrDefault(original, ()->null).get();
    }

    public static Block getFenceFromOriginal(Block original, Block slab)
    {
        return fenceBlocksByOriginal.getOrDefault(original.hashCode() / 2 + slab.hashCode() / 2, ()->null).get();
    }

    private static void cacheFenceBlock(Block original, Block slab, Supplier<Block> ourFence)
    {
        fenceBlocksByOriginal.put(original.hashCode() / 2 + slab.hashCode() / 2, ourFence);
    }
}

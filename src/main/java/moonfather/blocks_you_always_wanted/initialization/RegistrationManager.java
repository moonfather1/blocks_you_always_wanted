package moonfather.blocks_you_always_wanted.initialization;

import com.mojang.datafixers.util.Function3;
import moonfather.blocks_you_always_wanted.Constants;
import moonfather.blocks_you_always_wanted.blocks.*;
import moonfather.blocks_you_always_wanted.initialization.config.StartupConfig;
import moonfather.blocks_you_always_wanted.storage.ShopSignBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegistrationManager
{
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MODID);
    public static List<Item> itemsForCreativeTabs = new ArrayList<>();

    public static void init(IEventBus modBus)
    {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        modBus.addListener(RegistrationManager::onRegisterThings);
    }

    /////////////////////////////////////////////////////////////

    private static final List<Block> signBlocks = new ArrayList<>();
    private static final Map<Block, Block> signBlocksByOriginal = new HashMap<>(); // remove if original is made public
    private static final Map<Block, Block> slabBlocksByOriginal = new HashMap<>();
    private static final Map<Block, Block> fenceBlocksByOriginal = new HashMap<>();
    public static final RegistryObject<BlockEntityType<ShopSignBlockEntity>> SIGN_BE = BLOCK_ENTITIES.register("sign_be", () -> BlockEntityType.Builder.of(ShopSignBlockEntity::new, listToArray(signBlocks)).build(null));
    public static final RegistryObject<Block> GATE_TECHNICAL = BLOCKS.register("gate_technical_block", GateTechnicalBlock::new);

    private static Block[] listToArray(List<Block> list)
    {
        Block[] result = new Block[list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            result[i] = list.get(i);
        }
        return result;
    }

    //////////////////////////////////////////////////////////////////////////

    private static void onRegisterThings(RegisterEvent event)
    {
        if (event.getRegistryKey().registry().equals(Registries.BLOCK.registry())
                && event.getRegistryKey().location().equals(Registries.BLOCK.location()))
        {
            // signs
            if (StartupConfig.signsEnabled())
            {
                String prefix1 = "hanging_sign_1_", prefix2 = "hanging_sign_2_";
                event.register(Registries.BLOCK, new ResourceLocation(Constants.MODID, prefix1 + "oak"), () -> makeSign(Blocks.OAK_HANGING_SIGN));  // have wood type in originalCast.type().name()
                event.register(Registries.BLOCK, new ResourceLocation(Constants.MODID, prefix2 + "oak"), () -> makeSign(Blocks.OAK_WALL_HANGING_SIGN));
                event.register(Registries.BLOCK, new ResourceLocation(Constants.MODID, prefix1 + "spruce"), () -> makeSign(Blocks.SPRUCE_HANGING_SIGN));
                event.register(Registries.BLOCK, new ResourceLocation(Constants.MODID, prefix2 + "spruce"), () -> makeSign(Blocks.SPRUCE_WALL_HANGING_SIGN));
            }
            // fences, slabs
            if (StartupConfig.fencesEnabled())
            {
                String prefix0 = "fence_raised_";
                event.register(Registries.BLOCK, new ResourceLocation(Constants.MODID, prefix0 + "oak"), () -> makeFence(Blocks.OAK_FENCE));
                event.register(Registries.BLOCK, new ResourceLocation(Constants.MODID, prefix0 + "spruce"), () -> makeFence(Blocks.SPRUCE_FENCE));
                prefix0 = "fence_base_slab_";
                event.register(Registries.BLOCK, new ResourceLocation(Constants.MODID, prefix0 + "oak"), () -> makeFenceBase(Blocks.OAK_SLAB));
                event.register(Registries.BLOCK, new ResourceLocation(Constants.MODID, prefix0 + "spruce"), () -> makeFenceBase(Blocks.SPRUCE_SLAB));
                event.register(Registries.BLOCK, new ResourceLocation(Constants.MODID, prefix0 + "smooth_stone"), () -> makeFenceBase(Blocks.SMOOTH_STONE_SLAB));
            }
            // gates
            if (StartupConfig.wideGatesEnabled())
            {
                TriConsumer<String, String, Supplier<Block>> action = (prefix, type, block) -> event.register(Registries.BLOCK, new ResourceLocation(Constants.MODID, prefix + type), block);
                registerGate(Blocks.OAK_FENCE_GATE, Blocks.OAK_SLAB, WoodType.OAK, action);
                registerGate(Blocks.SPRUCE_FENCE_GATE, Blocks.SPRUCE_SLAB, WoodType.SPRUCE, action);
            }
            return;
        }
        if (event.getRegistryKey().registry().equals(Registries.ITEM.registry())
                && event.getRegistryKey().location().equals(Registries.ITEM.location()))
        {
            for (var entry : itemsToRegister.entrySet())
            {
                event.register(Registries.ITEM, new ResourceLocation(Constants.MODID, entry.getKey()), () -> entry.getValue());
            }
        }
    }
    private static final Map<String, Item> itemsToRegister = new HashMap<>();

    //////////////////////////////////////////////////////////////////////////

    private static void registerGate(Block originalGate, Block originalSlab, WoodType woodType, TriConsumer<String, String, Supplier<Block>> registrationAction)
    {
        Block gate1 = new GateBlock(originalGate, woodType);
        registrationAction.accept("gate_main_",  woodType.name(), () -> gate1);
        Block gate2 = new GateRaisedBlock(originalGate, originalSlab, woodType);
        registrationAction.accept("gate_spec_",  woodType.name(), () -> gate2);
        Item gateItem = new GateHolderItem(gate1);
        itemsForCreativeTabs.add(gateItem);
        itemsToRegister.put("gate_main_" + woodType.name(), gateItem);
    }

    private static Block makeSign(Block original)
    {
        SignBlock originalCast = (SignBlock) original;
        Block ourBlock;
        if (! (original instanceof WallHangingSignBlock))
        {
            ourBlock = new HangingSignBlock1(originalCast);
        }
        else
        {
            ourBlock = new HangingSignBlock2(originalCast);
        }
        signBlocks.add(ourBlock);
        signBlocksByOriginal.put(original, ourBlock);
        return ourBlock;
    }

    private static Block makeFence(Block original)
    {
        Block ourFence = new FenceMainBlock(original);
        fenceBlocksByOriginal.put(original, ourFence);
        return ourFence;
    }

    private static Block makeFenceBase(Block slab)
    {
        Block ourSlab = new FenceBearingSlabBlock(slab);
        slabBlocksByOriginal.put(slab, ourSlab);
        return ourSlab;
    }

    /////////////////////////////////////////////////////////////////////

    public static Block getSignFromOriginal(Block original)
    {
        return signBlocksByOriginal.getOrDefault(original, null);
    }

    public static Block getFenceFromOriginal(Block original)
    {
        return fenceBlocksByOriginal.getOrDefault(original, null);
    }

    public static Block getSlabFromOriginal(Block original)
    {
        return slabBlocksByOriginal.getOrDefault(original, null);
    }
}

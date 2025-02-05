package moonfather.blocks_you_always_wanted.initialization;

import moonfather.blocks_you_always_wanted.Constants;
import moonfather.blocks_you_always_wanted.MainConfig;
import moonfather.blocks_you_always_wanted.blocks.*;
import moonfather.blocks_you_always_wanted.storage.ShopSignBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RegistrationManager
{
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Constants.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.MODID);
    public static List<GateHolderItem> itemsForCreativeTabBuilding = new ArrayList<>();
    public static List<Item> itemsForCreativeTabDecorative = new ArrayList<>();

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
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShopSignBlockEntity>> SIGN_BE = BLOCK_ENTITIES.register("sign_be", () -> BlockEntityType.Builder.of(ShopSignBlockEntity::new, listToArray(signBlocks)).build(null));
    public static final DeferredBlock<Block> GATE_TECHNICAL = BLOCKS.register("gate_technical_block", GateTechnicalBlock::new);

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

    private static void onRegisterThings(final RegisterEvent event)
    {
        if (event.getRegistryKey().equals(Registries.BLOCK))
        {
            // signs
            if (MainConfig.COMMON.HangingSignsEnabled.get())
            {
                String prefix1 = "hanging_sign_1_", prefix2 = "hanging_sign_2_";
                event.register(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MODID, prefix1 + "oak"), () -> makeSign(Blocks.OAK_HANGING_SIGN));  // have wood type in originalCast.type().name()
                event.register(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MODID, prefix2 + "oak"), () -> makeSign(Blocks.OAK_WALL_HANGING_SIGN));
                event.register(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MODID, prefix1 + "spruce"), () -> makeSign(Blocks.SPRUCE_HANGING_SIGN));
                event.register(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MODID, prefix2 + "spruce"), () -> makeSign(Blocks.SPRUCE_WALL_HANGING_SIGN));
            }
            // fences, slabs
            if (MainConfig.COMMON.FencesEnabled.get())
            {
//                String prefix0 = "fence_raised_";
//                event.register(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MODID, prefix0 + "oak"), () -> makeFence(Blocks.OAK_FENCE));
//                event.register(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MODID, prefix0 + "spruce"), () -> makeFence(Blocks.SPRUCE_FENCE));
//                prefix0 = "fence_base_slab_";
//                event.register(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MODID, prefix0 + "oak"), () -> makeFenceBase(Blocks.OAK_SLAB));
//                event.register(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MODID, prefix0 + "spruce"), () -> makeFenceBase(Blocks.SPRUCE_SLAB));
//                event.register(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MODID, prefix0 + "smooth_stone"), () -> makeFenceBase(Blocks.SMOOTH_STONE_SLAB));
            }
            // gates
            if (MainConfig.COMMON.GatesEnabled.get())
            {
                TriConsumer<String, String, Supplier<Block>> action = (prefix, type, block) -> event.register(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MODID, prefix + type), block);
                registerGate(Blocks.OAK_FENCE_GATE, Blocks.OAK_SLAB, WoodType.OAK, action);
                registerGate(Blocks.SPRUCE_FENCE_GATE, Blocks.SPRUCE_SLAB, WoodType.SPRUCE, action);
            }
            // shoji
            if (MainConfig.COMMON.WallsEnabled.get())
            {
                Block wall1 = new PaperWallBlock();
                event.register(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MODID, "shoji_main_wall"), () -> wall1);
                Item wall1Item = new PaperWallItem(wall1, null);
                itemsForCreativeTabDecorative.add(wall1Item);
                itemsToRegister.put("shoji_main_wall", wall1Item);
                Block wall2 = new PaperWallBlock();
                event.register(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MODID, "shoji_main_cabinet"), () -> wall2);
                Item wall2Item = new PaperWallItem(wall2, wall1Item);
                itemsForCreativeTabDecorative.add(wall2Item);
                itemsToRegister.put("shoji_main_cabinet", wall2Item);
            }
            return;
        }
        if (event.getRegistryKey().equals(Registries.ITEM))
        {
            for (var entry : itemsToRegister.entrySet())
            {
                event.register(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MODID, entry.getKey()), () -> entry.getValue());
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
        GateHolderItem gateItem = new GateHolderItem(gate1, originalGate.asItem());
        itemsForCreativeTabBuilding.add(gateItem);
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

    ////////////////////////////////////////////////////////////////////////////////////////

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

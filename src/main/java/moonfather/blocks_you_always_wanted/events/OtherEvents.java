package moonfather.blocks_you_always_wanted.events;

import moonfather.blocks_you_always_wanted.Constants;
import moonfather.blocks_you_always_wanted.blocks.GateBlock;
import moonfather.blocks_you_always_wanted.blocks.GateBlock_V2;
import moonfather.blocks_you_always_wanted.blocks.GateTechnicalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class OtherEvents
{
    @SubscribeEvent
    public static void onFurnaceCheck(FurnaceFuelBurnTimeEvent event)
    {
        if (! event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof BlockItem bi && bi.getBlock() instanceof GateBlock)
        {
            event.setBurnTime(300 * 6); // six planks. crafted from eight, i wanted some loss.
        }
    }



    // message when placing normal gate. once per player per session. we'll just remember in a static list here.
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPlaceGate(BlockEvent.EntityPlaceEvent event)
    {
        if (event.getPlacedBlock().is(BlockTags.FENCE_GATES))
        {
            if (! (event.getPlacedBlock().getBlock() instanceof GateBlock) && event.getEntity() instanceof Player player)
            {
                if (seenMessage.contains(player))
                {
                    return; // once per session
                }
                seenMessage.add(player);
                player.displayClientMessage(Constants.Messages.MESSAGE_CRAFT_WIDE_GATE, true);
            }
        }
    }
    private static final List<Player> seenMessage = new ArrayList<>();



    // we will deny placing blocks next to wide gates. most blocks.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBeforePlaceBlock(PlayerInteractEvent.RightClickBlock event)
    {
        // first see if we have room for the gate and deny it if we do not
        if (event.getItemStack().getItem() instanceof BlockItem bi && bi.getBlock() instanceof GateBlock)
        {
            // check if there is room for tech block above the gate
            BlockPos.MutableBlockPos temp1 = new BlockPos.MutableBlockPos();
            temp1.set(event.getPos()); // target
            if (! event.getLevel().getBlockState(event.getPos()).canBeReplaced() && event.getFace() != null) { temp1.move(event.getFace()); } // empty space?
            temp1.move(Direction.UP);
            BlockState above = event.getLevel().getBlockState(temp1);
            if (! above.isAir() && ! above.canBeReplaced())
            {
                event.getEntity().displayClientMessage(Constants.Messages.MESSAGE_NO_ROOM, true);
                event.setCanceled(true);
                return;
            }
            // check above gate done
            // now we check for room to the left and to the right
            if (! event.getEntity().getDirection().getAxis().equals(Direction.Axis.Y))
            {
                temp1.move(event.getEntity().getDirection().getClockWise());
                BlockState side = event.getLevel().getBlockState(temp1);
                boolean ok = side.isAir() || side.is(BlockTags.FENCES) || side.is(BlockTags.WALLS);
                temp1.move(event.getEntity().getDirection().getCounterClockWise(), 2);
                side = event.getLevel().getBlockState(temp1);
                ok &= side.isAir() || side.is(BlockTags.FENCES) || side.is(BlockTags.WALLS);
                temp1.move(Direction.DOWN);
                side = event.getLevel().getBlockState(temp1);
                ok &= side.isAir() || side.is(BlockTags.FENCES) || side.is(BlockTags.WALLS);
                temp1.move(event.getEntity().getDirection().getClockWise(), 2);
                side = event.getLevel().getBlockState(temp1);
                ok &= side.isAir() || side.is(BlockTags.FENCES) || side.is(BlockTags.WALLS);
                if (! ok)
                {
                    event.getEntity().displayClientMessage(Constants.Messages.MESSAGE_NO_ROOM_SIDE, true);
                    event.setCanceled(true);
                    return;
                }
            }
            return;
        } // done checking the gate placement
        /////////////////////////////////
        // now check for blocks being placed next to the gate
        if (event.getItemStack().isEmpty() || ! (event.getItemStack().getItem() instanceof BlockItem bi))
        {
            return; // we only care for block-items
        }
        if (event.getItemStack().is(ItemTags.FENCES) || event.getItemStack().is(ItemTags.WALLS))
        {
            return; // allow these.
        }
        BlockPos.MutableBlockPos temp = new BlockPos.MutableBlockPos();
        temp.set(event.getPos()); // target
        if (! event.getLevel().getBlockState(event.getPos()).canBeReplaced() && event.getFace() != null) { temp.move(event.getFace()); } // empty space?
        BlockState adjacent;
        adjacent = event.getLevel().getBlockState(temp.move(Direction.NORTH));
        if (adjacent.getBlock() instanceof GateTechnicalBlock) { adjacent = event.getLevel().getBlockState(temp.move(Direction.DOWN)); temp.move(Direction.UP); }  temp.move(Direction.SOUTH);
        if ((adjacent.getBlock() instanceof GateBlock || adjacent.getBlock() instanceof GateBlock_V2) && (adjacent.getValue(GateBlock.FACING).equals(Direction.EAST) || adjacent.getValue(GateBlock.FACING).equals(Direction.WEST)))
        {
            event.setUseItem(Event.Result.DENY);
            return;
        }
        adjacent = event.getLevel().getBlockState(temp.move(Direction.WEST));
        if (adjacent.getBlock() instanceof GateTechnicalBlock) { adjacent = event.getLevel().getBlockState(temp.move(Direction.DOWN)); temp.move(Direction.UP); }  temp.move(Direction.EAST);
        if ((adjacent.getBlock() instanceof GateBlock || adjacent.getBlock() instanceof GateBlock_V2) && (adjacent.getValue(GateBlock.FACING).equals(Direction.NORTH) || adjacent.getValue(GateBlock.FACING).equals(Direction.SOUTH)))
        {
            event.setUseItem(Event.Result.DENY);
            return;
        }
        adjacent = event.getLevel().getBlockState(temp.move(Direction.EAST));
        if (adjacent.getBlock() instanceof GateTechnicalBlock) { adjacent = event.getLevel().getBlockState(temp.move(Direction.DOWN)); temp.move(Direction.UP); }  temp.move(Direction.WEST);
        if ((adjacent.getBlock() instanceof GateBlock || adjacent.getBlock() instanceof GateBlock_V2) && (adjacent.getValue(GateBlock.FACING).equals(Direction.NORTH) || adjacent.getValue(GateBlock.FACING).equals(Direction.SOUTH)))
        {
            event.setUseItem(Event.Result.DENY);
            return;
        }
        adjacent = event.getLevel().getBlockState(temp.move(Direction.SOUTH));
        if (adjacent.getBlock() instanceof GateTechnicalBlock) { adjacent = event.getLevel().getBlockState(temp.move(Direction.DOWN)); temp.move(Direction.UP); }  temp.move(Direction.NORTH);
        if ((adjacent.getBlock() instanceof GateBlock || adjacent.getBlock() instanceof GateBlock_V2) && (adjacent.getValue(GateBlock.FACING).equals(Direction.EAST) || adjacent.getValue(GateBlock.FACING).equals(Direction.WEST)))
        {
            event.setUseItem(Event.Result.DENY);
            //return;
        }
    }
}

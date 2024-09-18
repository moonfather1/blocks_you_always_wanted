package moonfather.blocks_you_always_wanted.events;

import moonfather.blocks_you_always_wanted.Constants;
import moonfather.blocks_you_always_wanted.blocks.GateBlock;
import moonfather.blocks_you_always_wanted.blocks.GateBlock_V2;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;;

@Mod.EventBusSubscriber
public class TransformGateEvent
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInteract(PlayerInteractEvent.RightClickBlock event)
    {
        BlockState state = event.getLevel().getBlockState(event.getHitVec().getBlockPos());
        if (event.getItemStack() != ItemStack.EMPTY
                && event.getFace() != null && event.getFace().equals(Direction.UP)
                && event.getItemStack().is(ItemTags.FENCE_GATES))
        {
            if (! (event.getItemStack().getItem() instanceof BlockItem bi) || ! (bi.getBlock() instanceof GateBlock gate))
            {
                return;
            }
            SlabBlock slab = null;  if (state.getBlock() instanceof SlabBlock) { slab = (SlabBlock) state.getBlock(); }
            BaseRailBlock rail = null;   if (state.getBlock() instanceof BaseRailBlock) { rail = (BaseRailBlock) state.getBlock(); }
            if (slab == null && rail == null)
            {
                return;
            }
            if (slab != null && ! state.getValue(SlabBlock.TYPE).equals(SlabType.BOTTOM))
            {
                return;
            }
            if (rail != null)
            {
                boolean type_ok = false;
                Direction playerDirection = event.getEntity().getDirection();
                RailShape shape = null;
                if (rail.equals(Blocks.POWERED_RAIL))
                {
                    shape = state.getValue(PoweredRailBlock.SHAPE);
                    type_ok = true;
                }
                if (rail.equals(Blocks.RAIL))
                {
                    shape = state.getValue(RailBlock.SHAPE);
                    type_ok = true;
                }
                if (! type_ok)
                {
                    event.getEntity().displayClientMessage(Constants.Messages.MESSAGE_RAIL_TYPE, true);
                    event.setCancellationResult(InteractionResult.FAIL);
                    event.setCanceled(true);
                    return;
                }
                if (((shape.equals(RailShape.NORTH_SOUTH) || shape.equals(RailShape.ASCENDING_SOUTH) || shape.equals(RailShape.ASCENDING_NORTH)) && (playerDirection.equals(Direction.EAST) || playerDirection.equals(Direction.WEST)))
                        || ((shape.equals(RailShape.EAST_WEST) || shape.equals(RailShape.ASCENDING_EAST) || shape.equals(RailShape.ASCENDING_WEST)) && (playerDirection.equals(Direction.NORTH) || playerDirection.equals(Direction.SOUTH))))
                {
                    event.getEntity().displayClientMessage(Constants.Messages.MESSAGE_WRONG_ANGLE, true);
                    event.setCancellationResult(InteractionResult.FAIL);
                    event.setCanceled(true);
                    return;
                }
                if (shape.equals(RailShape.NORTH_EAST) || shape.equals(RailShape.NORTH_WEST) || shape.equals(RailShape.SOUTH_EAST) || shape.equals(RailShape.SOUTH_WEST))
                {
                    event.getEntity().displayClientMessage(Constants.Messages.MESSAGE_STRAIGHT_ONLY, true);
                    event.setCancellationResult(InteractionResult.FAIL);
                    event.setCanceled(true);
                    return;
                }
            }
            ResourceLocation gateRL = ForgeRegistries.BLOCKS.getKey(gate);
            if (gateRL == null)
            {
                return;
            }
            gateRL = new ResourceLocation(gateRL.getNamespace(), gateRL.getPath().replace("gate_main", "gate_spec"));
            Block replacement = ForgeRegistries.BLOCKS.getValue(gateRL);
            if (replacement == null)
            {
                return;
            }
            if (slab != null && ! (slab.equals(Blocks.SMOOTH_STONE_SLAB)) && ! ((GateBlock_V2) replacement).slabMatches(slab))
            {
                event.getEntity().displayClientMessage(Constants.Messages.MESSAGE_SLAB_TYPE, true);
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            BlockPos above = event.getHitVec().getBlockPos().above();
            if (! event.getLevel().getBlockState(above).canBeReplaced())
            {
                event.getEntity().displayClientMessage(Constants.Messages.MESSAGE_NO_ROOM, true);
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            if (! event.getLevel().isClientSide)
            {
                BlockPlaceContext context = new BlockPlaceContext(event.getEntity(), event.getHand(), ItemStack.EMPTY.copy(), event.getHitVec().withPosition(event.getHitVec().getBlockPos().below()));
                BlockState newState = replacement.getStateForPlacement(context)
                        .setValue(GateBlock_V2.BLOCK_BELOW, GateBlock.blockToStateIndex(slab, rail));
                event.getLevel().setBlockAndUpdate(event.getHitVec().getBlockPos(), newState);
            }
            if (! event.getEntity().isCreative())
            {
                event.getItemStack().shrink(1);
            }
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
            event.setCanceled(true);
        }
        ////////////////////////////////////////////////////////////////////
        if (event.getItemStack().is(Items.RAIL) || event.getItemStack().is(Items.POWERED_RAIL))
        {
            if (! event.getLevel().isClientSide && event.getFace() != null && event.getFace().equals(Direction.UP))
            {
                BlockState above = event.getLevel().getBlockState(event.getHitVec().getBlockPos().above());
                if (above.getBlock() instanceof GateBlock gate)
                {
                    ResourceLocation gateRL = ForgeRegistries.BLOCKS.getKey(gate);
                    if (gateRL == null)
                    {
                        return;
                    }
                    gateRL = new ResourceLocation(gateRL.getNamespace(), gateRL.getPath().replace("gate_main", "gate_spec"));
                    Block replacement = ForgeRegistries.BLOCKS.getValue(gateRL);
                    if (replacement == null)
                    {
                        return;
                    }

                    BlockState newState = replacement.withPropertiesOf(above)
                             .setValue(GateBlock_V2.BLOCK_BELOW, event.getItemStack().is(Items.POWERED_RAIL) ? GateBlock_V2.ON_POWERED_RAIL : GateBlock_V2.ON_REGULAR_RAIL);
                    event.getLevel().setBlockAndUpdate(event.getHitVec().getBlockPos().above(), newState);
                    if (! event.getEntity().isCreative())
                    {
                        event.getItemStack().shrink(1);
                    }
                    event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
                    event.setCanceled(true);
                }
            }
        }
    }
}

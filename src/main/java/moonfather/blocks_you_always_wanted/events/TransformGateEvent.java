package moonfather.blocks_you_always_wanted.events;

import moonfather.blocks_you_always_wanted.Constants;
import moonfather.blocks_you_always_wanted.blocks.GateBlock;
import moonfather.blocks_you_always_wanted.blocks.GateRaisedBlock;
import moonfather.blocks_you_always_wanted.initialization.RegistrationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

@Mod.EventBusSubscriber
public class TransformGateEvent
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (! (event.getItemStack().getItem() instanceof BlockItem bi))
        {
            return;
        }
        BlockState state = event.getLevel().getBlockState(event.getHitVec().getBlockPos());
        if (event.getItemStack() != ItemStack.EMPTY
                && event.getFace() != null && event.getFace().equals(Direction.UP)
                && event.getItemStack().is(ItemTags.FENCE_GATES))
        {
            if (! (bi.getBlock() instanceof GateBlock gate))
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
            Block replacement = GateBlock.toRaisedGate(gate);
            if (slab != null && ! (slab.equals(Blocks.SMOOTH_STONE_SLAB)) && ! ((GateRaisedBlock) replacement).slabMatches(slab))
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
                        .setValue(GateRaisedBlock.BLOCK_BELOW, GateBlock.blockToStateIndex(slab, rail));
                event.getLevel().setBlockAndUpdate(event.getHitVec().getBlockPos(), newState);
                BlockState pt2 = RegistrationManager.GATE_TECHNICAL.get().withPropertiesOf(newState);
                event.getLevel().setBlockAndUpdate(event.getHitVec().getBlockPos().above(), pt2);
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
                if (event.getLevel().getBlockState(event.getHitVec().getBlockPos()).isFaceSturdy(event.getLevel(), event.getHitVec().getBlockPos(), Direction.UP))
                {
                    BlockState above = event.getLevel().getBlockState(event.getHitVec().getBlockPos().above());
                    Block replacement = null;
                    if (above.getBlock() instanceof GateBlock gate1)
                    {
                        replacement = GateBlock.toRaisedGate(gate1);
                    }
                    else if (above.getBlock() instanceof GateRaisedBlock gate2)
                    {
                        replacement = gate2;
                    }
                    if (replacement != null)
                    {
                        BlockState newState = replacement.withPropertiesOf(above)
                                                         .setValue(GateRaisedBlock.BLOCK_BELOW, event.getItemStack().is(Items.POWERED_RAIL) ? GateRaisedBlock.ON_POWERED_RAIL : GateRaisedBlock.ON_REGULAR_RAIL);
                        event.getLevel().setBlockAndUpdate(event.getHitVec().getBlockPos().above(), newState);
                        event.getLevel().playSound(event.getEntity(), event.getHitVec().getBlockPos(), SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, event.getLevel().getRandom().nextFloat() * 0.1F + 0.9F);
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
        ////////////////////////////////////////////////////////////////////
        if (event.getItemStack().is(ItemTags.SLABS))
        {
            if (! event.getLevel().isClientSide && event.getFace() != null && event.getFace().equals(Direction.UP))
            {
                BlockState above = event.getLevel().getBlockState(event.getHitVec().getBlockPos().above());
                BlockState newState = null;
                if (above.getBlock() instanceof GateBlock gate)
                {
                    Block replacement = GateBlock.toRaisedGate(gate);
                    newState = replacement.withPropertiesOf(above)
                                          .setValue(GateRaisedBlock.BLOCK_BELOW, event.getItemStack().is(Items.SMOOTH_STONE_SLAB) ? GateRaisedBlock.ON_STONE_SLAB : GateRaisedBlock.ON_WOODEN_SLAB);
                }
                if (above.getBlock() instanceof GateRaisedBlock gate2 && above.getValue(GateRaisedBlock.BLOCK_BELOW).equals(0))
                {
                    newState = above.setValue(GateRaisedBlock.BLOCK_BELOW, event.getItemStack().is(Items.SMOOTH_STONE_SLAB) ? GateRaisedBlock.ON_STONE_SLAB : GateRaisedBlock.ON_WOODEN_SLAB);
                }
                if (newState != null && ! bi.getBlock().equals(Blocks.SMOOTH_STONE_SLAB) && ! ((GateRaisedBlock) newState.getBlock()).slabMatches(bi.getBlock()))
                {
                    event.getEntity().displayClientMessage(Constants.Messages.MESSAGE_SLAB_TYPE, true);
                    event.setCancellationResult(InteractionResult.FAIL);
                    event.setCanceled(true);
                    return;
                }
                if (newState != null)
                {
                    event.getLevel().setBlockAndUpdate(event.getHitVec().getBlockPos().above(), newState);
                    event.getLevel().playSound(event.getEntity(), event.getHitVec().getBlockPos(), SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, event.getLevel().getRandom().nextFloat() * 0.1F + 0.9F);
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

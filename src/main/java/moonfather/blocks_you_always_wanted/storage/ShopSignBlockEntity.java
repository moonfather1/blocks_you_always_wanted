package moonfather.blocks_you_always_wanted.storage;

import moonfather.blocks_you_always_wanted.initialization.RegistrationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ShopSignBlockEntity extends BlockEntity
{
    public ShopSignBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(RegistrationManager.SIGN_BE.get(), blockPos, blockState);
    }

    private ItemStack item = ItemStack.EMPTY;
    private boolean waxed = false;

    //////////////////////////////////////////////////

    @Override
    protected void loadAdditional(CompoundTag mainTag, HolderLookup.Provider registries)
    {
        super.loadAdditional(mainTag, registries);
        CompoundTag itemTag = mainTag.getCompound("item1");
        this.item = ItemStack.parseOptional(registries, itemTag);
        this.waxed = mainTag.getBoolean("isWaxed");
    }

    @Override
    protected void saveAdditional(CompoundTag mainTag, HolderLookup.Provider registries)
    {
        super.saveAdditional(mainTag, registries);
        this.saveInternal(mainTag, registries);
    }

    private CompoundTag saveInternal(CompoundTag compoundTag, HolderLookup.Provider registries)
    {
        if (! this.item.isEmpty())
        {
            compoundTag.put("item1", this.item.save(registries));
        }
        else
        {
            compoundTag.remove("item1");
        }
        compoundTag.putBoolean("isWaxed", this.waxed);
        return compoundTag;
    }

    ///////////////////////

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        return this.saveInternal(new CompoundTag(), registries); //send to client
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }

    ///////////////////////////////////////////

    public ItemStack getItem()
    {
        return this.item;
    }

    public void setItem(ItemStack itemStack)
    {
        this.item = itemStack.copy();
        if (this.item.has(DataComponents.ENCHANTMENTS))
        {
            this.item.remove(DataComponents.ENCHANTMENTS);
        }
        this.setChanged();
    }

    public void setWaxed(boolean waxed)
    {
        this.waxed = waxed;
        this.setChanged();
    }

    public boolean isWaxed()
    {
        return this.waxed;
    }
}

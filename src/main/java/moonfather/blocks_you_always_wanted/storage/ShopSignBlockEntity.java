package moonfather.blocks_you_always_wanted.storage;

import moonfather.blocks_you_always_wanted.initialization.RegistrationManager;
import net.minecraft.core.BlockPos;
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
    public void load(CompoundTag mainTag)
    {
        super.load(mainTag);
        CompoundTag itemTag = mainTag.getCompound("item1");
        this.item = ItemStack.of(itemTag);
        this.waxed = mainTag.getBoolean("isWaxed");
    }

    @Override
    protected void saveAdditional(CompoundTag mainTag)
    {
        super.saveAdditional(mainTag);
        this.saveInternal(mainTag);
    }

    private CompoundTag saveInternal(CompoundTag compoundTag)
    {
        compoundTag.put("item1", this.item.save(new CompoundTag()));
        compoundTag.putBoolean("isWaxed", this.waxed);
        return compoundTag;
    }

    ///////////////////////

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.saveInternal(new CompoundTag()); //send to client
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
        if (this.item.hasTag() && this.item.getTag().contains("Enchantments"))
        {
            this.item.getTag().remove("Enchantments");
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

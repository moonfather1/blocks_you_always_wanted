package moonfather.blocks_you_always_wanted.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import moonfather.blocks_you_always_wanted.storage.ShopSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ItemAbilities;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ShopSignRenderer implements BlockEntityRenderer<ShopSignBlockEntity>
{
    private static final Quaternionf ROTATE_Y_180 = Axis.YP.rotationDegrees(180);
    private final BlockEntityRendererProvider.Context context;

    public ShopSignRenderer(BlockEntityRendererProvider.Context context)
    {
        this.context = context;
    }

    //todo: sepia

    // copied from hangingsignrenderer
    @Override
    public void render(ShopSignBlockEntity blockEntity, float p_112308_, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay)
    {
        BlockState blockstate = blockEntity.getBlockState();
        SignBlock signblock = (SignBlock)blockstate.getBlock();
        WoodType woodtype = SignBlock.getWoodType(signblock);
        HangingSignRenderer.HangingSignModel model = this.getModel(woodtype);
        model.evaluateVisibleParts(blockstate);
        poseStack.pushPose();
        this.translateSign(poseStack, blockstate);
        this.renderSign(poseStack, bufferSource, combinedLight, combinedOverlay, woodtype, model);
        this.renderItem(blockEntity, poseStack, bufferSource, combinedLight, combinedOverlay);
        poseStack.popPose();
    }

    // this is where the magic happens
    private void renderItem(ShopSignBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay)
    {
        int renderId = (int) blockEntity.getBlockPos().asLong();
        if (blockEntity.getItem().isEmpty())
        {
            return;
        }
        else if (blockEntity.getItem().is(Items.ANVIL) || blockEntity.getItem().is(Items.GRINDSTONE))
        {
            // anvil. useful for blacksmith shops. yes i know the condition is lacking.
            float size = 0.75f;
            poseStack.scale(size, size, 0.15f);
            poseStack.translate(0.0F, -0.45F, +0.375F);
            Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), renderId);
            poseStack.translate(0.0F, 0.0F, -0.745F);
            Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), renderId);
        }
        else if (blockEntity.getItem().getItem() instanceof BlockItem && Minecraft.getInstance().getItemRenderer().getModel(blockEntity.getItem(), blockEntity.getLevel(), null, combinedLight).isGui3d())
        {
            // blocks, but only 3d ones. cauldrons for example are excluded by second condition.
//            float size = 0.55f;
//            poseStack.scale(size, size, size*0.7f);
//            poseStack.translate(0.0F, -0.6F, -0.001F);
//            Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), renderId);
            float size = 0.75f;
            poseStack.scale(size, size, 0.15f);
            poseStack.translate(0.0F, -0.45, -0.325F); // front
            Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), renderId);
            poseStack.translate(0.0F, 0.0F, +0.605F); // back
            poseStack.mulPose(ROTATE_Y_180);
            Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), renderId);
        }
        else if (blockEntity.getItem().canPerformAction(ItemAbilities.SHIELD_BLOCK))
        {
            // shield
            poseStack.scale(0.5f, 0.5f, 0.3f);
            poseStack.translate(0.0F, -0.70F, -0.125F);
            Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), renderId);
            poseStack.mulPose(ROTATE_Y_180);
            poseStack.translate(0.0F, 0.0F, -0.255F);
            Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), renderId);
        }
        else if (Minecraft.getInstance().getItemRenderer().getModel(blockEntity.getItem(), blockEntity.getLevel(), null, combinedLight).isGui3d())
        {
            // trident, shield (handled above)...
            float size = 0.55f;
            poseStack.scale(size, size, 2.7f);
            poseStack.translate(0.0F, -0.5F, -0.001F);
            Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), renderId);
        }
        else
        {
            // normal items
            float size = 0.45f;
            poseStack.scale(size, size, 0.3f);
            poseStack.translate(0.0F, -0.70F, -0.205F);
            Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), renderId);
            poseStack.mulPose(ROTATE_Y_180);
            poseStack.translate(0.0F, 0.00F, -0.405F);
            Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), renderId);
        }
    }

    // copied from signrenderer
    private void translateSign(PoseStack poseStack, BlockState blockState)
    {
        poseStack.translate(0.5D, 0.9375D, 0.5D);
        poseStack.mulPose(getRotation(blockState));
        poseStack.translate(0.0F, -0.3125F, 0.0F);
    }

    // copied from signrenderer
    private void renderSign(PoseStack p_279104_, MultiBufferSource p_279408_, int combinedLight, int combinedOverlay, WoodType p_279170_, Model p_279159_)
    {
        p_279104_.pushPose();
        float f = this.getSignModelRenderScale();
        p_279104_.scale(f, -f, -f);
        Material material = this.getSignMaterial(p_279170_);
        VertexConsumer vertexconsumer = material.buffer(p_279408_, p_279159_::renderType);
        this.renderSignModel(p_279104_, combinedLight, combinedOverlay, p_279159_, vertexconsumer);
        p_279104_.popPose();
    }

    // copied from hangingsignrenderer
    private void renderSignModel(PoseStack p_251159_, int p_249874_, int p_249794_, Model p_248746_, VertexConsumer p_249165_)
    {
        HangingSignRenderer.HangingSignModel hangingsignrenderer$hangingsignmodel = (HangingSignRenderer.HangingSignModel)p_248746_;
        hangingsignrenderer$hangingsignmodel.root.render(p_251159_, p_249165_, p_249874_, p_249794_);
    }

    // copied from hangingsignrenderer
    public float getSignModelRenderScale() {
        return 1.0F;
    }

    // copied from hangingsignrenderer
    private Material getSignMaterial(WoodType p_251791_) {
        return Sheets.getHangingSignMaterial(p_251791_);
    }

    ///////////////////////****************************

    private final Map<WoodType, HangingSignRenderer.HangingSignModel> hangingSignModelsCache = new HashMap<>();

    private HangingSignRenderer.HangingSignModel getModel(WoodType woodType)
    {
        HangingSignRenderer.HangingSignModel result = hangingSignModelsCache.getOrDefault(woodType, null);
        if (result != null)
        {
            return result;
        }
        result = new HangingSignRenderer.HangingSignModel(this.context.bakeLayer(ModelLayers.createHangingSignModelName(woodType)));
        hangingSignModelsCache.put(woodType, result);
        return result;
    }

    ///////////////////////****************************

    private final Map<Float, Quaternionf> rotationCache = new HashMap<>();

    private Quaternionf getRotation(BlockState blockState)
    {
        float angle = ((SignBlock) blockState.getBlock()).getYRotationDegrees(blockState);
        Quaternionf result = rotationCache.getOrDefault(angle, null);
        if (result != null)
        {
            return result;
        }
        result = Axis.YP.rotationDegrees(-1 * angle);
        rotationCache.put(angle, result);
        return result;
    }

    //////////////////////////////////////////////////


    @Override
    public AABB getRenderBoundingBox(ShopSignBlockEntity blockEntity)
    {
        if (this.renderBox == null || ! blockEntity.getBlockPos().equals(this.lastPos))
        {
            this.renderBox = new net.minecraft.world.phys.AABB(blockEntity.getBlockPos());
            this.lastPos = blockEntity.getBlockPos();
        }
        return renderBox;
    }
    private net.minecraft.world.phys.AABB renderBox = null;
    private BlockPos lastPos = null;
}

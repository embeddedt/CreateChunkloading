package com.grimmauld.createintegration.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import com.simibubi.create.foundation.utility.SuperByteBuffer;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.MathHelper;

public class RollingMachineTileEntityRenderer extends SafeTileEntityRenderer<RollingMachineTile> {

    public RollingMachineTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    protected void renderSafe(RollingMachineTile te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffer, int light,
                              int overlay) {
        renderItems(te, partialTicks, ms, buffer, light, overlay);
        renderShaft(te, ms, buffer, light);
    }


    protected void renderShaft(RollingMachineTile te, MatrixStack ms, IRenderTypeBuffer buffer, int light) {
        KineticTileEntityRenderer.renderRotatingBuffer(te, getRotatedModel(te, Rotation.CLOCKWISE_180), ms, buffer.getBuffer(RenderType.getSolid()), light);
        KineticTileEntityRenderer.renderRotatingBuffer(te, getRotatedModel(te, Rotation.NONE), ms, buffer.getBuffer(RenderType.getSolid()), light);
    }

    private void renderItems(RollingMachineTile te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffer,
                             int light, int overlay) {


        if (!te.inventory.isEmpty()) {
            boolean alongZ = te.getBlockState().get(BlockStateProperties.FACING).getXOffset() == 0;
            ms.push();

            boolean moving = te.inventory.recipeDuration != 0;
            float offset = moving ? te.inventory.remainingTime / te.inventory.recipeDuration : 0;
            if (moving)
                offset = MathHelper.clamp(offset + (-partialTicks + .5f) / te.inventory.recipeDuration, 0, 1);

            if (te.getSpeed() == 0)
                offset = .5f;
            if (te.getSpeed() < 0 ^ alongZ)
                offset = 1 - offset;

            ItemStack stack = te.inventory.getStackInSlot(0);
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            IBakedModel modelWithOverrides = itemRenderer.getItemModelWithOverrides(stack, te.getWorld(), null);
            boolean blockItem = modelWithOverrides.isGui3d();

            ms.translate(alongZ ? offset : .5, blockItem ? .925f : 13f / 16f, alongZ ? .5 : offset);

            ms.scale(.5f, .5f, .5f);
            if (alongZ)
                ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90));
            ms.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
            itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED, light, overlay, ms, buffer);
            ms.pop();
        }
    }

    protected SuperByteBuffer getRotatedModel(KineticTileEntity te, Rotation rot) {
        BlockState state = te.getBlockState();
        return AllBlockPartials.SHAFT_HALF.renderOnDirectional(state.rotate(rot));
    }
}

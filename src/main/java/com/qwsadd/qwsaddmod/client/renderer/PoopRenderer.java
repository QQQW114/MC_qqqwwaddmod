package com.qwsadd.qwsaddmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.entity.PoopEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class PoopRenderer extends EntityRenderer<PoopEntity> {

    private static final ResourceLocation TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(QwsaddModMain.MODID, "textures/entity/poop_texture.png");

    private final PoopModel poopModel;

    public PoopRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.poopModel = new PoopModel(context.bakeLayer(PoopModel.LAYER_LOCATION));
        this.shadowRadius = 0.3F;
    }

    @Override
    public void render(PoopEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();

        // --- 让我们重新开始调试变换 ---
        // 标准的EntityModel渲染流程是这样的：
        // 1. 模型在(0,0,0)被渲染，但它的脚底应该在Y=0。
        // 2. Minecraft的渲染系统会把它整体向上移动1.5个单位，所以模型会悬浮。
        // 3. 同时，模型会是“头朝下”的。
        // 因此，我们需要一个标准的反向变换来修正它。

        pPoseStack.translate(0.0D, 1.5D, 0.0D); // 向上移动，以抵消渲染系统的下移
        pPoseStack.scale(1.0F, -1.0F, -1.0F);   // 沿Y轴翻转（修正头朝下），沿Z轴翻转（修正前后朝向）

        this.poopModel.renderToBuffer(
                pPoseStack,
                pBuffer.getBuffer(this.poopModel.renderType(getTextureLocation(pEntity))),
                pPackedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F
        );

        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PoopEntity pEntity) {
        return TEXTURE_LOCATION;
    }
}
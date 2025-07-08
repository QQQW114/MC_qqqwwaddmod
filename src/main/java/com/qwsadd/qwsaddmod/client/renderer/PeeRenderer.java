// 文件路径: com/qwsadd/qwsaddmod/client/renderer/PeeRenderer.java
package com.qwsadd.qwsaddmod.client.renderer;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.entity.PeeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class PeeRenderer extends EntityRenderer<PeeEntity> {

    // 【优化建议】修复过时警告
    private static final ResourceLocation TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(QwsaddModMain.MODID, "textures/entity/pee_texture.png");

    private final PeeModel peeModel;

    public PeeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.peeModel = new PeeModel(context.bakeLayer(PeeModel.LAYER_LOCATION));
        this.shadowRadius = 0.4F; // 给扁平的实体一个阴影
    }

    @Override
    public void render(PeeEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        // 【严重修复】将 super.render() 移动到所有 PoseStack 操作之前！
        // 这可以确保阴影等基础渲染在正确的坐标空间中完成，避免矩阵冲突。
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);

        pPoseStack.pushPose();

        // 标准变换，用于正确渲染来自 Blockbench 的模型
        pPoseStack.translate(0.0D, 1.5D, 0.0D);
        pPoseStack.scale(1.0F, -1.0F, -1.0F);

        this.peeModel.renderToBuffer(
                pPoseStack,
                pBuffer.getBuffer(this.peeModel.renderType(getTextureLocation(pEntity))),
                pPackedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F
        );

        pPoseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(PeeEntity pEntity) {
        return TEXTURE_LOCATION;
    }
}

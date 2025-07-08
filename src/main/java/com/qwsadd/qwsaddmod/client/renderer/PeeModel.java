// 文件路径: com/qwsadd/qwsaddmod/client/renderer/PeeModel.java
package com.qwsadd.qwsaddmod.client.renderer;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.entity.PeeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

// 【最终修复】此类现在完全匹配你从 Blockbench 导出的模型
public class PeeModel extends EntityModel<PeeEntity> {

    // 【关键】LayerLocation 保持不变，它在 ClientSetup 中被正确注册
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(QwsaddModMain.MODID, "pee_model"), "main");

    // 【关键】根部件的名称必须与 Blockbench 导出的代码一致
    private final ModelPart bb_main;

    public PeeModel(ModelPart root) {
        // 【关键】获取名为 "bb_main" 的子部件
        this.bb_main = root.getChild("bb_main");
    }

    // 【关键】这个方法现在精确地定义了你在 Blockbench 中制作的模型的形状和位置
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 【关键】将 Blockbench 导出的模型定义代码复制到这里
        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-7.0F, -1.0F, -7.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        // 【关键】纹理大小，确保和你的 pee_texture.png 尺寸匹配
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(PeeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // 无动画
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // 【关键】渲染名为 "bb_main" 的根部件
        bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

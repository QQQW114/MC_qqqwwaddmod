package com.qwsadd.qwsaddmod.client.renderer;

import com.qwsadd.qwsaddmod.QwsaddModMain; // 导入你的主类
import com.qwsadd.qwsaddmod.entity.PoopEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

// 类名和泛型保持我们自己的
public class PoopModel extends EntityModel<PoopEntity> {

    // 【重要】我们继续使用这个规范的 Layer Location 定义
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(QwsaddModMain.MODID, "poop_model"), "main");

    // 【已改回】根部件的名字现在是 "bone"，与新模型一致
    private final ModelPart bone;

    public PoopModel(ModelPart root) {
        // 【已改回】我们需要获取名为 "bone" 的子部件
        // 注意：新模型数据里，根部件的名字叫 "bone"。
        // 但在注册时，我们给整个模型层定义了一个子部件"main"。
        // Forge会自动把我们定义的"bone"包裹在"main"里。
        // 所以我们这里应该获取的是我们自己定义的根部件"bone"。
        this.bone = root.getChild("bone");
    }

    // 【关键】使用你提供的、正确的模型数据
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 【已改回】这里是正确的“一坨”模型代码！
        // 根部件的名字是 "bone"
        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create()
                        .texOffs(0, 14).addBox(-13.0F, -3.0F, 3.0F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-14.0F, -2.0F, 2.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 25).addBox(-12.0F, -4.0F, 4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 25).addBox(-11.0F, -5.0F, 5.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 32).addBox(-9.0F, -7.0F, 7.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(6, 29).addBox(-10.0F, -6.0F, 6.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(8.0F, 24.0F, -8.0F));

        // 纹理贴图大小保持 64x64
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(PoopEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // 无动画
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // 【已改回】渲染名为 "bone" 的根部件
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
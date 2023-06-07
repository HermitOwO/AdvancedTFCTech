package com.hermitowo.advancedtfctech.client.model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class PowerLoomParts extends Model
{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(MOD_ID, "powerloomparts"), "main");
    private final ModelPart powerloom;
    public final ModelPart rack;
    public final ModelPart rack_side;
    public final ModelPart rack2;
    public final ModelPart rack3;
    public final ModelPart rod;
    public final ModelPart holder;
    public final ModelPart fiber_pirn;
    public final ModelPart wool_pirn;

    public PowerLoomParts(ModelPart root)
    {
        super(RenderType::entitySolid);
        this.powerloom = root.getChild("powerloom");
        this.rack = root.getChild("rack");
        this.rack_side = root.getChild("rack_side");
        this.rack2 = root.getChild("rack2");
        this.rack3 = root.getChild("rack3");
        this.rod = root.getChild("rod");
        this.holder = root.getChild("holder");
        this.fiber_pirn = root.getChild("fiber_pirn");
        this.wool_pirn = root.getChild("wool_pirn");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition powerloom = partdefinition.addOrReplaceChild("powerloom", CubeListBuilder.create(), PartPose.offset(-31.0F, 24.0F, 31.0F));

        PartDefinition rack = partdefinition.addOrReplaceChild("rack", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition rackteeth = rack.addOrReplaceChild("rackteeth", CubeListBuilder.create().texOffs(338, 88).addBox(-69.5F, -27.0F, 11.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(338, 88).addBox(-69.5F, -27.0F, 9.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(338, 88).addBox(-69.5F, -27.0F, 5.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(338, 88).addBox(-69.5F, -27.0F, 7.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(338, 88).addBox(-69.5F, -27.0F, -3.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(338, 88).addBox(-69.5F, -27.0F, -1.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(338, 88).addBox(-69.5F, -27.0F, 1.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(338, 88).addBox(-69.5F, -27.0F, 3.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(338, 88).addBox(-69.5F, -27.0F, -19.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(338, 88).addBox(-69.5F, -27.0F, -17.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(338, 88).addBox(-69.5F, -27.0F, -15.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(338, 88).addBox(-69.5F, -27.0F, -13.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(338, 88).addBox(-69.5F, -27.0F, -5.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(338, 88).addBox(-69.5F, -27.0F, -7.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(338, 88).addBox(-69.5F, -27.0F, -11.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(338, 88).addBox(-69.5F, -27.0F, -9.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(93.5F, -3.0F, -19.5F));

        PartDefinition woodrack = rack.addOrReplaceChild("woodrack", CubeListBuilder.create().texOffs(165, 20).addBox(-88.5F, -23.0F, -48.0F, 5.0F, 3.0F, 62.0F, new CubeDeformation(0.0F))
            .texOffs(301, 103).mirror().addBox(-87.5F, -30.0F, -47.0F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
            .texOffs(282, 0).addBox(-88.0F, -35.0F, -48.0F, 5.0F, 5.0F, 62.0F, new CubeDeformation(0.0F)), PartPose.offset(111.0F, 0.0F, 0.0F));

        PartDefinition rack_side = partdefinition.addOrReplaceChild("rack_side", CubeListBuilder.create().texOffs(301, 103).addBox(-38.0F, -10.0F, -3.0F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-49.5F, -20.0F, 13.0F));

        PartDefinition rack2 = partdefinition.addOrReplaceChild("rack2", CubeListBuilder.create().texOffs(263, 77).addBox(19.0F, -46.0F, -41.0F, 1.0F, 3.0F, 36.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition rack2teeth = rack2.addOrReplaceChild("rack2teeth", CubeListBuilder.create().texOffs(345, 77).addBox(-26.0F, -18.0F, 42.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 40.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 36.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 38.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 44.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 46.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 48.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 50.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 60.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 62.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 64.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 66.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 52.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 54.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 56.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 58.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(45.0F, -25.0F, -74.0F));

        PartDefinition rack3 = partdefinition.addOrReplaceChild("rack3", CubeListBuilder.create().texOffs(263, 77).addBox(14.75F, -39.0F, -41.0F, 1.0F, 3.0F, 36.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition rack3teeth = rack3.addOrReplaceChild("rack3teeth", CubeListBuilder.create().texOffs(345, 77).addBox(-26.0F, -18.0F, 58.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 42.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 40.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 36.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 38.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 44.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 46.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 48.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 50.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 60.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 62.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 64.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 66.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 52.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 54.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(345, 77).addBox(-26.0F, -18.0F, 56.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(40.75F, -18.0F, -75.0F));

        PartDefinition rod = partdefinition.addOrReplaceChild("rod", CubeListBuilder.create().texOffs(384, 71).mirror().addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 38.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition holder = partdefinition.addOrReplaceChild("holder", CubeListBuilder.create().texOffs(267, 97).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition fiber_pirn = partdefinition.addOrReplaceChild("fiber_pirn", CubeListBuilder.create().texOffs(394, 51).addBox(3.0F, -1.0F, 0.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));
        PartDefinition wool_pirn = partdefinition.addOrReplaceChild("wool_pirn", CubeListBuilder.create().texOffs(394, 40).addBox(3.0F, -1.0F, 0.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        return LayerDefinition.create(meshdefinition, 512, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        powerloom.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
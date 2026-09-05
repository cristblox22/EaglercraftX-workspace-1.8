package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class ModelCrocodile extends ModelBase {
    public ModelRenderer body;
    public ModelRenderer left_leg;
    public ModelRenderer left_foot;
    public ModelRenderer right_leg;
    public ModelRenderer right_foot;
    public ModelRenderer left_arm;
    public ModelRenderer left_hand;
    public ModelRenderer right_arm;
    public ModelRenderer right_hand;
    public ModelRenderer tail1;
    public ModelRenderer tail2;
    public ModelRenderer tail3;
    public ModelRenderer neck;
    public ModelRenderer head;
    public ModelRenderer crown;
    public ModelRenderer left_upperteeth;
    public ModelRenderer right_upperteeth;
    public ModelRenderer jaw;
    public ModelRenderer left_lowerteeth;
    public ModelRenderer right_lowerteeth;

    public ModelCrocodile() {
        this.textureWidth = 256;
        this.textureHeight = 256;

        body = new ModelRenderer(this, 0, 0);
        body.setRotationPoint(0.0F, 15.0F, 0.0F);
        body.addBox(-8.0F, -7.0F, -13.0F, 16, 12, 27);

        left_leg = new ModelRenderer(this, 0, 0);
        left_leg.setRotationPoint(8.0F, 3.0F, 10.0F);
        left_leg.addBox(-2.0F, -2.0F, -5.0F, 5, 8, 8);
        body.addChild(left_leg);

        left_foot = new ModelRenderer(this, 45, 42);
        left_foot.setRotationPoint(2.0F, 6.0F, -3.0F);
        left_foot.addBox(-2.0F, -0.01F, -5.0F, 5, 0, 6);
        left_leg.addChild(left_foot);

        right_leg = new ModelRenderer(this, 0, 0);
        right_leg.mirror = true;
        right_leg.setRotationPoint(-8.0F, 3.0F, 10.0F);
        right_leg.addBox(-3.0F, -2.0F, -5.0F, 5, 8, 8);
        body.addChild(right_leg);

        right_foot = new ModelRenderer(this, 45, 42);
        right_foot.mirror = true;
        right_foot.setRotationPoint(-2.0F, 6.0F, -3.0F);
        right_foot.addBox(-3.0F, -0.01F, -5.0F, 5, 0, 6);
        right_leg.addChild(right_foot);

        left_arm = new ModelRenderer(this, 0, 40);
        left_arm.setRotationPoint(9.0F, 1.0F, -9.0F);
        left_arm.addBox(-2.0F, -2.0F, -2.0F, 4, 10, 4);
        body.addChild(left_arm);

        left_hand = new ModelRenderer(this, 0, 17);
        left_hand.setRotationPoint(0.0F, 8.0F, 1.0F);
        left_hand.addBox(-2.0F, -0.01F, -7.0F, 6, 0, 7);
        left_arm.addChild(left_hand);

        right_arm = new ModelRenderer(this, 0, 40);
        right_arm.mirror = true;
        right_arm.setRotationPoint(-9.0F, 1.0F, -9.0F);
        right_arm.addBox(-2.0F, -2.0F, -2.0F, 4, 10, 4);
        body.addChild(right_arm);

        right_hand = new ModelRenderer(this, 0, 17);
        right_hand.mirror = true;
        right_hand.setRotationPoint(0.0F, 8.0F, 1.0F);
        right_hand.addBox(-4.0F, -0.01F, -7.0F, 6, 0, 7);
        right_arm.addChild(right_hand);

        tail1 = new ModelRenderer(this, 0, 40);
        tail1.setRotationPoint(0.0F, 0.0F, 16.0F);
        tail1.addBox(-5.0F, -5.0F, -2.0F, 10, 10, 24);
        tail1.setTextureOffset(45, 51);
        tail1.addBox(-5.0F, -7.0F, -2.0F, 10, 2, 24);
        body.addChild(tail1);

        tail2 = new ModelRenderer(this, 62, 15);
        tail2.setRotationPoint(0.0F, 1.0F, 24.0F);
        tail2.addBox(-3.0F, -3.0F, -2.0F, 6, 7, 25);
        tail2.setTextureOffset(43, 78);
        tail2.addBox(-2.0F, -5.0F, -2.0F, 4, 2, 20);
        tail1.addChild(tail2);

        tail3 = new ModelRenderer(this, 0, 75);
        tail3.setRotationPoint(0.0F, 0.0F, 18.0F);
        tail3.addBox(0.0F, -6.0F, 0.0F, 0, 10, 21);
        tail2.addChild(tail3);

        neck = new ModelRenderer(this, 80, 89);
        neck.setRotationPoint(0.0F, 0.0F, -15.0F);
        neck.addBox(-6.0F, -5.0F, -10.0F, 12, 10, 12);
        neck.setTextureOffset(60, 0);
        neck.addBox(-4.0F, -6.0F, -10.0F, 8, 1, 12);
        body.addChild(neck);

        head = new ModelRenderer(this, 72, 78);
        head.setRotationPoint(0.0F, 1.0F, -11.0F);
        head.addBox(-5.0F, -4.0F, -5.0F, 10, 4, 6);
        head.setTextureOffset(60, 14);
        head.addBox(-4.0F, -5.0F, -5.0F, 8, 1, 5);
        head.setTextureOffset(22, 78);
        head.addBox(-3.0F, -4.0F, -17.0F, 6, 4, 12);
        neck.addChild(head);

        crown = new ModelRenderer(this, 49, 54);
        crown.setRotationPoint(0.0F, -5.0F, -2.0F);
        crown.addBox(-1.5F, -5.0F, -2.0F, 3, 5, 3);
        head.addChild(crown);

        left_upperteeth = new ModelRenderer(this, 104, 23);
        left_upperteeth.setRotationPoint(0.0F, 0.0F, -17.0F);
        left_upperteeth.addBox(0.0F, 0.0F, -0.025F, 3, 2, 11);
        left_upperteeth.rotateAngleZ = -0.0873F;
        head.addChild(left_upperteeth);

        right_upperteeth = new ModelRenderer(this, 104, 23);
        right_upperteeth.mirror = true;
        right_upperteeth.setRotationPoint(0.0F, 0.0F, -17.0F);
        right_upperteeth.addBox(-3.0F, 0.0F, -0.025F, 3, 2, 11);
        right_upperteeth.rotateAngleZ = 0.0873F;
        head.addChild(right_upperteeth);

        jaw = new ModelRenderer(this, 100, 7);
        jaw.setRotationPoint(0.0F, 0.0F, 0.0F);
        jaw.addBox(-5.5F, -2.0F, -6.0F, 11, 5, 7);
        jaw.setTextureOffset(90, 48);
        jaw.addBox(-3.0F, 0.0F, -17.0F, 6, 3, 11);
        head.addChild(jaw);

        left_lowerteeth = new ModelRenderer(this, 105, 67);
        left_lowerteeth.setRotationPoint(0.0F, 0.0F, -17.0F);
        left_lowerteeth.addBox(0.0F, -2.0F, -0.025F, 3, 2, 11);
        left_lowerteeth.rotateAngleZ = 0.0873F;
        jaw.addChild(left_lowerteeth);

        right_lowerteeth = new ModelRenderer(this, 105, 67);
        right_lowerteeth.mirror = true;
        right_lowerteeth.setRotationPoint(0.0F, 0.0F, -17.0F);
        right_lowerteeth.addBox(-3.0F, -2.0F, -0.025F, 3, 2, 11);
        right_lowerteeth.rotateAngleZ = -0.0873F;
        jaw.addChild(right_lowerteeth);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        
        if (this.isChild) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
            body.render(scale);
            GlStateManager.popMatrix();
        } else {
            body.render(scale);
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, Entity entityIn) {
        float walkSpeed = 0.7F;
        float walkDegree = 0.7F;

        neck.rotateAngleY = netHeadYaw * 0.0174533F;
        neck.rotateAngleX = headPitch * 0.0174533F;

        left_leg.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        right_leg.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
        left_arm.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
        right_arm.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;

        tail1.rotateAngleY = MathHelper.cos(limbSwing * walkSpeed * 0.5F) * walkDegree * limbSwingAmount;
        tail2.rotateAngleY = MathHelper.cos(limbSwing * walkSpeed * 0.5F - 1.0F) * walkDegree * limbSwingAmount;
        tail3.rotateAngleY = MathHelper.cos(limbSwing * walkSpeed * 0.5F - 2.0F) * walkDegree * limbSwingAmount;
    }
}
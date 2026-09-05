package net.minecraft.client.model;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelRaccoon extends ModelBase {
	public ModelRenderer body;
	public ModelRenderer tail;
	public ModelRenderer armLeft;
	public ModelRenderer armRight;
	public ModelRenderer legLeft;
	public ModelRenderer legRight;
	public ModelRenderer head;
	public ModelRenderer earLeft;
	public ModelRenderer earRight;
	public ModelRenderer snout;

	public ModelRaccoon() {
		this.textureWidth = 64;
		this.textureHeight = 64;

		body = new ModelRenderer(this, 0, 0);
		body.setRotationPoint(0.0F, 13.0F, 0.5F);
		body.addBox(-5.5F, -4.0F, -7.5F, 11, 8, 15, 0.0F);

		tail = new ModelRenderer(this, 0, 24);
		tail.setRotationPoint(0.5F, -1.0F, 7.5F);
		tail.addBox(-3.0F, -2.0F, 0.0F, 5, 5, 19, 0.0F);
		body.addChild(tail);

		armLeft = new ModelRenderer(this, 0, 24);
		armLeft.setRotationPoint(3.0F, 4.0F, -5.5F);
		armLeft.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
		body.addChild(armLeft);

		armRight = new ModelRenderer(this, 0, 24);
		armRight.mirror = true;
		armRight.setRotationPoint(-3.0F, 4.0F, -5.5F);
		armRight.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
		body.addChild(armRight);

		legLeft = new ModelRenderer(this, 9, 32);
		legLeft.setRotationPoint(3.0F, 4.0F, 6.5F);
		legLeft.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
		body.addChild(legLeft);

		legRight = new ModelRenderer(this, 9, 32);
		legRight.mirror = true;
		legRight.setRotationPoint(-3.0F, 4.0F, 6.5F);
		legRight.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
		body.addChild(legRight);

		head = new ModelRenderer(this, 30, 30);
		head.setRotationPoint(0.0F, 0.5F, -8.5F);
		head.addBox(-4.5F, -4.0F, -4.0F, 9, 7, 5, 0.0F);
		body.addChild(head);

		earLeft = new ModelRenderer(this, 9, 24);
		earLeft.setRotationPoint(3.5F, -4.0F, -2.0F);
		earLeft.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 1, 0.0F);
		head.addChild(earLeft);

		earRight = new ModelRenderer(this, 9, 24);
		earRight.mirror = true;
		earRight.setRotationPoint(-3.5F, -4.0F, -2.0F);
		earRight.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 1, 0.0F);
		head.addChild(earRight);

		snout = new ModelRenderer(this, 0, 0);
		snout.setRotationPoint(0.0F, 1.5F, -5.0F);
		snout.addBox(-2.0F, -1.5F, -2.0F, 4, 3, 3, 0.0F);
		head.addChild(snout);
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

		if (this.isChild) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.translate(0.0F, 2.0F * (1F / scale), 0.0F);
			body.render(scale);
			GlStateManager.popMatrix();
		} else {
			body.render(scale);
		}
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, Entity entityIn) {
		head.rotateAngleY = netHeadYaw * 0.0174533F;
		head.rotateAngleX = headPitch * 0.0174533F;

		legLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		legRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		armLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		armRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
	}
}
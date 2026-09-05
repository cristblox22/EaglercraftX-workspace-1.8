package net.minecraft.client.model;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelGrizzlyBear extends ModelBase {
	public ModelRenderer body;
	public ModelRenderer midbody;
	public ModelRenderer head;
	public ModelRenderer snout;
	public ModelRenderer leftEar;
	public ModelRenderer rightEar;
	public ModelRenderer leftLeg;
	public ModelRenderer rightLeg;
	public ModelRenderer leftArm;
	public ModelRenderer rightArm;
	public ModelRenderer hatBase;
	public ModelRenderer hatTop;
	public ModelRenderer microphoneStick;
	public ModelRenderer microphoneHead;

	public ModelGrizzlyBear() {
		this.textureWidth = 128;
		this.textureHeight = 128;

		body = new ModelRenderer(this, 0, 0);
		body.setRotationPoint(0.0F, 5.0F, 6.0F);
		body.addBox(-7.0F, -7.0F, -19.0F, 14, 15, 28, 0.0F);
		body.setTextureOffset(0, 44);
		body.addBox(-6.0F, 8.0F, -19.0F, 12, 3, 28, 0.0F);

		midbody = new ModelRenderer(this, 27, 99);
		midbody.setRotationPoint(0.0F, 0.5F, -4.0F);
		midbody.addBox(-8.0F, -8.5F, -6.0F, 16, 17, 12, 0.1F);
		body.addChild(midbody);

		head = new ModelRenderer(this, 57, 0);
		head.setRotationPoint(0.0F, -0.8F, -21.0F);
		head.addBox(-5.0F, -5.0F, -6.0F, 10, 10, 8, 0.0F);
		body.addChild(head);

		snout = new ModelRenderer(this, 0, 17);
		snout.setRotationPoint(0.0F, 0.0F, -6.0F);
		snout.addBox(-2.0F, 0.0F, -5.0F, 4, 5, 5, 0.0F);
		head.addChild(snout);

		leftEar = new ModelRenderer(this, 14, 17);
		leftEar.setRotationPoint(3.5F, -5.0F, -3.0F);
		leftEar.addBox(-1.5F, -2.0F, -1.0F, 3, 2, 2, 0.0F);
		head.addChild(leftEar);

		rightEar = new ModelRenderer(this, 14, 17);
		rightEar.mirror = true;
		rightEar.setRotationPoint(-3.5F, -5.0F, -3.0F);
		rightEar.addBox(-1.5F, -2.0F, -1.0F, 3, 2, 2, 0.0F);
		head.addChild(rightEar);

		leftLeg = new ModelRenderer(this, 0, 76);
		leftLeg.setRotationPoint(3.8F, 8.0F, 4.0F);
		leftLeg.addBox(-3.0F, 0.0F, -3.0F, 6, 11, 8, 0.0F);
		body.addChild(leftLeg);

		rightLeg = new ModelRenderer(this, 0, 76);
		rightLeg.mirror = true;
		rightLeg.setRotationPoint(-3.8F, 8.0F, 4.0F);
		rightLeg.addBox(-3.0F, 0.0F, -3.0F, 6, 11, 8, 0.0F);
		body.addChild(rightLeg);

		leftArm = new ModelRenderer(this, 74, 78);
		leftArm.setRotationPoint(4.5F, 4.0F, -13.0F);
		leftArm.addBox(-3.0F, -3.0F, -3.0F, 6, 18, 7, 0.0F);
		body.addChild(leftArm);

		rightArm = new ModelRenderer(this, 74, 78);
		rightArm.mirror = true;
		rightArm.setRotationPoint(-4.5F, 4.0F, -13.0F);
		rightArm.addBox(-3.0F, -3.0F, -3.0F, 6, 18, 7, 0.0F);
		body.addChild(rightArm);

		hatBase = new ModelRenderer(this, 0, 57);
		hatBase.setRotationPoint(0.0F, -5.0F, -4.0F);
		hatBase.addBox(-3.0F, -1.0F, -1.0F, 6, 1, 6, 0.0F);
		head.addChild(hatBase);

		hatTop = new ModelRenderer(this, 0, 48);
		hatTop.setRotationPoint(0.0F, -5.0F, -4.0F);
		hatTop.addBox(-2.0F, -5.0F, 0.0F, 4, 4, 4, 0.0F);
		head.addChild(hatTop);

		microphoneStick = new ModelRenderer(this, 0, 0);
		microphoneStick.setRotationPoint(0.0F, 13.0F, -3.0F);
		microphoneStick.addBox(-1.0F, -1.0F, -4.0F, 2, 2, 5, 0.0F);
		rightArm.addChild(microphoneStick);

		microphoneHead = new ModelRenderer(this, 15, 0);
		microphoneHead.setRotationPoint(0.0F, 13.0F, -3.0F);
		microphoneHead.addBox(-1.5F, -1.5F, -6.0F, 3, 3, 3, 0.0F);
		rightArm.addChild(microphoneHead);
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

		if (this.isChild) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.35F, 0.35F, 0.35F);
			GlStateManager.translate(0.0F, 2.75F * (1F / scale), 0.125F * (1F / scale));
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

		head.rotateAngleY = netHeadYaw * 0.0174533F;
		head.rotateAngleX = headPitch * 0.0174533F;

		leftLeg.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
		rightLeg.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
		leftArm.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
		rightArm.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;

		body.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed) * walkDegree * 0.2F * limbSwingAmount;

		float attackTicks = getAttackTick(entityIn);
		if (attackTicks > 0.0F) {
			float swing = MathHelper.sin(attackTicks * 0.5F) * 1.2F;
			leftArm.rotateAngleX += swing;
			rightArm.rotateAngleX -= swing;
			body.rotateAngleY = MathHelper.sin(attackTicks * 0.3F) * 0.35F;
		} else {
			body.rotateAngleY = 0.0F;
		}
	}

	private float getAttackTick(Entity entityIn) {
		return 0.0F;
	}
}
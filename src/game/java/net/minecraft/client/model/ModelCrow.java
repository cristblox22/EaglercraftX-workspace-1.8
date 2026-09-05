package net.minecraft.client.model;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityCrow;
import net.minecraft.util.MathHelper;

public class ModelCrow extends ModelBase {
	public ModelRenderer body;
	public ModelRenderer legLeft;
	public ModelRenderer legRight;
	public ModelRenderer wingLeft;
	public ModelRenderer wingRight;
	public ModelRenderer tail;
	public ModelRenderer head;
	public ModelRenderer beak;

	// Base pose rotations (radians) - taken from the original model's fixed offsets
	private static final float BODY_BASE_X = 1.0036F;
	private static final float LEG_BASE_X = 0.5672F;
	private static final float WING_BASE_X = 0.0436F;
	private static final float TAIL_BASE_X = -0.1309F;
	private static final float HEAD_BASE_X = -0.7418F;

	public ModelCrow() {
		this.textureWidth = 32;
		this.textureHeight = 32;

		body = new ModelRenderer(this, 0, 0);
		body.setRotationPoint(0.0F, 21.9F, 0.0F);
		body.addBox(-1.5F, -5.0F, 0.0F, 3, 5, 3, 0.0F);
		body.rotateAngleX = BODY_BASE_X;

		legLeft = new ModelRenderer(this, 0, 17);
		legLeft.setRotationPoint(0.9F, 0.0F, 0.0F);
		legLeft.addBox(-0.5F, -2.0F, -2.0F, 1, 2, 3, 0.0F);
		legLeft.rotateAngleX = LEG_BASE_X;
		body.addChild(legLeft);

		legRight = new ModelRenderer(this, 0, 17);
		legRight.mirror = true;
		legRight.setRotationPoint(-0.9F, 0.0F, 0.0F);
		legRight.addBox(-0.5F, -2.0F, -2.0F, 1, 2, 3, 0.0F);
		legRight.rotateAngleX = LEG_BASE_X;
		body.addChild(legRight);

		wingLeft = new ModelRenderer(this, 13, 13);
		wingLeft.setRotationPoint(1.5F, -4.9F, 1.7F);
		wingLeft.addBox(-0.5F, 0.0F, -1.7F, 1, 6, 3, 0.0F);
		wingLeft.rotateAngleX = WING_BASE_X;
		body.addChild(wingLeft);

		wingRight = new ModelRenderer(this, 13, 13);
		wingRight.mirror = true;
		wingRight.setRotationPoint(-1.5F, -4.9F, 1.7F);
		wingRight.addBox(-0.5F, 0.0F, -1.7F, 1, 6, 3, 0.0F);
		wingRight.rotateAngleX = WING_BASE_X;
		body.addChild(wingRight);

		tail = new ModelRenderer(this, 13, 0);
		tail.setRotationPoint(0.0F, -0.1F, 3.0F);
		tail.addBox(-1.5F, 0.0F, -2.0F, 3, 4, 2, 0.0F);
		tail.rotateAngleX = TAIL_BASE_X;
		body.addChild(tail);

		head = new ModelRenderer(this, 0, 9);
		head.setRotationPoint(0.0F, -4.8F, 1.7F);
		head.addBox(-1.5F, -2.8F, -1.5F, 3, 4, 3, 0.0F);
		head.rotateAngleX = HEAD_BASE_X;
		body.addChild(head);

		beak = new ModelRenderer(this, 13, 7);
		beak.setRotationPoint(0.0F, -1.4F, -1.9F);
		beak.addBox(-0.5F, -1.0F, -1.8F, 1, 2, 3, 0.0F);
		head.addChild(beak);
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

		if (this.isChild) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.translate(0.0F, 2.5F * (1F / scale), 0.0F);
			body.render(scale);
			GlStateManager.popMatrix();
		} else {
			body.render(scale);
		}
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, Entity entityIn) {
		boolean scared = entityIn instanceof EntityCrow && ((EntityCrow) entityIn).isScared();

		head.rotateAngleY = netHeadYaw * 0.0174533F;
		head.rotateAngleX = HEAD_BASE_X + headPitch * 0.0174533F;

		if (scared) {
			// Panic flight: fast, wide wing beats, tail flared, head tucked low/forward.
			float panicFlap = MathHelper.sin(ageInTicks * 2.2F) * 0.9F;
			wingLeft.rotateAngleX = WING_BASE_X - 0.6F + panicFlap;
			wingRight.rotateAngleX = WING_BASE_X - 0.6F + panicFlap;

			tail.rotateAngleX = TAIL_BASE_X - 0.35F;
			head.rotateAngleX += -0.3F;

			legLeft.rotateAngleX = LEG_BASE_X - 0.5F;
			legRight.rotateAngleX = LEG_BASE_X - 0.5F;

			body.rotateAngleX = BODY_BASE_X - 0.15F;
		} else {
			// idle tail wag
			tail.rotateAngleX = TAIL_BASE_X + MathHelper.cos(ageInTicks * 0.1F) * 0.05F;

			// idle wing rest flap (subtle, always on)
			float wingIdle = MathHelper.sin(ageInTicks * 0.2F) * 0.08F;
			wingLeft.rotateAngleX = WING_BASE_X + wingIdle;
			wingRight.rotateAngleX = WING_BASE_X + wingIdle;

			// walking legs
			legLeft.rotateAngleX = LEG_BASE_X + MathHelper.cos(limbSwing * 0.6662F) * 0.4F * limbSwingAmount;
			legRight.rotateAngleX = LEG_BASE_X + MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.4F * limbSwingAmount;

			// slight body bob while walking
			body.rotateAngleX = BODY_BASE_X + MathHelper.cos(limbSwing * 0.6662F) * 0.05F * limbSwingAmount;
		}
	}
}
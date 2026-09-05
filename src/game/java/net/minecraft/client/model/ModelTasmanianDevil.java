package net.minecraft.client.model;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTasmanianDevil;
import net.minecraft.util.MathHelper;

public class ModelTasmanianDevil extends ModelBase {
	public ModelRenderer body;
	public ModelRenderer armLeft;
	public ModelRenderer armRight;
	public ModelRenderer legLeft;
	public ModelRenderer legRight;
	public ModelRenderer head;
	public ModelRenderer earLeft;
	public ModelRenderer earRight;
	public ModelRenderer tail;

	public ModelTasmanianDevil() {
		this.textureWidth = 64;
		this.textureHeight = 64;

		body = new ModelRenderer(this, 0, 0);
		body.setRotationPoint(0.0F, 18.0F, 0.0F);
		body.addBox(-3.5F, -3.0F, -5.0F, 7, 6, 11, 0.0F);

		armLeft = new ModelRenderer(this, 26, 18);
		armLeft.setRotationPoint(2.6F, 3.0F, -3.0F);
		armLeft.addBox(-1.0F, -1.0F, -1.0F, 2, 4, 2, 0.0F);
		body.addChild(armLeft);

		armRight = new ModelRenderer(this, 26, 18);
		armRight.mirror = true;
		armRight.setRotationPoint(-2.6F, 3.0F, -3.0F);
		armRight.addBox(-1.0F, -1.0F, -1.0F, 2, 4, 2, 0.0F);
		body.addChild(armRight);

		legLeft = new ModelRenderer(this, 0, 0);
		legLeft.setRotationPoint(2.6F, 3.0F, 4.0F);
		legLeft.addBox(-1.0F, -1.0F, -1.0F, 2, 4, 2, 0.0F);
		body.addChild(legLeft);

		legRight = new ModelRenderer(this, 0, 0);
		legRight.mirror = true;
		legRight.setRotationPoint(-2.6F, 3.0F, 4.0F);
		legRight.addBox(-1.0F, -1.0F, -1.0F, 2, 4, 2, 0.0F);
		body.addChild(legRight);

		head = new ModelRenderer(this, 0, 18);
		head.setRotationPoint(0.0F, -2.0F, -6.0F);
		head.addBox(-3.0F, -2.0F, -3.0F, 6, 4, 4, 0.0F);
		head.setTextureOffset(26, 0);
		head.addBox(-2.0F, -2.0F, -6.0F, 4, 4, 3, 0.0F);
		body.addChild(head);

		earLeft = new ModelRenderer(this, 0, 27);
		earLeft.setRotationPoint(2.0F, -1.0F, 0.0F);
		earLeft.addBox(-1.0F, -3.0F, -1.0F, 3, 3, 1, 0.0F);
		setRotationAngle(earLeft, 0.2182F, 0.0F, 0.3054F);
		head.addChild(earLeft);

		earRight = new ModelRenderer(this, 0, 27);
		earRight.mirror = true;
		earRight.setRotationPoint(-2.0F, -1.0F, 0.0F);
		earRight.addBox(-2.0F, -3.0F, -1.0F, 3, 3, 1, 0.0F);
		setRotationAngle(earRight, 0.2182F, 0.0F, -0.3054F);
		head.addChild(earRight);

		tail = new ModelRenderer(this, 15, 21);
		tail.setRotationPoint(0.0F, -2.0F, 6.0F);
		tail.addBox(-1.0F, 0.0F, 0.0F, 2, 2, 6, 0.0F);
		setRotationAngle(tail, -0.5236F, 0.0F, 0.0F);
		body.addChild(tail);
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

		if (this.isChild) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.translate(0.0F, 1.5F * (1F / scale), 0.0F);
			body.render(scale);
			GlStateManager.popMatrix();
		} else {
			body.render(scale);
		}
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, Entity entityIn) {
		EntityTasmanianDevil devil = (EntityTasmanianDevil) entityIn;

		float walkSpeed = 1.0F;
		float walkDegree = 0.5F;

		// base walk cycle
		legLeft.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
		legRight.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
		armLeft.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
		armRight.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;

		body.rotateAngleY = MathHelper.cos(limbSwing * walkSpeed) * walkDegree * 0.6F * limbSwingAmount;
		tail.rotateAngleY = MathHelper.cos(limbSwing * walkSpeed) * walkDegree * 0.6F * limbSwingAmount;

		earLeft.rotateAngleY = 0.3054F + MathHelper.cos(limbSwing * walkSpeed) * 0.15F * limbSwingAmount;
		earRight.rotateAngleY = -0.3054F - MathHelper.cos(limbSwing * walkSpeed) * 0.15F * limbSwingAmount;

		head.rotateAngleY = netHeadYaw * 0.0174533F;
		head.rotateAngleX = headPitch * 0.0174533F;

		// attack bite: quick head snap down and forward
		if (devil.getAnimation() == EntityTasmanianDevil.ANIMATION_ATTACK) {
			float t = devil.getAnimationTick();
			float bite = MathHelper.sin(t * 0.9F) * 0.5F;
			head.rotateAngleX += bite;
		}

		// howl/screech: head thrown back, ears pinned, mouth wide (handled by texture swap in the renderer)
		if (devil.getAnimation() == EntityTasmanianDevil.ANIMATION_HOWL) {
			float t = Math.min(devil.getAnimationTick(), 10);
			float progress = t / 10F;
			head.rotateAngleX -= 0.7854F * progress;
			earLeft.rotateAngleX -= 0.3F * progress;
			earRight.rotateAngleX -= 0.3F * progress;
			armLeft.rotateAngleX -= 0.4F * progress;
			armRight.rotateAngleX -= 0.4F * progress;
		}
	}

	public void setRotationAngle(ModelRenderer renderer, float x, float y, float z) {
		renderer.rotateAngleX = x;
		renderer.rotateAngleY = y;
		renderer.rotateAngleZ = z;
	}
}
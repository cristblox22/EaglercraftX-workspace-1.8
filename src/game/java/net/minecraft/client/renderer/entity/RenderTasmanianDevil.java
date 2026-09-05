package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelTasmanianDevil;
import net.minecraft.entity.passive.EntityTasmanianDevil;
import net.minecraft.util.ResourceLocation;

public class RenderTasmanianDevil extends RenderLiving<EntityTasmanianDevil> {

	private static final ResourceLocation DEVIL_TEXTURE = new ResourceLocation("textures/entity/alexmobs/tasmanian_devil.png");
	private static final ResourceLocation DEVIL_TEXTURE_ANGRY = new ResourceLocation("textures/entity/alexmobs/tasmanian_devil_angry.png");

	public RenderTasmanianDevil(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelTasmanianDevil(), 0.3F);
	}

	protected ResourceLocation getEntityTexture(EntityTasmanianDevil entity) {
		return entity.getAnimation() == EntityTasmanianDevil.ANIMATION_HOWL && entity.getAnimationTick() < 34
				? DEVIL_TEXTURE_ANGRY
				: DEVIL_TEXTURE;
	}
}
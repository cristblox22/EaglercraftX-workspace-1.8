package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelRaccoon;
import net.minecraft.entity.passive.EntityRaccoon;
import net.minecraft.util.ResourceLocation;

public class RenderRaccoon extends RenderLiving<EntityRaccoon> {

	private static final ResourceLocation RACCOON_TEXTURE = new ResourceLocation("textures/entity/alexmobs/raccoon.png");

	public RenderRaccoon(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelRaccoon(), 0.4F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRaccoon entity) {
		return RACCOON_TEXTURE;
	}
}
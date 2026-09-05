package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelCrow;
import net.minecraft.entity.passive.EntityCrow;
import net.minecraft.util.ResourceLocation;

public class RenderCrow extends RenderLiving<EntityCrow> {

	private static final ResourceLocation CROW_TEXTURE = new ResourceLocation("textures/entity/alexmobs/crow.png");

	public RenderCrow(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelCrow(), 0.2F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCrow entity) {
		return CROW_TEXTURE;
	}
}

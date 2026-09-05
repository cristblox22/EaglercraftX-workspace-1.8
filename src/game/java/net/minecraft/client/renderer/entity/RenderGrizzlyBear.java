package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelGrizzlyBear;
import net.minecraft.entity.passive.EntityGrizzlyBear;
import net.minecraft.util.ResourceLocation;

public class RenderGrizzlyBear extends RenderLiving<EntityGrizzlyBear> {

	private static final ResourceLocation GRIZZLY_BEAR_TEXTURE = new ResourceLocation("textures/entity/alexmobs/grizzly_bear.png");

	public RenderGrizzlyBear(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelGrizzlyBear(), 0.9F);
	}

	protected ResourceLocation getEntityTexture(EntityGrizzlyBear entity) {
		return GRIZZLY_BEAR_TEXTURE;
	}
}
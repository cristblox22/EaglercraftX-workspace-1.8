package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.layers.LayerSheepWool;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;

public class RenderSheep extends RenderLiving<EntitySheep> {

	private enum SheepVariant {
		NORMAL("sheep"),
		FUZZY("fuzzy"),
		INKY("inky"),
		LONG_NOSED("long_nosed"),
		PATCHED("patched"),
		ROCKY("rocky");

		final ResourceLocation texture;

		SheepVariant(String name) {
			this.texture = new ResourceLocation("textures/entity/sheep/" + name + ".png");
		}

		static final SheepVariant[] VALUES = values();
	}

	private static final java.util.Map<Integer, SheepVariant> VARIANT_CACHE = new java.util.HashMap<Integer, SheepVariant>();

	public RenderSheep(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
		super(renderManagerIn, modelBaseIn, shadowSizeIn);
		this.addLayer(new LayerSheepWool(this));
	}

	protected ResourceLocation getEntityTexture(EntitySheep entitysheep) {
		return getVariant(entitysheep).texture;
	}

	private SheepVariant getVariant(EntitySheep entitysheep) {
		int entId = entitysheep.getEntityId();

		SheepVariant cached = VARIANT_CACHE.get(entId);
		if (cached != null) {
			return cached;
		}

		int index = Math.abs(entId) % SheepVariant.VALUES.length;
		SheepVariant chosen = SheepVariant.VALUES[index];
		VARIANT_CACHE.put(entId, chosen);
		return chosen;
	}
}
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityCrocodile;
import net.minecraft.util.ResourceLocation;

public class RenderCrocodile extends RenderLiving<EntityCrocodile> {

    private static final ResourceLocation CROCODILE_TEXTURE = new ResourceLocation("textures/entity/alexmobs/crocodile_0.png");

    public RenderCrocodile(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCrocodile entity) {
        return CROCODILE_TEXTURE;
    }
}
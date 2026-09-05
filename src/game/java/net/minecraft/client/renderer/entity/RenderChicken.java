package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderChicken extends RenderLiving<EntityChicken> {

    private static final ResourceLocation CHICKEN_NORMAL =
            new ResourceLocation("textures/entity/chicken/chicken.png");

    private static final ResourceLocation CHICKEN_AMBER =
            new ResourceLocation("textures/entity/chicken/amber.png");

    private static final ResourceLocation CHICKEN_BONE =
            new ResourceLocation("textures/entity/chicken/bone.png");

    private static final ResourceLocation CHICKEN_BRONZED =
            new ResourceLocation("textures/entity/chicken/bronzed.png");

    private static final ResourceLocation CHICKEN_DUCK =
            new ResourceLocation("textures/entity/chicken/duck.png");

    private static final ResourceLocation CHICKEN_GOLD_CRESTED =
            new ResourceLocation("textures/entity/chicken/gold_crested.png");

    private static final ResourceLocation CHICKEN_MIDNIGHT =
            new ResourceLocation("textures/entity/chicken/midnight.png");

    private static final ResourceLocation CHICKEN_SKEWBALD =
            new ResourceLocation("textures/entity/chicken/skewbald.png");

    private static final ResourceLocation CHICKEN_STORMY =
            new ResourceLocation("textures/entity/chicken/stormy.png");

    private static final ResourceLocation[] CHICKEN_VARIANTS = {
        CHICKEN_NORMAL,
        CHICKEN_AMBER,
        CHICKEN_BONE,
        CHICKEN_BRONZED,
        CHICKEN_DUCK,
        CHICKEN_GOLD_CRESTED,
        CHICKEN_MIDNIGHT,
        CHICKEN_SKEWBALD,
        CHICKEN_STORMY
    };

    private static final int[] VARIANT_CHANCES = {
        50,
         2,
         2,
         5,
         5,
         5,
         2,
         4,
        25
    };

    public RenderChicken(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityChicken entity) {
        long bits = entity.getUniqueID().getLeastSignificantBits();
        int roll = Math.abs((int) (bits % 100));

        int cumulative = 0;
        for (int i = 0; i < CHICKEN_VARIANTS.length; i++) {
            cumulative += VARIANT_CHANCES[i];
            if (roll < cumulative) {
                return CHICKEN_VARIANTS[i];
            }
        }

        return CHICKEN_NORMAL;
    }

    @Override
    protected float handleRotationFloat(EntityChicken livingBase, float partialTicks) {
        float f = livingBase.field_70888_h
                + (livingBase.wingRotation - livingBase.field_70888_h) * partialTicks;

        float f1 = livingBase.field_70884_g
                + (livingBase.destPos - livingBase.field_70884_g) * partialTicks;

        return (MathHelper.sin(f) + 1.0F) * f1;
    }
}
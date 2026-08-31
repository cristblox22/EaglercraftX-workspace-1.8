package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

/**+
 * This portion of EaglercraftX contains deobfuscated Minecraft 1.8 source code.
 * 
 * Minecraft 1.8.8 bytecode is (c) 2015 Mojang AB. "Do not distribute!"
 * Mod Coder Pack v9.18 deobfuscation configs are (c) Copyright by the MCP Team
 * 
 * EaglercraftX 1.8 patch files (c) 2022-2025 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.
 */
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

    /**
     * All chicken variants.
     *
     * 9 total textures:
     * 0 = normal chicken
     * 1 = amber
     * 2 = bone
     * 3 = bronzed
     * 4 = duck
     * 5 = gold crested
     * 6 = midnight
     * 7 = skewbald
     * 8 = stormy
     */
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

    public RenderChicken(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    /**+
     * Returns the location of an entity's texture.
     *
     * Each chicken gets one of the 9 variants.
     * The entity ID keeps the selected variant consistent
     * so the texture does not flicker.
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityChicken entity) {
        int id = entity.getEntityId();

        // Prevent negative entity IDs from causing an invalid array index
        int variant = Math.abs(id) % CHICKEN_VARIANTS.length;

        return CHICKEN_VARIANTS[variant];
    }

    /**+
     * Defines what float the third param in setRotationAngles of
     * ModelBase is.
     */
    @Override
    protected float handleRotationFloat(EntityChicken livingBase, float partialTicks) {
        float f = livingBase.field_70888_h
                + (livingBase.wingRotation - livingBase.field_70888_h) * partialTicks;

        float f1 = livingBase.field_70884_g
                + (livingBase.destPos - livingBase.field_70884_g) * partialTicks;

        return (MathHelper.sin(f) + 1.0F) * f1;
    }
}

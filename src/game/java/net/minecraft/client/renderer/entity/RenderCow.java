package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityCow;
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
public class RenderCow extends RenderLiving<EntityCow> {

    private static final ResourceLocation COW_NORMAL =
            new ResourceLocation("textures/entity/cow/cow.png");

    private static final ResourceLocation COW_WOOLY =
            new ResourceLocation("textures/entity/cow/wooly.png");

    private static final ResourceLocation COW_UMBRA =
            new ResourceLocation("textures/entity/cow/umbra.png");

    private static final ResourceLocation COW_SUNSET =
            new ResourceLocation("textures/entity/cow/sunset.png");

    private static final ResourceLocation COW_PINTO =
            new ResourceLocation("textures/entity/cow/pinto.png");

    private static final ResourceLocation COW_DAIRY =
            new ResourceLocation("textures/entity/cow/dairy.png");

    private static final ResourceLocation COW_CREAM =
            new ResourceLocation("textures/entity/cow/cream.png");

    private static final ResourceLocation COW_COOKIE =
            new ResourceLocation("textures/entity/cow/cookie.png");

    private static final ResourceLocation COW_ASHEN =
            new ResourceLocation("textures/entity/cow/ashen.png");

    private static final ResourceLocation COW_ALBINO =
            new ResourceLocation("textures/entity/cow/albino.png");

    /**
     * All cow variants.
     *
     * 10 total textures:
     * 0 = normal cow
     * 1 = wooly
     * 2 = umbra
     * 3 = sunset
     * 4 = pinto
     * 5 = dairy
     * 6 = cream
     * 7 = cookie
     * 8 = ashen
     * 9 = albino
     */
    private static final ResourceLocation[] COW_VARIANTS = {
        COW_NORMAL,
        COW_WOOLY,
        COW_UMBRA,
        COW_SUNSET,
        COW_PINTO,
        COW_DAIRY,
        COW_CREAM,
        COW_COOKIE,
        COW_ASHEN,
        COW_ALBINO
    };

    public RenderCow(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    /**+
     * Returns the texture used by this cow.
     *
     * Each cow gets one of the 10 variants.
     * The entity ID is used so the chosen texture stays
     * consistent and does not flicker while rendering.
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityCow entity) {
        int id = entity.getEntityId();

        // Prevent negative entity IDs from causing an invalid array index
        int variant = Math.abs(id) % COW_VARIANTS.length;

        return COW_VARIANTS[variant];
    }
}

package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

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
public class RenderPig extends RenderLiving<EntityPig> {

    private static final ResourceLocation PIG_NORMAL =
            new ResourceLocation("textures/entity/pig/pig.png");

    private static final ResourceLocation PIG_MOTTLED =
            new ResourceLocation("textures/entity/pig/mottled.png");

    private static final ResourceLocation PIG_PIEBALD =
            new ResourceLocation("textures/entity/pig/piebald.png");

    private static final ResourceLocation PIG_PINK_FOOTED =
            new ResourceLocation("textures/entity/pig/pink_footed.png");

    private static final ResourceLocation PIG_SOOTY =
            new ResourceLocation("textures/entity/pig/sooty.png");

    private static final ResourceLocation PIG_SPOTTED =
            new ResourceLocation("textures/entity/pig/spotted.png");

    private static final ResourceLocation PIG_MUD_OVERLAY =
            new ResourceLocation("textures/entity/pig/mud_overlay.png");

    /**
     * All pig variants.
     *
     * 6 total textures:
     * 0 = normal pig
     * 1 = mottled
     * 2 = piebald
     * 3 = pink footed
     * 4 = sooty
     * 5 = spotted
     */
    private static final ResourceLocation[] PIG_VARIANTS = {
        PIG_NORMAL,
        PIG_MOTTLED,
        PIG_PIEBALD,
        PIG_PINK_FOOTED,
        PIG_SOOTY,
        PIG_SPOTTED
    };

    public RenderPig(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);

        this.addLayer(new LayerPigMud(this, modelBaseIn));
    }

    /**
     * Returns the texture used by this pig.
     *
     * Each pig gets one of the 6 variants.
     * The entity ID keeps the selected variant consistent
     * so the texture does not flicker.
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityPig entity) {
        int id = entity.getEntityId();

        int variant = Math.abs(id) % PIG_VARIANTS.length;

        return PIG_VARIANTS[variant];
    }

    /**
     * Adds mud to some pigs.
     *
     * 25% of pigs will have the mud overlay.
     */
    private static class LayerPigMud implements LayerRenderer<EntityPig> {

        private final RenderPig renderer;
        private final ModelBase model;

        public LayerPigMud(RenderPig renderer, ModelBase model) {
            this.renderer = renderer;
            this.model = model;
        }

        @Override
        public void doRenderLayer(
                EntityPig entity,
                float limbSwing,
                float limbSwingAmount,
                float partialTicks,
                float ageInTicks,
                float netHeadYaw,
                float headPitch,
                float scale) {

            int id = entity.getEntityId();

            // 25% chance of this pig being muddy
            int mudChance = Math.abs(id * 31 + 7) % 100;

            if (mudChance >= 25) {
                return;
            }

            // Render the transparent mud texture over the pig
            renderer.bindTexture(PIG_MUD_OVERLAY);

            model.render(
                    entity,
                    limbSwing,
                    limbSwingAmount,
                    ageInTicks,
                    netHeadYaw,
                    headPitch,
                    scale
            );
        }

        @Override
        public boolean shouldCombineTextures() {
            return true;
        }
    }
}

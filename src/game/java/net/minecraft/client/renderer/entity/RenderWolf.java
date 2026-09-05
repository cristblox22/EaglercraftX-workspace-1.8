package net.minecraft.client.renderer.entity;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.layers.LayerWolfCollar;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

/**+
 * This portion of EaglercraftX contains deobfuscated Minecraft 1.8 source code.
 *
 * Minecraft 1.8.8 bytecode is (c) 2015 Mojang AB. "Do not distribute!"
 * Mod Coder Pack v9.18 deobfuscation configs are (c) Copyright by the MCP Team
 *
 * EaglercraftX 1.8 patch files (c) 2022-2025 lax1dude, ayunami2000. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
public class RenderWolf extends RenderLiving<EntityWolf> {

	private enum WolfVariant {
		NORMAL(0, "wolf", "wolf_tame", "wolf_angry"),
		BLACK(1, "black", "black_tame", "black_angry"),
		STRIPED(2, "striped", "striped_tame", "striped_angry"),
		SNOWY(3, "snowy", "snowy_tame", "snowy_angry"),
		HUSKY(4, "husky", "husky_tame", "husky_angry"),
		JUPITER(5, "jupiter", "jupiter_tame", "jupiter_angry"),
		GERMAN_SHEPHERD(6, "german_shepherd", "german_shepherd_tame", "german_shepherd_angry"),
		WOODS(7, "woods", "woods_tame", "woods_angry"),
		CHESTNUT(8, "chestnut", "chestnut_tame", "chestnut_angry");

		final int id;
		final ResourceLocation normalTex;
		final ResourceLocation tameTex;
		final ResourceLocation angryTex;

		WolfVariant(int id, String normal, String tame, String angry) {
			this.id = id;
			this.normalTex = new ResourceLocation("textures/entity/wolf/" + normal + ".png");
			this.tameTex = new ResourceLocation("textures/entity/wolf/" + tame + ".png");
			this.angryTex = new ResourceLocation("textures/entity/wolf/" + angry + ".png");
		}

		static final WolfVariant[] VALUES = values();

		static final WolfVariant[] RANDOM_POOL;
		static {
			java.util.List<WolfVariant> pool = new java.util.ArrayList<WolfVariant>();
			for (WolfVariant v : VALUES) {
				if (v != SNOWY && v != STRIPED) {
					pool.add(v);
				}
			}
			RANDOM_POOL = pool.toArray(new WolfVariant[0]);
		}
	}

	public RenderWolf(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
		super(renderManagerIn, modelBaseIn, shadowSizeIn);
		this.addLayer(new LayerWolfCollar(this));
	}

	protected float handleRotationFloat(EntityWolf entitywolf, float var2) {
		return entitywolf.getTailRotation();
	}

	public void doRender(EntityWolf entitywolf, double d0, double d1, double d2, float f, float f1) {
		if (entitywolf.isWolfWet()) {
			float f2 = entitywolf.getBrightness(f1) * entitywolf.getShadingWhileWet(f1);
			GlStateManager.color(f2, f2, f2);
		}

		super.doRender(entitywolf, d0, d1, d2, f, f1);
	}

	protected ResourceLocation getEntityTexture(EntityWolf entitywolf) {
		WolfVariant variant = getVariant(entitywolf);

		if (entitywolf.isTamed()) {
			return variant.tameTex;
		} else if (entitywolf.isAngry()) {
			return variant.angryTex;
		} else {
			return variant.normalTex;
		}
	}

	private static final java.util.Map<Integer, WolfVariant> VARIANT_CACHE = new java.util.HashMap<Integer, WolfVariant>();

	private WolfVariant getVariant(EntityWolf entitywolf) {
		int entId = entitywolf.getEntityId();

		WolfVariant cached = VARIANT_CACHE.get(entId);
		if (cached != null) {
			return cached;
		}

		WolfVariant chosen = computeVariant(entitywolf);
		VARIANT_CACHE.put(entId, chosen);
		return chosen;
	}

	private WolfVariant computeVariant(EntityWolf entitywolf) {
		BiomeGenBase biome = entitywolf.worldObj.getBiomeGenForCoords(
				new net.minecraft.util.BlockPos(entitywolf.getPosition()));

		if (biome != null) {
			if (isSnowyBiome(biome)) {
				return WolfVariant.SNOWY;
			}
			if (isDesertOrAcaciaBiome(biome)) {
				return WolfVariant.STRIPED;
			}
		}

		int id = entitywolf.getEntityId();
		int index = Math.abs(id) % WolfVariant.RANDOM_POOL.length;
		return WolfVariant.RANDOM_POOL[index];
	}

	private boolean isSnowyBiome(BiomeGenBase biome) {
		return biome == BiomeGenBase.icePlains
				|| biome == BiomeGenBase.iceMountains
				|| biome == BiomeGenBase.coldTaiga
				|| biome == BiomeGenBase.coldTaigaHills
				|| biome == BiomeGenBase.frozenRiver
				|| biome == BiomeGenBase.frozenOcean
				|| biome.getEnableSnow();
	}

	private boolean isDesertOrAcaciaBiome(BiomeGenBase biome) {
		return biome == BiomeGenBase.desert
				|| biome == BiomeGenBase.desertHills
				|| biome == BiomeGenBase.savanna
				|| biome == BiomeGenBase.savannaPlateau;
	}
}
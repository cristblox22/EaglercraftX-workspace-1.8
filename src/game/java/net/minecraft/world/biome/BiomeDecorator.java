package net.minecraft.world.biome;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.world.gen.feature.WorldGenSedge;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockSedge;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.GeneratorBushFeature;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenBoulder;
import net.minecraft.world.gen.feature.WorldGenBush;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenClay;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenFallenLog;
import net.minecraft.world.gen.feature.WorldGenFernPatch;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenSand;
import net.minecraft.world.gen.feature.WorldGenWaterlily;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.ArrayList;
import java.util.List;

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
public class BiomeDecorator {

	protected World currentWorld;
	protected EaglercraftRandom randomGenerator;
	protected BlockPos field_180294_c;
	protected ChunkProviderSettings chunkProviderSettings;
	/**+
	 * The clay generator.
	 */
	protected WorldGenerator clayGen = new WorldGenClay(4);
	/**+
	 * The sand generator.
	 */
	protected WorldGenerator sedgeGen;
	protected WorldGenerator sandGen = new WorldGenSand(Blocks.sand, 7);
	/**+
	 * The gravel generator.
	 */
	protected WorldGenerator gravelAsSandGen = new WorldGenSand(Blocks.gravel, 6);
	protected WorldGenerator dirtGen;
	protected WorldGenerator gravelGen;
	protected WorldGenerator graniteGen;
	protected WorldGenerator dioriteGen;
	protected WorldGenerator andesiteGen;
	protected WorldGenerator coalGen;
	protected WorldGenerator ironGen;
	protected WorldGenerator goldGen;
	protected WorldGenerator redstoneGen;
	protected WorldGenerator diamondGen;
	protected WorldGenerator lapisGen;
	protected WorldGenFlowers yellowFlowerGen = new WorldGenFlowers(Blocks.yellow_flower,
			BlockFlower.EnumFlowerType.DANDELION);
	/**+
	 * Field that holds mushroomBrown WorldGenFlowers
	 */
	protected WorldGenerator mushroomBrownGen = new GeneratorBushFeature(Blocks.brown_mushroom);
	/**+
	 * Field that holds mushroomRed WorldGenFlowers
	 */
	protected WorldGenerator mushroomRedGen = new GeneratorBushFeature(Blocks.red_mushroom);
	/**+
	 * Field that holds big mushroom generator
	 */
	protected WorldGenerator bigMushroomGen = new WorldGenBigMushroom();
	/**+
	 * Field that holds WorldGenReed
	 */
	protected WorldGenerator reedGen = new WorldGenReed();
	/**+
	 * Field that holds WorldGenCactus
	 */
	protected WorldGenerator cactusGen = new WorldGenCactus();
	/**+
	 * The water lily generation!
	 */
	protected WorldGenerator waterlilyGen = new WorldGenWaterlily();
	/**+
	 * The fallen oak log generator.
	 */
	protected WorldGenFallenLog fallenLogGen = new WorldGenFallenLog(true);
	/**+
	 * Minimum block distance (X/Z plane) a fallen log must keep from any
	 * tree placed this chunk, so it no longer spawns right up against a
	 * trunk. See placedTreeWorldPositions in genDecorations().
	 */
	protected int logTreeClearance = 3;
	/**+
	 * The tall fern patch generator.
	 */
	protected WorldGenerator fernPatchGen = new WorldGenFernPatch(true);
	/**+
	 * The leaf bush generator. Its leaf type is set per-attempt in
	 * genDecorations to match the current biome.
	 */
	protected WorldGenBush bushGen = new WorldGenBush(true);
	/**+
	 * The stone/cobblestone/mossy cobblestone/coal ore boulder generator.
	 */
	protected WorldGenerator boulderGen = new WorldGenBoulder(true);
	protected int waterlilyPerChunk;
	protected int treesPerChunk;
	/**+
	 * Minimum block distance (measured on the X/Z plane, at the trunk's
	 * base position) enforced between trees placed within the same
	 * decorate() call. Applies to every tree type this biome can spawn -
	 * oak, birch, savanna, jungle, spruce, dark oak - since they all
	 * share the single placement loop in genDecorations() below and only
	 * differ in which WorldGenAbstractTree subclass genBigTreeChance()
	 * hands back. Raise this for more open/park-like spacing, lower it
	 * (or set to 0) to go back to vanilla's unspaced placement.
	 */
	protected int minTreeSpacing = 4;
	/**+
	 * Number of fallen log attempts per chunk. Each attempt only
	 * succeeds some of the time (see genDecorations), so this isn't
	 * a guaranteed count.
	 */
	protected int fallenLogsPerChunk = 1;
	/**+
	 * Number of fern patch attempts per chunk. Each attempt only
	 * succeeds some of the time (see genDecorations), so this isn't
	 * a guaranteed count.
	 */
	protected int fernPatchesPerChunk = 1;
	/**+
	 * Number of leaf bush attempts per chunk. Each attempt only
	 * succeeds some of the time (see genDecorations), so this isn't
	 * a guaranteed count - kept deliberately low so bushes don't spam.
	 */
	protected int bushesPerChunk = 1;
	/**+
	 * Number of boulder attempts per chunk. Each attempt only succeeds
	 * some of the time (see genDecorations), so this isn't a guaranteed
	 * count - kept deliberately low so boulders don't spam.
	 */
	protected int bouldersPerChunk = 1;
	/**+
	 * The number of yellow flower patches to generate per chunk.
	 * The game generates much less than this number, since it
	 * attempts to generate them at a random altitude.
	 */
	protected int flowersPerChunk = 2;
	/**+
	 * The amount of tall grass to generate per chunk.
	 */
	protected int grassPerChunk = 1;
	protected int deadBushPerChunk;
	protected int mushroomsPerChunk;
	protected int reedsPerChunk;
	protected int cactiPerChunk;
	/**+
	 * The number of sand patches to generate per chunk. Sand
	 * patches only generate when part of it is underwater.
	 */
	protected int sandPerChunk = 1;
	/**+
	 * The number of sand patches to generate per chunk. Sand
	 * patches only generate when part of it is underwater. There
	 * appear to be two separate fields for this.
	 */
	protected int sandPerChunk2 = 3;
	/**+
	 * The number of clay patches to generate per chunk. Only
	 * generates when part of it is underwater.
	 */
	protected int clayPerChunk = 1;
	protected int bigMushroomsPerChunk;
	/**+
	 * True if decorator should generate surface lava & water
	 */
	public boolean generateLakes = true;

	public void decorate(World worldIn, EaglercraftRandom random, BiomeGenBase parBiomeGenBase, BlockPos parBlockPos) {
		if (this.currentWorld != null) {
			throw new RuntimeException("Already decorating");
		} else {
			this.currentWorld = worldIn;
			String s = worldIn.getWorldInfo().getGeneratorOptions();
			if (s != null) {
				this.chunkProviderSettings = ChunkProviderSettings.Factory.jsonToFactory(s).func_177864_b();
			} else {
				this.chunkProviderSettings = ChunkProviderSettings.Factory.jsonToFactory("").func_177864_b();
			}

			this.randomGenerator = random;
			this.field_180294_c = parBlockPos;
			this.dirtGen = new WorldGenMinable(Blocks.dirt.getDefaultState(), this.chunkProviderSettings.dirtSize);
			this.dirtGen = new WorldGenMinable(Blocks.dirt.getDefaultState(), this.chunkProviderSettings.dirtSize);
			this.sedgeGen = new WorldGenSedge(Blocks.sedge, 2);
			this.gravelGen = new WorldGenMinable(Blocks.gravel.getDefaultState(),
					this.chunkProviderSettings.gravelSize);
			this.graniteGen = new WorldGenMinable(
					Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE),
					this.chunkProviderSettings.graniteSize);
			this.dioriteGen = new WorldGenMinable(
					Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE),
					this.chunkProviderSettings.dioriteSize);
			this.andesiteGen = new WorldGenMinable(
					Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE),
					this.chunkProviderSettings.andesiteSize);
			this.coalGen = new WorldGenMinable(Blocks.coal_ore.getDefaultState(), this.chunkProviderSettings.coalSize);
			this.ironGen = new WorldGenMinable(Blocks.iron_ore.getDefaultState(), this.chunkProviderSettings.ironSize);
			this.goldGen = new WorldGenMinable(Blocks.gold_ore.getDefaultState(), this.chunkProviderSettings.goldSize);
			this.redstoneGen = new WorldGenMinable(Blocks.redstone_ore.getDefaultState(),
					this.chunkProviderSettings.redstoneSize);
			this.diamondGen = new WorldGenMinable(Blocks.diamond_ore.getDefaultState(),
					this.chunkProviderSettings.diamondSize);
			this.lapisGen = new WorldGenMinable(Blocks.lapis_ore.getDefaultState(),
					this.chunkProviderSettings.lapisSize);
			// genDecorations() used to run outside a try/finally, so if any
			// generator threw (e.g. a feature touching a not-yet-generated
			// neighboring chunk, which re-enters decorate() and throws
			// "Already decorating" itself), currentWorld/randomGenerator
			// were never reset to null. That permanently stuck decorate()
			// in the "already decorating" state - every future chunk
			// populate() anywhere in the world, forever after, would
			// immediately throw on the very first line, regardless of
			// what triggered it (tree gen, liquid tick, anything). Always
			// resetting state here - even on failure - means one bad
			// generation attempt just skips that chunk's remaining
			// decorations instead of bricking the whole world.
			try {
				this.genDecorations(parBiomeGenBase);
			} finally {
				this.currentWorld = null;
				this.randomGenerator = null;
			}
		}
	}

	/**+
	 * True if (x, z) - a chunk-local column offset in the same [8,23]-ish
	 * space used by the tree placement loop - is at least
	 * minTreeSpacing blocks (measured on the flat X/Z plane) away from
	 * every tree already placed this decorate() call.
	 */
	private boolean isFarEnoughFromTrees(int x, int z, List<BlockPos> placed) {
		int minSpacingSq = this.minTreeSpacing * this.minTreeSpacing;
		for (int i = 0; i < placed.size(); ++i) {
			BlockPos p = placed.get(i);
			int dx = p.getX() - x;
			int dz = p.getZ() - z;
			if (dx * dx + dz * dz < minSpacingSq) {
				return false;
			}
		}
		return true;
	}

	protected void genDecorations(BiomeGenBase biomeGenBaseIn) {
		this.generateOres();

		for (int i = 0; i < this.sandPerChunk2; ++i) {
			int j = this.randomGenerator.nextInt(16) + 8;
			int k = this.randomGenerator.nextInt(16) + 8;
			this.sandGen.generate(this.currentWorld, this.randomGenerator,
					this.currentWorld.getTopSolidOrLiquidBlock(this.field_180294_c.add(j, 0, k)));
		}

		for (int i1 = 0; i1 < this.clayPerChunk; ++i1) {
			int l1 = this.randomGenerator.nextInt(16) + 8;
			int i6 = this.randomGenerator.nextInt(16) + 8;
			this.clayGen.generate(this.currentWorld, this.randomGenerator,
					this.currentWorld.getTopSolidOrLiquidBlock(this.field_180294_c.add(l1, 0, i6)));
		}

		for (int j1 = 0; j1 < this.sandPerChunk; ++j1) {
			int i2 = this.randomGenerator.nextInt(16) + 8;
			int j6 = this.randomGenerator.nextInt(16) + 8;
			this.gravelAsSandGen.generate(this.currentWorld, this.randomGenerator,
					this.currentWorld.getTopSolidOrLiquidBlock(this.field_180294_c.add(i2, 0, j6)));
		}

		int k1 = this.treesPerChunk;
		if (this.randomGenerator.nextInt(10) == 0) {
			++k1;
		}

		// Positions actually used by a successfully-generated tree this
		// call, so later trees in the same chunk can be checked against
		// them. Cleared per decorate() call (this method isn't
		// reentered - see the try/finally around genDecorations() in
		// decorate()), so it never carries state between chunks.
		List<BlockPos> placedTreePositions = new ArrayList<BlockPos>();
		// Absolute world positions of the same trees, for fallen-log
		// clearance checks below - placedTreePositions above stays in
		// chunk-local ints since that's what the spacing check compares
		// against, so this is tracked separately rather than converting
		// back and forth.
		List<BlockPos> placedTreeWorldPositions = new ArrayList<BlockPos>();

		for (int j2 = 0; j2 < k1; ++j2) {
			int k6 = 0;
			int l = 0;
			boolean foundSpot = this.minTreeSpacing <= 0;

			// A handful of resample attempts is enough to keep chunks
			// with a high treesPerChunk (e.g. dense forest biomes) from
			// mostly failing to place - if every attempt is too close to
			// an existing tree, this tree is just skipped rather than
			// forced into a cramped spot.
			for (int attempt = 0; !foundSpot && attempt < 8; ++attempt) {
				int candidateX = this.randomGenerator.nextInt(16) + 8;
				int candidateZ = this.randomGenerator.nextInt(16) + 8;
				if (this.isFarEnoughFromTrees(candidateX, candidateZ, placedTreePositions)) {
					k6 = candidateX;
					l = candidateZ;
					foundSpot = true;
				}
			}

			if (!foundSpot) {
				continue;
			}

			WorldGenAbstractTree worldgenabstracttree = biomeGenBaseIn.genBigTreeChance(this.randomGenerator);
			worldgenabstracttree.func_175904_e();
			BlockPos blockpos = this.currentWorld.getHeight(this.field_180294_c.add(k6, 0, l));
			if (worldgenabstracttree.generate(this.currentWorld, this.randomGenerator, blockpos)) {
				worldgenabstracttree.func_180711_a(this.currentWorld, this.randomGenerator, blockpos);
				placedTreePositions.add(new BlockPos(k6, 0, l));
				placedTreeWorldPositions.add(blockpos);
			}
		}

		for (int k2 = 0; k2 < this.bigMushroomsPerChunk; ++k2) {
			int l6 = this.randomGenerator.nextInt(16) + 8;
			int k10 = this.randomGenerator.nextInt(16) + 8;
			this.bigMushroomGen.generate(this.currentWorld, this.randomGenerator,
					this.currentWorld.getHeight(this.field_180294_c.add(l6, 0, k10)));
		}

		for (int l2 = 0; l2 < this.flowersPerChunk; ++l2) {
			int i7 = this.randomGenerator.nextInt(16) + 8;
			int l10 = this.randomGenerator.nextInt(16) + 8;
			int j14 = this.currentWorld.getHeight(this.field_180294_c.add(i7, 0, l10)).getY() + 32;
			if (j14 > 0) {
				int k17 = this.randomGenerator.nextInt(j14);
				BlockPos blockpos1 = this.field_180294_c.add(i7, k17, l10);
				BlockFlower.EnumFlowerType blockflower$enumflowertype = biomeGenBaseIn
						.pickRandomFlower(this.randomGenerator, blockpos1);
				BlockFlower blockflower = blockflower$enumflowertype.getBlockType().getBlock();
				if (blockflower.getMaterial() != Material.air) {
					this.yellowFlowerGen.setGeneratedBlock(blockflower, blockflower$enumflowertype);
					this.yellowFlowerGen.generate(this.currentWorld, this.randomGenerator, blockpos1);
				}
			}
		}

		for (int i3 = 0; i3 < this.grassPerChunk; ++i3) {
			int j7 = this.randomGenerator.nextInt(16) + 8;
			int i11 = this.randomGenerator.nextInt(16) + 8;
			int k14 = this.currentWorld.getHeight(this.field_180294_c.add(j7, 0, i11)).getY() * 2;
			if (k14 > 0) {
				int l17 = this.randomGenerator.nextInt(k14);
				biomeGenBaseIn.getRandomWorldGenForGrass(this.randomGenerator).generate(this.currentWorld,
						this.randomGenerator, this.field_180294_c.add(j7, l17, i11));
			}
		}

		for (int fl0 = 0; fl0 < this.fallenLogsPerChunk; ++fl0) {
			if (this.randomGenerator.nextInt(10) == 0) {
				int fl1 = this.randomGenerator.nextInt(16) + 8;
				int fl2 = this.randomGenerator.nextInt(16) + 8;
				BlockPos fl3 = this.currentWorld.getHeight(this.field_180294_c.add(fl1, 0, fl2));
				this.fallenLogGen.setTreesToAvoid(placedTreeWorldPositions, this.logTreeClearance);
				this.fallenLogGen.generate(this.currentWorld, this.randomGenerator, fl3);
			}
		}

		for (int fp0 = 0; fp0 < this.fernPatchesPerChunk; ++fp0) {
			if (this.randomGenerator.nextInt(6) == 0) {
				int fp1 = this.randomGenerator.nextInt(16) + 8;
				int fp2 = this.randomGenerator.nextInt(16) + 8;
				BlockPos fp3 = this.currentWorld.getHeight(this.field_180294_c.add(fp1, 0, fp2));
				this.fernPatchGen.generate(this.currentWorld, this.randomGenerator, fp3);
			}
		}
for (int sedge = 0; sedge < 2; ++sedge) {
			int sx = this.randomGenerator.nextInt(16) + 8;
			int sz = this.randomGenerator.nextInt(16) + 8;
			BlockPos spos = this.currentWorld.getHeight(this.field_180294_c.add(sx, 0, sz));
			this.sedgeGen.generate(this.currentWorld, this.randomGenerator, spos);
		}

		for (int bu0 = 0; bu0 < this.bushesPerChunk; ++bu0) {
			if (this.randomGenerator.nextInt(8) == 0) {
				int bu1 = this.randomGenerator.nextInt(16) + 8;
				int bu2 = this.randomGenerator.nextInt(16) + 8;
				BlockPos bu3 = this.currentWorld.getHeight(this.field_180294_c.add(bu1, 0, bu2));
				this.bushGen.setLeavesState(this.pickBushLeaves(biomeGenBaseIn));
				this.bushGen.generate(this.currentWorld, this.randomGenerator, bu3);
			}
		}

		for (int bo0 = 0; bo0 < this.bouldersPerChunk; ++bo0) {
			if (this.randomGenerator.nextInt(12) == 0) {
				int bo1 = this.randomGenerator.nextInt(16) + 8;
				int bo2 = this.randomGenerator.nextInt(16) + 8;
				BlockPos bo3 = this.currentWorld.getHeight(this.field_180294_c.add(bo1, 0, bo2));
				this.boulderGen.generate(this.currentWorld, this.randomGenerator, bo3);
			}
		}

		for (int j3 = 0; j3 < this.deadBushPerChunk; ++j3) {
			int k7 = this.randomGenerator.nextInt(16) + 8;
			int j11 = this.randomGenerator.nextInt(16) + 8;
			int l14 = this.currentWorld.getHeight(this.field_180294_c.add(k7, 0, j11)).getY() * 2;
			if (l14 > 0) {
				int i18 = this.randomGenerator.nextInt(l14);
				(new WorldGenDeadBush()).generate(this.currentWorld, this.randomGenerator,
						this.field_180294_c.add(k7, i18, j11));
			}
		}

		for (int k3 = 0; k3 < this.waterlilyPerChunk; ++k3) {
			int l7 = this.randomGenerator.nextInt(16) + 8;
			int k11 = this.randomGenerator.nextInt(16) + 8;
			int i15 = this.currentWorld.getHeight(this.field_180294_c.add(l7, 0, k11)).getY() * 2;
			if (i15 > 0) {
				int j18 = this.randomGenerator.nextInt(i15);

				BlockPos blockpos4;
				BlockPos blockpos7;
				for (blockpos4 = this.field_180294_c.add(l7, j18, k11); blockpos4.getY() > 0; blockpos4 = blockpos7) {
					blockpos7 = blockpos4.down();
					if (!this.currentWorld.isAirBlock(blockpos7)) {
						break;
					}
				}

				this.waterlilyGen.generate(this.currentWorld, this.randomGenerator, blockpos4);
			}
		}

		for (int l3 = 0; l3 < this.mushroomsPerChunk; ++l3) {
			if (this.randomGenerator.nextInt(4) == 0) {
				int i8 = this.randomGenerator.nextInt(16) + 8;
				int l11 = this.randomGenerator.nextInt(16) + 8;
				BlockPos blockpos2 = this.currentWorld.getHeight(this.field_180294_c.add(i8, 0, l11));
				this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, blockpos2);
			}

			if (this.randomGenerator.nextInt(8) == 0) {
				int j8 = this.randomGenerator.nextInt(16) + 8;
				int i12 = this.randomGenerator.nextInt(16) + 8;
				int j15 = this.currentWorld.getHeight(this.field_180294_c.add(j8, 0, i12)).getY() * 2;
				if (j15 > 0) {
					int k18 = this.randomGenerator.nextInt(j15);
					BlockPos blockpos5 = this.field_180294_c.add(j8, k18, i12);
					this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, blockpos5);
				}
			}
		}

		if (this.randomGenerator.nextInt(4) == 0) {
			int i4 = this.randomGenerator.nextInt(16) + 8;
			int k8 = this.randomGenerator.nextInt(16) + 8;
			int j12 = this.currentWorld.getHeight(this.field_180294_c.add(i4, 0, k8)).getY() * 2;
			if (j12 > 0) {
				int k15 = this.randomGenerator.nextInt(j12);
				this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator,
						this.field_180294_c.add(i4, k15, k8));
			}
		}

		if (this.randomGenerator.nextInt(8) == 0) {
			int j4 = this.randomGenerator.nextInt(16) + 8;
			int l8 = this.randomGenerator.nextInt(16) + 8;
			int k12 = this.currentWorld.getHeight(this.field_180294_c.add(j4, 0, l8)).getY() * 2;
			if (k12 > 0) {
				int l15 = this.randomGenerator.nextInt(k12);
				this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator,
						this.field_180294_c.add(j4, l15, l8));
			}
		}

		for (int k4 = 0; k4 < this.reedsPerChunk; ++k4) {
			int i9 = this.randomGenerator.nextInt(16) + 8;
			int l12 = this.randomGenerator.nextInt(16) + 8;
			int i16 = this.currentWorld.getHeight(this.field_180294_c.add(i9, 0, l12)).getY() * 2;
			if (i16 > 0) {
				int l18 = this.randomGenerator.nextInt(i16);
				this.reedGen.generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(i9, l18, l12));
			}
		}

		for (int l4 = 0; l4 < 10; ++l4) {
			int j9 = this.randomGenerator.nextInt(16) + 8;
			int i13 = this.randomGenerator.nextInt(16) + 8;
			int j16 = this.currentWorld.getHeight(this.field_180294_c.add(j9, 0, i13)).getY() * 2;
			if (j16 > 0) {
				int i19 = this.randomGenerator.nextInt(j16);
				this.reedGen.generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(j9, i19, i13));
			}
		}

		if (this.randomGenerator.nextInt(32) == 0) {
			int i5 = this.randomGenerator.nextInt(16) + 8;
			int k9 = this.randomGenerator.nextInt(16) + 8;
			int j13 = this.currentWorld.getHeight(this.field_180294_c.add(i5, 0, k9)).getY() * 2;
			if (j13 > 0) {
				int k16 = this.randomGenerator.nextInt(j13);
				(new WorldGenPumpkin()).generate(this.currentWorld, this.randomGenerator,
						this.field_180294_c.add(i5, k16, k9));
			}
		}

		for (int j5 = 0; j5 < this.cactiPerChunk; ++j5) {
			int l9 = this.randomGenerator.nextInt(16) + 8;
			int k13 = this.randomGenerator.nextInt(16) + 8;
			int l16 = this.currentWorld.getHeight(this.field_180294_c.add(l9, 0, k13)).getY() * 2;
			if (l16 > 0) {
				int j19 = this.randomGenerator.nextInt(l16);
				this.cactusGen.generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(l9, j19, k13));
			}
		}

		if (this.generateLakes) {
			for (int k5 = 0; k5 < 50; ++k5) {
				int i10 = this.randomGenerator.nextInt(16) + 8;
				int l13 = this.randomGenerator.nextInt(16) + 8;
				int i17 = this.randomGenerator.nextInt(248) + 8;
				if (i17 > 0) {
					int k19 = this.randomGenerator.nextInt(i17);
					BlockPos blockpos6 = this.field_180294_c.add(i10, k19, l13);
					(new WorldGenLiquids(Blocks.flowing_water)).generate(this.currentWorld, this.randomGenerator,
							blockpos6);
				}
			}

			for (int l5 = 0; l5 < 20; ++l5) {
				int j10 = this.randomGenerator.nextInt(16) + 8;
				int i14 = this.randomGenerator.nextInt(16) + 8;
				int j17 = this.randomGenerator
						.nextInt(this.randomGenerator.nextInt(this.randomGenerator.nextInt(240) + 8) + 8);
				BlockPos blockpos3 = this.field_180294_c.add(j10, j17, i14);
				(new WorldGenLiquids(Blocks.flowing_lava)).generate(this.currentWorld, this.randomGenerator, blockpos3);
			}
		}

	}

	/**+
	 * Picks a non-decaying leaf block state to match the current biome,
	 * for use by the standalone leaf bush feature. Falls back to oak
	 * leaves for any biome that doesn't match a more specific type.
	 */
	protected net.minecraft.block.state.IBlockState pickBushLeaves(BiomeGenBase biomeGenBaseIn) {
		String name = biomeGenBaseIn.biomeName;
		if (name.contains("Jungle")) {
			return Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE)
					.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
		} else if (name.contains("Birch")) {
			return Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH)
					.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
		} else if (name.contains("Taiga")) {
			return Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE)
					.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
		} else if (name.contains("Roofed")) {
			return Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.DARK_OAK)
					.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
		} else if (name.contains("Savanna")) {
			return Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA)
					.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
		} else {
			return Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK)
					.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
		}
	}

	/**+
	 * Standard ore generation helper. Generates most ores.
	 */
	protected void genStandardOre1(int blockCount, WorldGenerator generator, int minHeight, int maxHeight) {
		if (maxHeight < minHeight) {
			int i = minHeight;
			minHeight = maxHeight;
			maxHeight = i;
		} else if (maxHeight == minHeight) {
			if (minHeight < 255) {
				++maxHeight;
			} else {
				--minHeight;
			}
		}

		for (int j = 0; j < blockCount; ++j) {
			BlockPos blockpos = this.field_180294_c.add(this.randomGenerator.nextInt(16),
					this.randomGenerator.nextInt(maxHeight - minHeight) + minHeight, this.randomGenerator.nextInt(16));
			generator.generate(this.currentWorld, this.randomGenerator, blockpos);
		}

	}

	/**+
	 * Standard ore generation helper. Generates Lapis Lazuli.
	 */
	protected void genStandardOre2(int blockCount, WorldGenerator generator, int centerHeight, int spread) {
		for (int i = 0; i < blockCount; ++i) {
			BlockPos blockpos = this.field_180294_c.add(this.randomGenerator.nextInt(16),
					this.randomGenerator.nextInt(spread) + this.randomGenerator.nextInt(spread) + centerHeight - spread,
					this.randomGenerator.nextInt(16));
			generator.generate(this.currentWorld, this.randomGenerator, blockpos);
		}

	}

	/**+
	 * Generates ores in the current chunk
	 */
	protected void generateOres() {
		this.genStandardOre1(this.chunkProviderSettings.dirtCount, this.dirtGen,
				this.chunkProviderSettings.dirtMinHeight, this.chunkProviderSettings.dirtMaxHeight);
		this.genStandardOre1(this.chunkProviderSettings.gravelCount, this.gravelGen,
				this.chunkProviderSettings.gravelMinHeight, this.chunkProviderSettings.gravelMaxHeight);
		this.genStandardOre1(this.chunkProviderSettings.dioriteCount, this.dioriteGen,
				this.chunkProviderSettings.dioriteMinHeight, this.chunkProviderSettings.dioriteMaxHeight);
		this.genStandardOre1(this.chunkProviderSettings.graniteCount, this.graniteGen,
				this.chunkProviderSettings.graniteMinHeight, this.chunkProviderSettings.graniteMaxHeight);
		this.genStandardOre1(this.chunkProviderSettings.andesiteCount, this.andesiteGen,
				this.chunkProviderSettings.andesiteMinHeight, this.chunkProviderSettings.andesiteMaxHeight);
		this.genStandardOre1(this.chunkProviderSettings.coalCount, this.coalGen,
				this.chunkProviderSettings.coalMinHeight, this.chunkProviderSettings.coalMaxHeight);
		this.genStandardOre1(this.chunkProviderSettings.ironCount, this.ironGen,
				this.chunkProviderSettings.ironMinHeight, this.chunkProviderSettings.ironMaxHeight);
		this.genStandardOre1(this.chunkProviderSettings.goldCount, this.goldGen,
				this.chunkProviderSettings.goldMinHeight, this.chunkProviderSettings.goldMaxHeight);
		this.genStandardOre1(this.chunkProviderSettings.redstoneCount, this.redstoneGen,
				this.chunkProviderSettings.redstoneMinHeight, this.chunkProviderSettings.redstoneMaxHeight);
		this.genStandardOre1(this.chunkProviderSettings.diamondCount, this.diamondGen,
				this.chunkProviderSettings.diamondMinHeight, this.chunkProviderSettings.diamondMaxHeight);
		this.genStandardOre2(this.chunkProviderSettings.lapisCount, this.lapisGen,
				this.chunkProviderSettings.lapisCenterHeight, this.chunkProviderSettings.lapisSpread);
	}
}
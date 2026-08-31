package net.minecraft.world.gen.feature;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**+
 * Custom EaglercraftX 1.8 world generation feature (not original Mojang/MCP
 * code) written to match the style of the surrounding classes in this
 * package.
 *
 * Generates a small standalone leaf bush - no trunk/log, just a compact,
 * roughly round clump of leaves sitting directly on the ground. The leaf
 * block state isn't hardcoded: call setLeavesState() before generate() to
 * pick whichever leaf type matches the current biome (oak, spruce, birch,
 * jungle, acacia, dark oak), so a single instance can be reused everywhere.
 * Only ever places on top of grass - never sand, water, stone, etc - and
 * never overwrites existing blocks or floats in midair.
 *
 * MODIFIED: previously required the exact sampled position to already be
 * sitting on grass, which usually isn't true - getHeight() treats leaves
 * as solid ground (same issue documented in WorldGenBoulder), so most bush
 * attempts landed on top of tree canopy and silently failed, making
 * bushes very rare. generate() now walks down through canopy/plant matter
 * to find the real grass surface first, the same findRealGround() pattern
 * WorldGenBoulder uses. The per-layer radius also now shrinks with height
 * (again mirroring WorldGenBoulder), which tapers the top of taller
 * bushes and makes the overall clump read as slightly smaller/rounder
 * than before.
 */
public class WorldGenBush extends WorldGenerator {
	private IBlockState leavesState;

	public WorldGenBush(boolean parFlag) {
		super(parFlag);
		this.leavesState = Blocks.leaves.getDefaultState();
	}

	/**+
	 * Sets which leaf block state the next generate() call will use.
	 * Call this before generate() to match the bush's leaves to whatever
	 * biome it's spawning in.
	 */
	public void setLeavesState(IBlockState leavesState) {
		this.leavesState = leavesState;
	}

	public boolean generate(World worldIn, EaglercraftRandom random, BlockPos position) {
		BlockPos groundedPos = this.findRealGround(worldIn, position);
		if (groundedPos == null) {
			return false;
		}

		if (groundedPos.getY() < 1 || groundedPos.getY() + 2 >= 256) {
			return false;
		}

		if (!this.isClear(worldIn, groundedPos)) {
			return false;
		}

		int radius = 1 + random.nextInt(2); // 1 or 2 base radius
		int height = 1 + random.nextInt(2); // 1 or 2 blocks tall
		boolean placedAny = false;

		for (int dy = 0; dy < height; ++dy) {
			// Shrink each layer going up, same as WorldGenBoulder - tapers
			// the top instead of stacking two identical-width layers, and
			// nets a slightly smaller silhouette overall.
			double layerRadius = (double) radius - (double) dy * 0.6D;
			if (layerRadius < 0.5D) {
				break;
			}

			int r = (int) Math.ceil(layerRadius);

			for (int dx = -r; dx <= r; ++dx) {
				for (int dz = -r; dz <= r; ++dz) {
					double dist = Math.sqrt((double) (dx * dx + dz * dz));
					// Solid core, feathered/random edge - reads as a rounded clump
					// rather than a cube or a perfect sphere.
					if (dist <= layerRadius && (dist < layerRadius - 0.5D || random.nextInt(2) == 0)) {
						BlockPos leafPos = groundedPos.add(dx, dy, dz);
						if (this.isClear(worldIn, leafPos)) {
							this.setBlockAndNotifyAdequately(worldIn, leafPos, this.leavesState);
							placedAny = true;
						}
					}
				}
			}
		}

		return placedAny;
	}

	/**+
	 * Descends from the sampled position past any leaves/plants/vines
	 * until it finds the first block resting directly on real grass.
	 * Returns null if it hits something solid that isn't grass (stone,
	 * sand, water, etc.) or runs out of search distance - same shape as
	 * WorldGenBoulder#findRealGround, but bushes only ever belong on
	 * grass, so anything else is a hard stop rather than a valid ground
	 * type.
	 */
	private BlockPos findRealGround(World worldIn, BlockPos position) {
		BlockPos cursor = position;
		for (int i = 0; i < 24; ++i) {
			if (cursor.getY() < 1) {
				return null;
			}

			Block below = worldIn.getBlockState(cursor.down()).getBlock();
			if (below == Blocks.grass) {
				return cursor;
			}

			Material belowMaterial = below.getMaterial();
			if (belowMaterial != Material.air && belowMaterial != Material.leaves
					&& belowMaterial != Material.plants && belowMaterial != Material.vine) {
				return null;
			}

			cursor = cursor.down();
		}

		return null;
	}

	private boolean isClear(World worldIn, BlockPos pos) {
		Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
		return material == Material.air || material == Material.leaves || material == Material.plants;
	}
}
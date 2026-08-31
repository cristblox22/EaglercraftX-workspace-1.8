package net.minecraft.world.gen.feature;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenBoulder extends WorldGenerator {
	private static final IBlockState STONE = Blocks.stone.getDefaultState();
	private static final IBlockState COBBLESTONE = Blocks.cobblestone.getDefaultState();
	private static final IBlockState MOSSY_COBBLESTONE = Blocks.mossy_cobblestone.getDefaultState();
	private static final IBlockState COAL_ORE = Blocks.coal_ore.getDefaultState();

	public WorldGenBoulder(boolean parFlag) {
		super(parFlag);
	}

	public boolean generate(World worldIn, EaglercraftRandom random, BlockPos position) {
		// Walk down through any tree canopy / plant matter under the
		// sampled position until we hit the real ground surface. This is
		// the fix for boulders spawning on top of / inside tree leaves:
		// getHeight() treats leaves as solid, so without this the boulder
		// would generate at leaf height instead of true ground height.
		BlockPos groundedPos = this.findRealGround(worldIn, position);
		if (groundedPos == null) {
			return false;
		}

		if (!this.canFit(worldIn, groundedPos)) {
			return false;
		}

		int radius = 2 + random.nextInt(2);
		int height = 2 + random.nextInt(2);
		boolean placedAny = false;

		for (int dy = 0; dy < height; ++dy) {
			double layerRadius = (double) radius - (double) dy * 0.6D;
			if (layerRadius < 0.5D) {
				break;
			}

			int r = (int) Math.ceil(layerRadius);

			for (int dx = -r; dx <= r; ++dx) {
				for (int dz = -r; dz <= r; ++dz) {
					double dist = Math.sqrt((double) (dx * dx + dz * dz));
					if (dist <= layerRadius) {
						BlockPos blockPos = groundedPos.add(dx, dy, dz);

						// On the base layer, require actual solid ground directly
						// beneath this column - not just "canReplace" on the target
						// block - so the boulder never overhangs open air on sloped
						// terrain. Columns that fail just get skipped (a gap),
						// rather than floating.
						if (dy == 0 && !this.hasSolidGroundBelow(worldIn, blockPos)) {
							continue;
						}

						if (this.canReplace(worldIn, blockPos)) {
							this.setBlockAndNotifyAdequately(worldIn, blockPos, this.pickBoulderBlock(random));
							placedAny = true;
						}
					}
				}
			}
		}

		return placedAny;
	}

	/**+
	 * Descends from the sampled position past any leaves/logs/plants until
	 * it finds the first block resting directly on real ground (grass,
	 * dirt, or stone). Returns null if no real ground is found within a
	 * reasonable search distance (e.g. sampled position was over open air
	 * or deep water).
	 */
	private BlockPos findRealGround(World worldIn, BlockPos position) {
		BlockPos cursor = position;
		for (int i = 0; i < 24; ++i) {
			if (cursor.getY() < 1) {
				return null;
			}
			Block below = worldIn.getBlockState(cursor.down()).getBlock();
			if (this.isRealGround(below)) {
				return cursor;
			}
			cursor = cursor.down();
		}
		return null;
	}

	private boolean isRealGround(Block block) {
		return block == Blocks.grass || block == Blocks.dirt || block == Blocks.stone
				|| block == Blocks.sand || block == Blocks.gravel;
	}

	private boolean hasSolidGroundBelow(World worldIn, BlockPos pos) {
		Block below = worldIn.getBlockState(pos.down()).getBlock();
		return this.isRealGround(below) || below == Blocks.cobblestone || below == Blocks.mossy_cobblestone;
	}

	private IBlockState pickBoulderBlock(EaglercraftRandom random) {
		int roll = random.nextInt(20);
		if (roll == 0) {
			return COAL_ORE;
		} else if (roll <= 4) {
			return MOSSY_COBBLESTONE;
		} else if (roll <= 11) {
			return COBBLESTONE;
		} else {
			return STONE;
		}
	}

	private boolean canReplace(World worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();
		Material material = block.getMaterial();
		return material == Material.air || material == Material.plants || material == Material.vine
				|| block == Blocks.grass || block == Blocks.dirt || block == Blocks.stone;
	}

	/**+
	 * Ground check tightened to an explicit whitelist of real terrain
	 * blocks - it used to accept anything that wasn't water/lava/air,
	 * which meant tree leaves counted as valid ground and caused
	 * boulders to spawn resting on top of canopies.
	 */
	private boolean canFit(World worldIn, BlockPos position) {
		if (position.getY() < 1 || position.getY() + 4 >= 256) {
			return false;
		}

		Block ground = worldIn.getBlockState(position.down()).getBlock();
		return this.isRealGround(ground);
	}
}
package net.minecraft.world.gen.feature;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;

public class WorldGenFernPatch extends WorldGenerator {
	private static final int AREA_RADIUS = 15; // 30x30 bounding area
	private static final IBlockState FERN_LOWER = Blocks.double_plant.getDefaultState()
			.withProperty(BlockDoublePlant.VARIANT, BlockDoublePlant.EnumPlantType.FERN);
	private static final IBlockState FERN_UPPER = Blocks.double_plant.getDefaultState().withProperty(
			BlockDoublePlant.VARIANT, BlockDoublePlant.EnumPlantType.FERN).withProperty(BlockDoublePlant.HALF,
			BlockDoublePlant.EnumBlockHalf.UPPER);

	public WorldGenFernPatch(boolean parFlag) {
		super(parFlag);
	}

	public boolean generate(World worldIn, EaglercraftRandom random, BlockPos position) {
		int clusterCount = 8 + random.nextInt(9); // 8-16 patches across the 30x30 area
		boolean placedAny = false;

		for (int c = 0; c < clusterCount; ++c) {
			int centerX = random.nextInt(AREA_RADIUS + 1) - random.nextInt(AREA_RADIUS + 1);
			int centerZ = random.nextInt(AREA_RADIUS + 1) - random.nextInt(AREA_RADIUS + 1);
			BlockPos clusterCenter = position.add(centerX, 0, centerZ);

			int clusterAttempts = 4 + random.nextInt(6); // ferns per patch

			for (int i = 0; i < clusterAttempts; ++i) {
				int dx = random.nextInt(5) - random.nextInt(5);
				int dz = random.nextInt(5) - random.nextInt(5);
				BlockPos fernPos = this.findSurface(worldIn, clusterCenter.add(dx, 0, dz));

				if (fernPos != null && this.isWithinArea(position, fernPos) && this.tryPlaceFern(worldIn, fernPos)) {
					placedAny = true;
				}
			}
		}

		return placedAny;
	}

	private boolean isWithinArea(BlockPos origin, BlockPos pos) {
		int dx = pos.getX() - origin.getX();
		int dz = pos.getZ() - origin.getZ();
		return dx >= -AREA_RADIUS && dx <= AREA_RADIUS && dz >= -AREA_RADIUS && dz <= AREA_RADIUS;
	}

	private BlockPos findSurface(World worldIn, BlockPos pos) {
		// AREA_RADIUS (15) plus the per-fern dx/dz spread means a cluster
		// can land well outside the chunk's guaranteed-generated footprint
		// (the safe 0-31 block window past field_180294_c that vanilla's
		// 8-23 offset trick relies on). Reading a block in a chunk that
		// hasn't been generated yet forces it to generate immediately,
		// which can re-enter BiomeDecorator.decorate() while the current
		// chunk's decoration is still on the stack, throwing
		// "Already decorating". Bail out before touching the block state
		// if the target chunk isn't there yet - same fix as
		// WorldGenTrees#schematicFootprintIsSafe.
		if (!this.isChunkSafe(worldIn, pos)) {
			return null;
		}

		for (int dy = 2; dy >= -2; --dy) {
			BlockPos candidate = pos.up(dy);
			Block ground = worldIn.getBlockState(candidate.down()).getBlock();
			if (ground == Blocks.grass || ground == Blocks.dirt) {
				return candidate;
			}
		}

		return null;
	}

	private boolean isChunkSafe(World worldIn, BlockPos pos) {
		if (!(worldIn.getChunkProvider() instanceof ChunkProviderServer)) {
			return true;
		}

		ChunkProviderServer provider = (ChunkProviderServer) worldIn.getChunkProvider();
		int cx = pos.getX() >> 4;
		int cz = pos.getZ() >> 4;
		return provider.chunkExists(cx, cz);
	}

	private boolean tryPlaceFern(World worldIn, BlockPos pos) {
		if (pos.getY() < 1 || pos.getY() + 1 >= 256) {
			return false;
		}

		Material lowerMaterial = worldIn.getBlockState(pos).getBlock().getMaterial();
		Material upperMaterial = worldIn.getBlockState(pos.up()).getBlock().getMaterial();
		if (lowerMaterial != Material.air || upperMaterial != Material.air) {
			return false;
		}

		this.setBlockAndNotifyAdequately(worldIn, pos, FERN_LOWER);
		this.setBlockAndNotifyAdequately(worldIn, pos.up(), FERN_UPPER);
		return true;
	}
}
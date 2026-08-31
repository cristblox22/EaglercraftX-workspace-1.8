package net.minecraft.world.gen.feature;

import java.util.Collections;
import java.util.List;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**+
 * MODIFIED: canFit() used to only check what the log itself would sit on
 * or overlap (ground type, air/plant/leaf/vine above and at the log), so
 * it happily placed a log directly touching the base of a tree that had
 * already been generated in the same chunk - it just couldn't overlap the
 * trunk itself. setTreesToAvoid() lets the caller (BiomeDecorator) hand
 * over the X/Z positions of trees already placed this chunk plus a
 * minimum clearance; canFit() now rejects any log position that comes
 * within that distance of a tree, in addition to the existing checks.
 */
public class WorldGenFallenLog extends WorldGenerator {
	private static final IBlockState LOG_STATE_X = Blocks.log.getDefaultState().withProperty(BlockLog.LOG_AXIS,
			BlockLog.EnumAxis.X);
	private static final IBlockState LOG_STATE_Z = Blocks.log.getDefaultState().withProperty(BlockLog.LOG_AXIS,
			BlockLog.EnumAxis.Z);

	private List<BlockPos> treesToAvoid = Collections.emptyList();
	private int treeClearance = 0;

	public WorldGenFallenLog(boolean parFlag) {
		super(parFlag);
	}

	/**+
	 * Call before generate() to make the log steer clear of trees already
	 * placed this chunk. treePositions are absolute world BlockPos
	 * (X/Z is what matters - Y is ignored). clearance is the minimum
	 * block distance (X/Z plane) the log must keep from every entry.
	 * Pass an empty list or clearance <= 0 to disable the check.
	 */
	public void setTreesToAvoid(List<BlockPos> treePositions, int clearance) {
		this.treesToAvoid = treePositions != null ? treePositions : Collections.<BlockPos>emptyList();
		this.treeClearance = clearance;
	}

	public boolean generate(World worldIn, EaglercraftRandom random, BlockPos position) {
		int length = 4 + random.nextInt(3);
		boolean alongX = random.nextBoolean();
		EnumFacing facing;
		if (alongX) {
			facing = random.nextBoolean() ? EnumFacing.EAST : EnumFacing.WEST;
		} else {
			facing = random.nextBoolean() ? EnumFacing.SOUTH : EnumFacing.NORTH;
		}

		if (!this.canFit(worldIn, position, length, facing)) {
			return false;
		}

		IBlockState logState = alongX ? LOG_STATE_X : LOG_STATE_Z;
		BlockPos cursor = position;

		for (int i = 0; i < length; ++i) {
			this.setBlockAndNotifyAdequately(worldIn, cursor, logState);
			cursor = cursor.offset(facing);
		}

		return true;
	}

	private boolean canFit(World worldIn, BlockPos position, int length, EnumFacing facing) {
		if (position.getY() < 1 || position.getY() + 1 >= 256) {
			return false;
		}

		BlockPos cursor = position;

		for (int i = 0; i < length; ++i) {
			Block ground = worldIn.getBlockState(cursor.down()).getBlock();
			if (ground != Blocks.grass && ground != Blocks.dirt) {
				return false;
			}

			if (!this.isClear(worldIn, cursor) || !this.isClear(worldIn, cursor.up())) {
				return false;
			}

			if (this.isTooCloseToTree(cursor)) {
				return false;
			}

			cursor = cursor.offset(facing);
		}

		return true;
	}

	private boolean isTooCloseToTree(BlockPos pos) {
		if (this.treeClearance <= 0) {
			return false;
		}

		int clearanceSq = this.treeClearance * this.treeClearance;
		for (int i = 0; i < this.treesToAvoid.size(); ++i) {
			BlockPos tree = this.treesToAvoid.get(i);
			int dx = tree.getX() - pos.getX();
			int dz = tree.getZ() - pos.getZ();
			if (dx * dx + dz * dz < clearanceSq) {
				return true;
			}
		}

		return false;
	}

	private boolean isClear(World worldIn, BlockPos pos) {
		Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
		return material == Material.air || material == Material.plants || material == Material.vine
				|| material == Material.leaves;
	}
}
package net.minecraft.world.gen.feature;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;

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
 * ---
 * MODIFIED: generate() now replays the same baked block-offset schematics as
 * WorldGenTrees (see WorldGenTreeSchematics), using acacia log/leaf block
 * states, instead of the old procedural leaning-trunk shape.
 */
public class WorldGenSavannaTree extends WorldGenAbstractTree {
	private static final IBlockState field_181643_a = Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT,
			BlockPlanks.EnumType.ACACIA);
	private static final IBlockState field_181644_b = Blocks.leaves2.getDefaultState()
			.withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA)
			.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));

	public WorldGenSavannaTree(boolean parFlag) {
		super(parFlag);
	}

	public boolean generate(World world, EaglercraftRandom random, BlockPos blockpos) {
		int roll = random.nextInt(10);
		int[] logOffsets;
		int[] leafOffsets;
		int height;
		if (roll < 4) {
			logOffsets = WorldGenTreeSchematics.SMALL_A_LOGS;
			leafOffsets = WorldGenTreeSchematics.SMALL_A_LEAVES;
			height = WorldGenTreeSchematics.SMALL_A_HEIGHT;
		} else if (roll < 8) {
			logOffsets = WorldGenTreeSchematics.SMALL_B_LOGS;
			leafOffsets = WorldGenTreeSchematics.SMALL_B_LEAVES;
			height = WorldGenTreeSchematics.SMALL_B_HEIGHT;
		} else {
			logOffsets = WorldGenTreeSchematics.MEDIUM_LOGS;
			leafOffsets = WorldGenTreeSchematics.MEDIUM_LEAVES;
			height = WorldGenTreeSchematics.MEDIUM_HEIGHT;
		}

		if (blockpos.getY() < 1 || blockpos.getY() + height + 2 >= 256) {
			return false;
		}

		if (!this.schematicFootprintIsSafe(world, blockpos, logOffsets, leafOffsets)) {
			return false;
		}

		Block ground = world.getBlockState(blockpos.down()).getBlock();
		if (ground != Blocks.grass && ground != Blocks.dirt) {
			return false;
		}

		this.func_175921_a(world, blockpos.down());

		for (int i = 0; i < logOffsets.length; ++i) {
			int packed = logOffsets[i];
			BlockPos target = blockpos.add(WorldGenTreeSchematics.unpackDx(packed),
					WorldGenTreeSchematics.unpackDy(packed), WorldGenTreeSchematics.unpackDz(packed));
			Material material = world.getBlockState(target).getBlock().getMaterial();
			if (material == Material.air || material == Material.leaves || material == Material.plants
					|| material == Material.vine) {
				this.setBlockAndNotifyAdequately(world, target, field_181643_a);
			}
		}

		for (int i = 0; i < leafOffsets.length; ++i) {
			int packed = leafOffsets[i];
			BlockPos target = blockpos.add(WorldGenTreeSchematics.unpackDx(packed),
					WorldGenTreeSchematics.unpackDy(packed), WorldGenTreeSchematics.unpackDz(packed));
			Material material = world.getBlockState(target).getBlock().getMaterial();
			if (material == Material.air || material == Material.leaves || material == Material.plants
					|| material == Material.vine) {
				this.setBlockAndNotifyAdequately(world, target, field_181644_b);
			}
		}

		return true;
	}

	/**+
	 * Checks that every chunk touched by the given schematic's log/leaf
	 * offsets (relative to origin) already exists in the world before any
	 * block is placed. Some baked schematics (e.g. MEDIUM) have leaves
	 * that reach well outside the safe margin that vanilla decoration
	 * guarantees is already terrain-generated. Reading or writing a block
	 * in a chunk that hasn't been generated yet forces the game to
	 * generate AND populate that chunk immediately, which re-enters
	 * BiomeDecorator.decorate() while the current chunk's decoration is
	 * still on the stack, throwing "Already decorating". Returning false
	 * here just skips the tree at this position instead of crashing.
	 */
	private boolean schematicFootprintIsSafe(World world, BlockPos origin, int[] logOffsets, int[] leafOffsets) {
		int minCx = Integer.MAX_VALUE, maxCx = Integer.MIN_VALUE;
		int minCz = Integer.MAX_VALUE, maxCz = Integer.MIN_VALUE;

		for (int i = 0; i < logOffsets.length; ++i) {
			int packed = logOffsets[i];
			int cx = (origin.getX() + WorldGenTreeSchematics.unpackDx(packed)) >> 4;
			int cz = (origin.getZ() + WorldGenTreeSchematics.unpackDz(packed)) >> 4;
			if (cx < minCx) minCx = cx;
			if (cx > maxCx) maxCx = cx;
			if (cz < minCz) minCz = cz;
			if (cz > maxCz) maxCz = cz;
		}

		for (int i = 0; i < leafOffsets.length; ++i) {
			int packed = leafOffsets[i];
			int cx = (origin.getX() + WorldGenTreeSchematics.unpackDx(packed)) >> 4;
			int cz = (origin.getZ() + WorldGenTreeSchematics.unpackDz(packed)) >> 4;
			if (cx < minCx) minCx = cx;
			if (cx > maxCx) maxCx = cx;
			if (cz < minCz) minCz = cz;
			if (cz > maxCz) maxCz = cz;
		}

		if (!(world.getChunkProvider() instanceof ChunkProviderServer)) {
			return true;
		}

		ChunkProviderServer provider = (ChunkProviderServer) world.getChunkProvider();
		for (int cx = minCx; cx <= maxCx; ++cx) {
			for (int cz = minCz; cz <= maxCz; ++cz) {
				if (!provider.chunkExists(cx, cz)) {
					return false;
				}
			}
		}

		return true;
	}
}
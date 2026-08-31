package net.minecraft.world.gen.feature;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**+
 * Custom generator for Sedge foliage in EaglercraftX.
 */
public class WorldGenSedge extends WorldGenerator {
    private final Block sedgeBlock;
    private final int generationCount;
    private final int spreadRadius;

    public WorldGenSedge(Block sedgeBlock, int generationCount) {
        this.sedgeBlock = sedgeBlock;
        this.generationCount = generationCount;
        this.spreadRadius = 7; // Kept under 8 to prevent cross-chunk decoration bleeding
    }

    @Override
    public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
        boolean generated = false;

        for (int i = 0; i < this.generationCount; ++i) {
            BlockPos targetPos = position.add(
                rand.nextInt(this.spreadRadius) - rand.nextInt(this.spreadRadius),
                rand.nextInt(4) - rand.nextInt(4),
                rand.nextInt(this.spreadRadius) - rand.nextInt(this.spreadRadius)
            );

            if (worldIn.isAirBlock(targetPos) && targetPos.getY() < 255) {
                // Ensure the block below is valid for plant growth before placing
                Block blockBelow = worldIn.getBlockState(targetPos.down()).getBlock();
                if (blockBelow == net.minecraft.init.Blocks.grass || blockBelow == net.minecraft.init.Blocks.dirt || blockBelow == net.minecraft.init.Blocks.farmland) {
                    if (this.sedgeBlock.canPlaceBlockAt(worldIn, targetPos)) {
                        worldIn.setBlockState(targetPos, this.sedgeBlock.getDefaultState(), 2);
                        generated = true;
                    }
                }
            }
        }

        return generated;
    }
}
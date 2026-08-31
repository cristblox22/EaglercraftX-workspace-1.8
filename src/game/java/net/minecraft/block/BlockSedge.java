package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockSedge extends BlockCrops {
    // Custom property for exactly 4 stages (0 to 3)
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);

    public BlockSedge() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
    }

    @Override
    protected Item getSeed() {
        // Ensure you register an ItemSedgeSeed in Item.java
        return Item.getByNameOrId("sedge_seeds"); 
    }

    @Override
    protected Item getCrop() {
        // Ensure you register the final harvested Item in Item.java
        return Item.getByNameOrId("sedge_item"); 
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta > 3 ? 3 : meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((Integer)state.getValue(AGE)).intValue();
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] {AGE});
    }

    // --- Allow placement on Grass, Dirt, and Farmland ---
    @Override
    protected boolean canPlaceBlockOn(Block ground) {
        return ground == net.minecraft.init.Blocks.farmland || ground == net.minecraft.init.Blocks.grass || ground == net.minecraft.init.Blocks.dirt;
    }

    // --- Bonemeal Prevention ---
    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return false;
    }

    public boolean canUseBonemeal(World worldIn, net.lax1dude.eaglercraft.v1_8.EaglercraftRandom rand, BlockPos pos, IBlockState state) {
        return false;
    }

    public void grow(World worldIn, net.lax1dude.eaglercraft.v1_8.EaglercraftRandom rand, BlockPos pos, IBlockState state) {
    }
}
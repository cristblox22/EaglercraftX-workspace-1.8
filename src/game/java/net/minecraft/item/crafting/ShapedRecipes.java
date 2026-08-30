package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ShapedRecipes implements IRecipe {
	public final int recipeWidth;
	public final int recipeHeight;
	public final ItemStack[] recipeItems;
	private final ItemStack recipeOutput;
	private boolean copyIngredientNBT;

	public ShapedRecipes(int width, int height, ItemStack[] parArrayOfItemStack, ItemStack output) {
		this.recipeWidth = width;
		this.recipeHeight = height;
		this.recipeItems = parArrayOfItemStack;
		this.recipeOutput = output;
	}

	public ItemStack getRecipeOutput() {
		return this.recipeOutput;
	}

	public ItemStack[] getRemainingItems(InventoryCrafting inventorycrafting) {
		ItemStack[] aitemstack = new ItemStack[inventorycrafting.getSizeInventory()];

		for (int i = 0; i < aitemstack.length; ++i) {
			ItemStack itemstack = inventorycrafting.getStackInSlot(i);
			if (itemstack != null && itemstack.getItem().hasContainerItem()) {
				aitemstack[i] = new ItemStack(itemstack.getItem().getContainerItem());
			}
		}

		return aitemstack;
	}

	public boolean matches(InventoryCrafting inventorycrafting, World var2) {
		for (int i = 0; i <= 3 - this.recipeWidth; ++i) {
			for (int j = 0; j <= 3 - this.recipeHeight; ++j) {
				if (this.checkMatch(inventorycrafting, i, j, true)) {
					return true;
				}

				if (this.checkMatch(inventorycrafting, i, j, false)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean checkMatch(InventoryCrafting parInventoryCrafting, int parInt1, int parInt2, boolean parFlag) {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				int k = i - parInt1;
				int l = j - parInt2;
				ItemStack itemstack = null;
				if (k >= 0 && l >= 0 && k < this.recipeWidth && l < this.recipeHeight) {
					if (parFlag) {
						itemstack = this.recipeItems[this.recipeWidth - k - 1 + l * this.recipeWidth];
					} else {
						itemstack = this.recipeItems[k + l * this.recipeWidth];
					}
				}

				ItemStack itemstack1 = parInventoryCrafting.getStackInRowAndColumn(i, j);
				if (itemstack1 != null || itemstack != null) {
					if (itemstack1 == null && itemstack != null || itemstack1 != null && itemstack == null) {
						return false;
					}

					if (itemstack.getItem() != itemstack1.getItem()) {
						return false;
					}

					if (itemstack.getMetadata() != 32767 && itemstack.getMetadata() != itemstack1.getMetadata()) {
						return false;
					}
				}
			}
		}

		return true;
	}

	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
		ItemStack itemstack = this.getRecipeOutput().copy();
		if (this.copyIngredientNBT) {
			for (int i = 0; i < inventorycrafting.getSizeInventory(); ++i) {
				ItemStack itemstack1 = inventorycrafting.getStackInSlot(i);
				if (itemstack1 != null && itemstack1.hasTagCompound()) {
					itemstack.setTagCompound((NBTTagCompound) itemstack1.getTagCompound().copy());
				}
			}
		}

		return itemstack;
	}

	public int getRecipeSize() {
		return this.recipeWidth * this.recipeHeight;
	}
}
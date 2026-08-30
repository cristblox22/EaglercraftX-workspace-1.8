package net.minecraft.item.crafting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class RecipeMatcher {

    public static boolean hasAnyIngredient(EntityPlayer player, IRecipe recipe) {
        List<ItemStack> requirements = getRecipeIngredients(recipe);
        if (requirements.isEmpty()) return true;

        for (ItemStack req : requirements) {
            if (req == null) continue;
            if (countItem(player.inventory, req) > 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean canCraft(EntityPlayer player, IRecipe recipe) {
        return getMissingIngredients(player, recipe).isEmpty();
    }

    public static List<ItemStack> getMissingIngredients(EntityPlayer player, IRecipe recipe) {
        List<ItemStack> missing = new ArrayList<ItemStack>();
        List<ItemStack> requirements = getRecipeIngredients(recipe);
        
        for (ItemStack req : requirements) {
            if (req == null) continue;
            int count = countItem(player.inventory, req);
            if (count < req.stackSize) {
                ItemStack missingItem = req.copy();
                missingItem.stackSize = req.stackSize - count;
                missing.add(missingItem);
            }
        }
        return missing;
    }

    private static List<ItemStack> getRecipeIngredients(IRecipe recipe) {
        List<ItemStack> list = new ArrayList<ItemStack>();
        
        if (recipe instanceof ShapedRecipes) {
            ShapedRecipes shaped = (ShapedRecipes) recipe;
            for (ItemStack item : shaped.recipeItems) {
                if (item != null) {
                    addItemToList(list, item);
                }
            }
        } else if (recipe instanceof ShapelessRecipes) {
            ShapelessRecipes shapeless = (ShapelessRecipes) recipe;
            for (Object obj : shapeless.recipeItems) {
                if (obj instanceof ItemStack) {
                    addItemToList(list, (ItemStack) obj);
                }
            }
        }
        return list;
    }

    private static void addItemToList(List<ItemStack> list, ItemStack toAdd) {
        for (ItemStack existing : list) {
            if (existing.getItem() == toAdd.getItem() && (existing.getMetadata() == 32767 || existing.getMetadata() == toAdd.getMetadata())) {
                existing.stackSize += toAdd.stackSize;
                return;
            }
        }
        list.add(toAdd.copy());
    }

    private static int countItem(IInventory inv, ItemStack target) {
        int count = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && stack.getItem() == target.getItem() && (target.getMetadata() == 32767 || stack.getMetadata() == target.getMetadata())) {
                count += stack.stackSize;
            }
        }
        return count;
    }
}
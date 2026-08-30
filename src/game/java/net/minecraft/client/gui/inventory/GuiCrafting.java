package net.minecraft.client.gui.inventory;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeMatcher;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GuiCrafting extends GuiContainer {
	private static final ResourceLocation craftingTableGuiTextures = new ResourceLocation(
			"textures/gui/container/crafting_table.png");
	private List<IRecipe> allRecipes = new ArrayList<IRecipe>();
	private List<IRecipe> displayableRecipes = new ArrayList<IRecipe>();
	private int scrollOffset = 0;

	public GuiCrafting(InventoryPlayer playerInv, World worldIn) {
		this(playerInv, worldIn, BlockPos.ORIGIN);
	}

	public GuiCrafting(InventoryPlayer playerInv, World worldIn, BlockPos blockPosition) {
		super(new ContainerWorkbench(playerInv, worldIn, blockPosition != null ? blockPosition : BlockPos.ORIGIN));
		this.allRecipes = CraftingManager.getInstance().getRecipeList();
		updateFilteredRecipes();
	}

	private void updateFilteredRecipes() {
		displayableRecipes.clear();
		if (this.mc != null && this.mc.thePlayer != null) {
			List<IRecipe> craftable = new ArrayList<IRecipe>();
			List<IRecipe> uncraftable = new ArrayList<IRecipe>();
			for (int i = 0; i < allRecipes.size(); ++i) {
				IRecipe recipe = (IRecipe)allRecipes.get(i);
				if (recipe != null && recipe.getRecipeOutput() != null) {
					if (RecipeMatcher.hasAnyIngredient(this.mc.thePlayer, recipe)) {
						if (RecipeMatcher.canCraft(this.mc.thePlayer, recipe)) {
							craftable.add(recipe);
						} else {
							uncraftable.add(recipe);
						}
					}
				}
			}
			displayableRecipes.addAll(craftable);
			displayableRecipes.addAll(uncraftable);
		}
	}

	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - this.xSize) / 2 + 50;
		updateFilteredRecipes();
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		int startX = this.guiLeft - 105;
		int startY = this.guiTop;
		
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		this.fontRendererObj.drawString("Recipe Book", startX + 6, startY + 6, 4210752);
		GlStateManager.enableDepth();
		GlStateManager.enableLighting();

		int xPos = startX + 6;
		int yPos = startY + 22;
		int renderedCount = 0;
		int maxVisible = 35;
		
		IRecipe hoveredRecipe = null;
		int hoverX = 0;
		int hoverY = 0;

		for (int i = scrollOffset; i < displayableRecipes.size() && renderedCount < maxVisible; ++i) {
			IRecipe recipe = (IRecipe)displayableRecipes.get(i);
			ItemStack output = recipe.getRecipeOutput();
			if (output == null) continue;

			int col = renderedCount % 5;
			int row = renderedCount / 5;
			int itemX = xPos + (col * 18);
			int itemY = yPos + (row * 18);

			boolean canCraft = RecipeMatcher.canCraft(this.mc.thePlayer, recipe);

			RenderHelper.enableGUIStandardItemLighting();
			this.itemRender.renderItemIntoGUI(output, itemX, itemY);
			
			if (!canCraft) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				drawRect(itemX, itemY, itemX + 16, itemY + 16, 0x80000000);
				GlStateManager.enableDepth();
				GlStateManager.enableLighting();
			}

			if (mouseX >= itemX && mouseX < itemX + 16 && mouseY >= itemY && mouseY < itemY + 16) {
				hoveredRecipe = recipe;
				hoverX = mouseX;
				hoverY = mouseY;
			}

			renderedCount++;
		}
		
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		int scrollX = startX + 95;
		int scrollY = startY + 22;
		int scrollHeight = 126;
		drawRect(scrollX, scrollY, scrollX + 3, scrollY + scrollHeight, 0xFF555555);
		
		int totalRows = (int) Math.ceil((double) displayableRecipes.size() / 5.0);
		int visibleRows = 7;
		int maxScrollOffset = displayableRecipes.size() - maxVisible;
		
		if (maxScrollOffset > 0) {
			int maxRowsToScroll = (int) Math.ceil(maxScrollOffset / 5.0);
			int currentRowScroll = scrollOffset / 5;
			int handleHeight = Math.max(10, (int)((float)visibleRows / totalRows * scrollHeight));
			int handleY = scrollY + (int)((float)currentRowScroll / maxRowsToScroll * (scrollHeight - handleHeight));
			drawRect(scrollX, handleY, scrollX + 3, handleY + handleHeight, 0xFFCCCCCC);
		} else {
			drawRect(scrollX, scrollY, scrollX + 3, scrollY + scrollHeight, 0xFFCCCCCC);
		}
		GlStateManager.enableDepth();
		GlStateManager.enableLighting();
		
		if (hoveredRecipe != null) {
			List<String> tooltip = new ArrayList<String>();
			tooltip.add(hoveredRecipe.getRecipeOutput().getDisplayName());
			boolean canCraft = RecipeMatcher.canCraft(this.mc.thePlayer, hoveredRecipe);
			if (!canCraft) {
				tooltip.add("\u00a7cMissing ingredients:");
				List<ItemStack> missing = RecipeMatcher.getMissingIngredients(this.mc.thePlayer, hoveredRecipe);
				for (int k = 0; k < missing.size(); ++k) {
					ItemStack m = missing.get(k);
					tooltip.add("\u00a77- " + m.stackSize + "x " + m.getDisplayName());
				}
			}
			this.drawHoveringText(tooltip, hoverX, hoverY);
		}
	}

	public void handleMouseInput() {
		try {
			super.handleMouseInput();
		} catch (Exception e) {
		}
		int wheel = Mouse.getEventDWheel();
		int maxVisible = 35;
		if (wheel != 0) {
			if (wheel > 0 && scrollOffset > 0) {
				scrollOffset -= 5;
			} else if (wheel < 0 && scrollOffset + maxVisible < displayableRecipes.size()) {
				scrollOffset += 5;
			}
		}
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		int startX = this.guiLeft - 105;
		int startY = this.guiTop;
		int xPos = startX + 6;
		int yPos = startY + 22;
		int renderedCount = 0;
		int maxVisible = 35;

		for (int i = scrollOffset; i < displayableRecipes.size() && renderedCount < maxVisible; ++i) {
			IRecipe recipe = (IRecipe)displayableRecipes.get(i);
			if (recipe.getRecipeOutput() == null) continue;

			int col = renderedCount % 5;
			int row = renderedCount / 5;
			int itemX = xPos + (col * 18);
			int itemY = yPos + (row * 18);

			if (mouseX >= itemX && mouseX < itemX + 16 && mouseY >= itemY && mouseY < itemY + 16) {
				List<ItemStack> missing = RecipeMatcher.getMissingIngredients(this.mc.thePlayer, recipe);
				if (!missing.isEmpty()) {
					this.mc.thePlayer.addChatMessage(new ChatComponentText("\u00a7eMissing for " + recipe.getRecipeOutput().getDisplayName() + ":"));
					for (int k = 0; k < missing.size(); ++k) {
						ItemStack req = missing.get(k);
						this.mc.thePlayer.addChatMessage(new ChatComponentText("\u00a77- " + req.stackSize + "x " + req.getDisplayName()));
					}
				} else {
					autoFillRecipe(recipe);
				}
				break;
			}
			renderedCount++;
		}
	}

	private void autoFillRecipe(IRecipe recipe) {
		for (int i = 1; i <= 9; i++) {
			if (this.inventorySlots.getSlot(i).getHasStack()) {
				this.mc.playerController.windowClick(this.inventorySlots.windowId, i, 0, 1, this.mc.thePlayer);
			}
		}

		if (recipe instanceof ShapedRecipes) {
			ShapedRecipes shaped = (ShapedRecipes) recipe;
			int w = shaped.recipeWidth;
			int h = shaped.recipeHeight;
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					ItemStack req = shaped.recipeItems[x + y * w];
					if (req != null) {
						int gridSlot = 1 + x + (y * 3);
						moveItemToGrid(req, gridSlot);
					}
				}
			}
		} else if (recipe instanceof ShapelessRecipes) {
			ShapelessRecipes shapeless = (ShapelessRecipes) recipe;
			int gridSlot = 1;
			for (Object obj : shapeless.recipeItems) {
				if (obj instanceof ItemStack) {
					moveItemToGrid((ItemStack) obj, gridSlot++);
				}
			}
		}
	}

	private void moveItemToGrid(ItemStack req, int gridSlot) {
		int invSlot = findItemInInventory(req);
		if (invSlot != -1) {
			this.mc.playerController.windowClick(this.inventorySlots.windowId, invSlot, 0, 0, this.mc.thePlayer);
			this.mc.playerController.windowClick(this.inventorySlots.windowId, gridSlot, 1, 0, this.mc.thePlayer);
			this.mc.playerController.windowClick(this.inventorySlots.windowId, invSlot, 0, 0, this.mc.thePlayer);
		}
	}

	private int findItemInInventory(ItemStack req) {
		for (int i = 10; i < 46; i++) {
			ItemStack stack = this.inventorySlots.getSlot(i).getStack();
			if (stack != null && stack.getItem() == req.getItem() && (req.getMetadata() == 32767 || stack.getMetadata() == req.getMetadata())) {
				return i;
			}
		}
		return -1;
	}

	protected void drawGuiContainerForegroundLayer(int var1, int var2) {
		this.fontRendererObj.drawString(I18n.format("container.crafting", new Object[0]), 28, 6, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2,
				4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(craftingTableGuiTextures);
		int i = this.guiLeft;
		int j = this.guiTop;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		
		int startX = i - 105;
		int startY = j;
		
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		drawRect(startX, startY, startX + 100, startY + 166, 0xFF000000);
		drawRect(startX + 1, startY + 1, startX + 99, startY + 165, 0xFFC6C6C6);
		GlStateManager.enableDepth();
		GlStateManager.enableLighting();
	}
}
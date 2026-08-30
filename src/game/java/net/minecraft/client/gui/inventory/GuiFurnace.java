package net.minecraft.client.gui.inventory;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;

public class GuiFurnace extends GuiContainer {
    private static final ResourceLocation furnaceGuiTextures = new ResourceLocation(
            "textures/gui/container/furnace.png");
    private final InventoryPlayer playerInventory;
    private IInventory tileFurnace;
    private List<ItemStack> displayableSmeltables = new ArrayList<ItemStack>();
    private int scrollOffset = 0;

    public GuiFurnace(InventoryPlayer playerInv, IInventory furnaceInv) {
        super(new ContainerFurnace(playerInv, furnaceInv));
        this.playerInventory = playerInv;
        this.tileFurnace = furnaceInv;
    }

    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2 + 50;
    }

    private void updateFilteredSmeltables() {
        displayableSmeltables.clear();
        if (this.mc != null && this.mc.thePlayer != null) {
            for (int i = 3; i < 39; i++) {
                ItemStack stack = this.inventorySlots.getSlot(i).getStack();
                if (stack != null) {
                    ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
                    if (result != null) {
                        boolean alreadyAdded = false;
                        for (ItemStack existing : displayableSmeltables) {
                            if (existing.getItem() == stack.getItem() && (existing.getMetadata() == 32767 || existing.getMetadata() == stack.getMetadata())) {
                                alreadyAdded = true;
                                break;
                            }
                        }
                        if (!alreadyAdded) {
                            ItemStack toAdd = stack.copy();
                            toAdd.stackSize = 1;
                            displayableSmeltables.add(toAdd);
                        }
                    }
                }
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        updateFilteredSmeltables();
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        int startX = this.guiLeft - 105;
        int startY = this.guiTop;
        
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        this.fontRendererObj.drawString("Smeltables", startX + 6, startY + 6, 4210752);
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();

        int xPos = startX + 6;
        int yPos = startY + 22;
        int renderedCount = 0;
        int maxVisible = 35;
        
        ItemStack hoveredItem = null;
        int hoverX = 0;
        int hoverY = 0;

        for (int i = scrollOffset; i < displayableSmeltables.size() && renderedCount < maxVisible; ++i) {
            ItemStack item = displayableSmeltables.get(i);
            int col = renderedCount % 5;
            int row = renderedCount / 5;
            int itemX = xPos + (col * 18);
            int itemY = yPos + (row * 18);

            RenderHelper.enableGUIStandardItemLighting();
            this.itemRender.renderItemIntoGUI(item, itemX, itemY);

            if (mouseX >= itemX && mouseX < itemX + 16 && mouseY >= itemY && mouseY < itemY + 16) {
                hoveredItem = item;
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
        
        int totalRows = (int) Math.ceil((double) displayableSmeltables.size() / 5.0);
        int visibleRows = 7;
        int maxScrollOffset = displayableSmeltables.size() - maxVisible;
        
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
        
        if (hoveredItem != null) {
            this.renderToolTip(hoveredItem, hoverX, hoverY);
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
            } else if (wheel < 0 && scrollOffset + maxVisible < displayableSmeltables.size()) {
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

        for (int i = scrollOffset; i < displayableSmeltables.size() && renderedCount < maxVisible; ++i) {
            ItemStack req = displayableSmeltables.get(i);
            int col = renderedCount % 5;
            int row = renderedCount / 5;
            int itemX = xPos + (col * 18);
            int itemY = yPos + (row * 18);

            if (mouseX >= itemX && mouseX < itemX + 16 && mouseY >= itemY && mouseY < itemY + 16) {
                moveItemToTopSlot(req);
                break;
            }
            renderedCount++;
        }
    }

    private void moveItemToTopSlot(ItemStack req) {
        int invSlot = -1;
        for (int i = 3; i < 39; i++) {
            ItemStack stack = this.inventorySlots.getSlot(i).getStack();
            if (stack != null && stack.getItem() == req.getItem() && (req.getMetadata() == 32767 || stack.getMetadata() == req.getMetadata())) {
                invSlot = i;
                break;
            }
        }
        if (invSlot != -1) {
            this.mc.playerController.windowClick(this.inventorySlots.windowId, invSlot, 0, 0, this.mc.thePlayer);
            this.mc.playerController.windowClick(this.inventorySlots.windowId, 0, 0, 0, this.mc.thePlayer);
            this.mc.playerController.windowClick(this.inventorySlots.windowId, invSlot, 0, 0, this.mc.thePlayer);
        }
    }

    protected void drawGuiContainerForegroundLayer(int var1, int var2) {
        String s = this.tileFurnace.getDisplayName().getUnformattedText();
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8,
                this.ySize - 96 + 2, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(furnaceGuiTextures);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        
        if (TileEntityFurnace.isBurning(this.tileFurnace)) {
            int k = this.getBurnLeftScaled(13);
            this.drawTexturedModalRect(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        int l = this.getCookProgressScaled(24);
        this.drawTexturedModalRect(i + 79, j + 34, 176, 14, l + 1, 16);
        
        int startX = i - 105;
        int startY = j;
        
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        drawRect(startX, startY, startX + 100, startY + 166, 0xFF000000);
        drawRect(startX + 1, startY + 1, startX + 99, startY + 165, 0xFFC6C6C6);
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }

    private int getCookProgressScaled(int pixels) {
        int i = this.tileFurnace.getField(2);
        int j = this.tileFurnace.getField(3);
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }

    private int getBurnLeftScaled(int pixels) {
        int i = this.tileFurnace.getField(1);
        if (i == 0) {
            i = 200;
        }

        return this.tileFurnace.getField(0) * pixels / i;
    }
}
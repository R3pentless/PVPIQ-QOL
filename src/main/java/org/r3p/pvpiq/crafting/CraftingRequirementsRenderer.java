package org.r3p.pvpiq.crafting;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.r3p.pvpiq.handlers.GuiEventHandler;

import java.util.HashMap;
import java.util.Map;

public class CraftingRequirementsRenderer {

    private Minecraft mc = Minecraft.getMinecraft();
    private GuiEventHandler guiEventHandler;

    public CraftingRequirementsRenderer(GuiEventHandler guiEventHandler) {
        this.guiEventHandler = guiEventHandler;
    }

    @SubscribeEvent
    public void onGuiDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (guiEventHandler.isCraftingGuiOpen()) {
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();

            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            renderCraftingRequirements();

            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
    }

    private void renderCraftingRequirements() {
        CraftingRecipe recipe = guiEventHandler.getCurrentRecipe();
        if (recipe == null) return;

        Map<String, Integer> requiredItems = recipe.getRequiredItems();
        Map<String, Integer> playerItems = new HashMap<>();
        for (ItemStack stack : mc.thePlayer.inventory.mainInventory) {
            if (stack != null && stack.hasDisplayName()) {
                String displayName = stack.getDisplayName();
                if (requiredItems.containsKey(displayName)) {
                    int count = playerItems.getOrDefault(displayName, 0) + stack.stackSize;
                    playerItems.put(displayName, count);
                }
            }
        }

        ScaledResolution sr = new ScaledResolution(mc);
        int x = sr.getScaledWidth() - 360;
        int y = sr.getScaledHeight() / 2 - 60;

        FontRenderer fontRenderer = mc.fontRendererObj;

        fontRenderer.drawStringWithShadow(EnumChatFormatting.GREEN + "Potrzebne przedmioty:", x, y, 0xFFFFFF);
        y += fontRenderer.FONT_HEIGHT + 2;

        for (Map.Entry<String, Integer> entry : requiredItems.entrySet()) {
            String itemName = entry.getKey();
            int requiredAmount = entry.getValue();
            int playerAmount = playerItems.getOrDefault(itemName, 0);

            String line = playerAmount + " / " + requiredAmount + " " + itemName;
            if (playerAmount >= requiredAmount) {
                fontRenderer.drawStringWithShadow(EnumChatFormatting.GREEN + line, x, y, 0xFFFFFF);
            } else {
                fontRenderer.drawStringWithShadow(EnumChatFormatting.RED + line, x, y, 0xFFFFFF);
            }
            y += fontRenderer.FONT_HEIGHT + 2;
        }
    }
}

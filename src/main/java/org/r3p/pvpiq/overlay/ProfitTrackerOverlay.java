package org.r3p.pvpiq.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.r3p.pvpiq.PvpIQ;
import org.r3p.pvpiq.config.ConfigHandler;

public class ProfitTrackerOverlay {

    private Minecraft mc = Minecraft.getMinecraft();
    private boolean dragging = false;
    private int dragOffsetX;
    private int dragOffsetY;
    private final int padding = 6;
    private final int margin = 4;
    private final int borderThickness = 2;
    private final int borderColor = 0xD3D3D3FF;

    private int profit;
    private int fishCount;
    private int chestCount;
    private int nemoCount;
    private int rozdymkaCount;

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (!ConfigHandler.trackerEnabled) return;
        boolean isEditing = PvpIQ.keyInputHandler.isEditing();
        renderOverlay(isEditing);
    }

    public void renderOverlay(boolean isEditing) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(ConfigHandler.scale * ConfigHandler.profitScale, ConfigHandler.scale * ConfigHandler.profitScale, 1);
        GlStateManager.translate(ConfigHandler.profitPosX / (ConfigHandler.scale * ConfigHandler.profitScale), ConfigHandler.profitPosY / (ConfigHandler.scale * ConfigHandler.profitScale), 0);
        drawBackground();
        int y = margin + padding;
        mc.fontRendererObj.drawString("§aTracker Lowienia", margin + padding, y, 0xFFFFFF);
        y += 12;
        mc.fontRendererObj.drawString("§7Zarobek: §f$" + profit, margin + padding, y, 0xFFFFFF);
        y += 12;
        mc.fontRendererObj.drawString("§7Liczba zlowionych ryb: §f" + fishCount, margin + padding, y, 0xFFFFFF);
        y += 12;
        mc.fontRendererObj.drawString("§7Skrzynie Rybaka: §f" + chestCount, margin + padding, y, 0xFFFFFF);
        y += 12;
        mc.fontRendererObj.drawString("§7Rozdymka: §f" + rozdymkaCount, margin + padding, y, 0xFFFFFF);
        y += 12;
        mc.fontRendererObj.drawString("§7Nemo: §f" + nemoCount, margin + padding, y, 0xFFFFFF);
        GlStateManager.popMatrix();
    }

    private void drawBackground() {
        int backgroundWidth = getBackgroundWidth();
        int backgroundHeight = 12 * 5 + padding * 2 + margin * 2 + padding;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.color(0.2F, 0.2F, 0.2F, 0.6F);
        Gui.drawRect(0, 0, backgroundWidth, backgroundHeight, 0x80000000);
        GlStateManager.color(0.0F, 1.0F, 1.0F, 1.0F);
        for (int i = 0; i < borderThickness; i++) {
            Gui.drawRect(i, i, backgroundWidth - i, i + 1, borderColor);
            Gui.drawRect(i, backgroundHeight - i - 1, backgroundWidth - i, backgroundHeight - i, borderColor);
            Gui.drawRect(i, i, i + 1, backgroundHeight - i, borderColor);
            Gui.drawRect(backgroundWidth - i - 1, i, backgroundWidth - i, backgroundHeight - i, borderColor);
        }
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    private int getBackgroundWidth() {
        String[] lines = {
                "§aTracker Lowienia",
                "§7Zarobek: §f$" + profit,
                "§7Liczba zlowionych ryb: §f" + fishCount,
                "§7Skrzynie Rybaka: §f" + chestCount,
                "§7Rozdymka: §f" + rozdymkaCount,
                "§7Nemo: §f" + nemoCount
        };

        int maxWidth = 0;
        for (String line : lines) {
            int lineWidth = mc.fontRendererObj.getStringWidth(line) + padding * 2;
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }
        return maxWidth + margin * 2;
    }

    public void handleMouseInput() {
        if (!ConfigHandler.trackerEnabled || !PvpIQ.keyInputHandler.isEditing()) return;

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int scaledWidth = scaledResolution.getScaledWidth();
        int scaledHeight = scaledResolution.getScaledHeight();

        int mouseX = Mouse.getEventX() * scaledWidth / mc.displayWidth;
        int mouseY = scaledHeight - Mouse.getEventY() * scaledHeight / mc.displayHeight - 1;

        int textHeight = 12 * 6 + margin * 2;
        int textWidth = getBackgroundWidth();

        if (mouseX >= ConfigHandler.profitPosX && mouseX <= ConfigHandler.profitPosX + textWidth
                && mouseY >= ConfigHandler.profitPosY && mouseY <= ConfigHandler.profitPosY + textHeight) {
            if (Mouse.getEventButton() == 0) {
                if (Mouse.getEventButtonState()) {
                    if (!dragging) {
                        dragging = true;
                        dragOffsetX = mouseX - ConfigHandler.profitPosX;
                        dragOffsetY = mouseY - ConfigHandler.profitPosY;
                    }
                } else {
                    dragging = false;
                }
            }

            if (dragging) {
                ConfigHandler.profitPosX = mouseX - dragOffsetX;
                ConfigHandler.profitPosY = mouseY - dragOffsetY;
                ConfigHandler.saveConfig();
            }
        }

        int wheel = Mouse.getDWheel();
        if (wheel != 0 && mouseX >= ConfigHandler.profitPosX && mouseX <= ConfigHandler.profitPosX + textWidth
                && mouseY >= ConfigHandler.profitPosY && mouseY <= ConfigHandler.profitPosY + textHeight) {
            if (wheel > 0) {
                ConfigHandler.saveProfitScale(ConfigHandler.profitScale + 0.1f);
                if (ConfigHandler.profitScale > 3.0f) ConfigHandler.profitScale = 3.0f;
            } else {
                ConfigHandler.saveProfitScale(ConfigHandler.profitScale - 0.1f);
                if (ConfigHandler.profitScale < 0.5f) ConfigHandler.profitScale = 0.5f;
            }
        }
    }

    public void addProfit(int amount) {
        if (!ConfigHandler.trackerEnabled) return;
        this.profit += amount;
        ConfigHandler.saveProfit(amount);
    }

    public void addFish() {
        if (!ConfigHandler.trackerEnabled) return;
        this.fishCount++;
    }

    public void addChest() {
        if (!ConfigHandler.trackerEnabled) return;
        this.chestCount++;
    }

    public void addNemo() {
        if (!ConfigHandler.trackerEnabled) return;
        this.nemoCount++;
        ConfigHandler.saveProfit(300000);
    }

    public void addRozdymka() {
        if (!ConfigHandler.trackerEnabled) return;
        this.rozdymkaCount++;
        ConfigHandler.saveProfit(125000);
    }

    public void resetTracker() {
        if (!ConfigHandler.trackerEnabled) return;
        profit = 0;
        fishCount = 0;
        chestCount = 0;
        nemoCount = 0;
        rozdymkaCount = 0;
    }
}

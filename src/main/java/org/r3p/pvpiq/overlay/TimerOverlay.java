package org.r3p.pvpiq.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.r3p.pvpiq.PvpIQ;
import org.r3p.pvpiq.boss.BossInfo;
import org.r3p.pvpiq.config.ConfigHandler;

import java.util.List;

public class TimerOverlay {

    private Minecraft mc = Minecraft.getMinecraft();

    private boolean dragging = false;
    private int dragOffsetX;
    private int dragOffsetY;
    private int timerHeight = 12;
    private final int margin = 4;
    private final int padding = 6;
    private final int borderThickness = 2;
    private final int borderColor = 0xD3D3D3FF;

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (!ConfigHandler.timerEnabled) return;
        boolean isEditing = PvpIQ.keyInputHandler.isEditing();
        renderOverlay(isEditing);
    }

    public void renderOverlay(boolean isEditing) {
        List<BossInfo> visibleBosses = PvpIQ.chatEventHandler.getVisibleBosses();
        if (visibleBosses.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.scale(ConfigHandler.scale, ConfigHandler.scale, 1);
        GlStateManager.translate(ConfigHandler.posX / ConfigHandler.scale, ConfigHandler.posY / ConfigHandler.scale, 0);
        drawBackground(visibleBosses);
        int y = margin + padding;
        for (BossInfo boss : visibleBosses) {
            String bossName = boss.getBossName();
            String displayString;
            long timeLeft = 0;
            if (PvpIQ.chatEventHandler.getBossTimers().containsKey(bossName)) {
                timeLeft = PvpIQ.chatEventHandler.getBossTimers().get(bossName) - System.currentTimeMillis();
                if (timeLeft <= 0) {
                    displayString = "§c" + bossName + "§r: §6Mozliwy Boss";
                } else {
                    String timeString = formatTime(timeLeft);
                    displayString = "§c" + bossName + "§r: §6" + timeString;
                }
            } else {
                displayString = "§c" + bossName + "§r: §6Mozliwy Boss";
            }
            mc.fontRendererObj.drawString(displayString, margin + padding, y, 0xFFFFFF);
            y += timerHeight;
        }
        GlStateManager.popMatrix();
    }

    public void handleMouseInput() {
        if (!ConfigHandler.timerEnabled || !PvpIQ.keyInputHandler.isEditing()) return;

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int scaledWidth = scaledResolution.getScaledWidth();
        int scaledHeight = scaledResolution.getScaledHeight();

        int mouseX = Mouse.getEventX() * scaledWidth / mc.displayWidth;
        int mouseY = scaledHeight - Mouse.getEventY() * scaledHeight / mc.displayHeight - 1;

        List<BossInfo> visibleBosses = PvpIQ.chatEventHandler.getVisibleBosses();
        int totalHeight = timerHeight * visibleBosses.size() + margin * 2;

        if (mouseX >= ConfigHandler.posX && mouseX <= ConfigHandler.posX + getBackgroundWidth(visibleBosses)
                && mouseY >= ConfigHandler.posY && mouseY <= ConfigHandler.posY + totalHeight) {
            if (Mouse.getEventButton() == 0) {
                if (Mouse.getEventButtonState()) {
                    if (!dragging) {
                        dragging = true;
                        dragOffsetX = mouseX - ConfigHandler.posX;
                        dragOffsetY = mouseY - ConfigHandler.posY;
                    }
                } else {
                    dragging = false;
                }
            }

            if (dragging) {
                ConfigHandler.posX = mouseX - dragOffsetX;
                ConfigHandler.posY = mouseY - dragOffsetY;
                ConfigHandler.saveConfig();
            }
        }
    }

    private void drawBackground(List<BossInfo> visibleBosses) {
        int backgroundWidth = getBackgroundWidth(visibleBosses);
        int backgroundHeight = timerHeight * visibleBosses.size() + padding * 2 + margin * 2;

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

    private int getBackgroundWidth(List<BossInfo> visibleBosses) {
        int maxWidth = 0;
        for (BossInfo boss : visibleBosses) {
            String bossName = boss.getBossName();
            String displayString;
            if (PvpIQ.chatEventHandler.getBossTimers().containsKey(bossName)) {
                long timeLeft = PvpIQ.chatEventHandler.getBossTimers().get(bossName) - System.currentTimeMillis();
                if (timeLeft <= 0) {
                    displayString = "§c" + bossName + "§r: §6Mozliwy Boss";
                } else {
                    String timeString = formatTime(timeLeft);
                    displayString = "§c" + bossName + "§r: §6" + timeString;
                }
            } else {
                displayString = "§c" + bossName + "§r: §6Mozliwy Boss";
            }
            int stringWidth = mc.fontRendererObj.getStringWidth(displayString) + padding * 2 + margin * 2;
            if (stringWidth > maxWidth) {
                maxWidth = stringWidth;
            }
        }
        return maxWidth;
    }

    private String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

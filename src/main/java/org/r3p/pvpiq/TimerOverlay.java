package org.r3p.pvpiq;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import java.util.List;

public class TimerOverlay {

    private Minecraft mc = Minecraft.getMinecraft();

    public int posX = ConfigHandler.posX;
    public int posY = ConfigHandler.posY;
    public float scale = ConfigHandler.scale;

    private boolean dragging = false;
    private int dragOffsetX;
    private int dragOffsetY;
    private int timerWidth = 200;
    private int timerHeight = 12;

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        boolean isEditing = PvpIQ.keyInputHandler.isEditing();
        renderTimers(isEditing);
    }

    public void renderTimers(boolean isEditing) {
        List<BossInfo> visibleBosses = PvpIQ.chatEventHandler.getVisibleBosses();
        if (visibleBosses.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.translate(posX / scale, posY / scale, 0);
        int y = 0;
        for (BossInfo boss : visibleBosses) {
            String bossName = boss.getBossName();
            String displayString;
            long timeLeft = 0;
            boolean hasActiveTimer = false;
            if (PvpIQ.chatEventHandler.getBossTimers().containsKey(bossName)) {
                timeLeft = PvpIQ.chatEventHandler.getBossTimers().get(bossName) - System.currentTimeMillis();
                if (timeLeft <= 0) {
                    displayString = "§c" + bossName + "§r: §6Mozliwy Boss";
                } else {
                    hasActiveTimer = true;
                    String timeString = formatTime(timeLeft);
                    displayString = "§c" + bossName + "§r: §6" + timeString;
                }
            } else {
                displayString = "§c" + bossName + "§r: §6Mozliwy Boss";
            }
            if (isEditing) {
                highlightTextBox(y);
            }
            mc.fontRendererObj.drawString(displayString, 0, y, 0xFFFFFF);
            y += 12;
        }
        GlStateManager.popMatrix();
    }

    public void handleMouseInput() {
        if (!PvpIQ.keyInputHandler.isEditing()) {
            return;
        }
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int scaledWidth = scaledResolution.getScaledWidth();
        int scaledHeight = scaledResolution.getScaledHeight();
        int mouseX = Mouse.getEventX() * scaledWidth / mc.displayWidth;
        int mouseY = scaledHeight - Mouse.getEventY() * scaledHeight / mc.displayHeight - 1;
        if (Mouse.getEventButton() == 0) {
            if (Mouse.getEventButtonState()) {
                if (!dragging) {
                    dragging = true;
                    dragOffsetX = mouseX - posX;
                    dragOffsetY = mouseY - posY;
                }
            } else {
                dragging = false;
            }
        }
        if (dragging) {
            posX = mouseX - dragOffsetX;
            posY = mouseY - dragOffsetY;
            ConfigHandler.saveConfig(posX, posY, scale);
        }
        int dWheel = Mouse.getDWheel();
        if (dWheel != 0) {
            if (dWheel > 0) {
                scale += 0.1f;
            } else {
                scale = Math.max(0.1f, scale - 0.1f);
            }
            ConfigHandler.saveConfig(posX, posY, scale);
        }
    }

    private void highlightTextBox(int yOffset) {
        int boxX1 = 0;
        int boxY1 = yOffset;
        int boxX2 = timerWidth;
        int boxY2 = yOffset + timerHeight;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.4F);
        Gui.drawRect(boxX1, boxY1, boxX2, boxY2, 0x40FFFFFF);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    private String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

package org.r3p.pvpiq.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;
import org.r3p.pvpiq.PvpIQ;
import org.r3p.pvpiq.config.ConfigHandler;

import java.io.IOException;

public class TimerEditGui extends GuiScreen {

    public TimerEditGui() {
    }

    @Override
    public void initGui() {
        Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
    }

    @Override
    public void onGuiClosed() {
        Minecraft.getMinecraft().mouseHelper.grabMouseCursor();
        PvpIQ.keyInputHandler.setEditing(false);
        ConfigHandler.saveConfig();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        PvpIQ.timerOverlay.handleMouseInput();
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();
        if (PvpIQ.editTimersKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        PvpIQ.timerOverlay.renderTimers(true);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

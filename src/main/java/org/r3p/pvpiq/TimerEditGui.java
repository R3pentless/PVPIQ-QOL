package org.r3p.pvpiq;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;

import java.io.IOException;

public class TimerEditGui extends GuiScreen {

    private TimerOverlay timerOverlay;

    public TimerEditGui() {
        this.timerOverlay = PvpIQ.timerOverlay;
    }

    @Override
    public void initGui() {
        Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
    }

    @Override
    public void onGuiClosed() {
        Minecraft.getMinecraft().mouseHelper.grabMouseCursor();
        PvpIQ.keyInputHandler.setEditing(false);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        timerOverlay.handleMouseInput();
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        timerOverlay.renderTimers(true);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

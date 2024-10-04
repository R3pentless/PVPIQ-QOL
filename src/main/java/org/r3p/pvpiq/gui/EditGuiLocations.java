package org.r3p.pvpiq.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;
import org.r3p.pvpiq.PvpIQ;
import org.r3p.pvpiq.config.ConfigHandler;

import java.io.IOException;

public class EditGuiLocations extends GuiScreen {

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
        PvpIQ.profitTrackerOverlay.handleMouseInput();
        PvpIQ.timerOverlay.handleMouseInput();
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();
        if (PvpIQ.editGuiLocationsKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        boolean isEditing = PvpIQ.keyInputHandler.isEditing();
        PvpIQ.profitTrackerOverlay.renderOverlay(isEditing);
        PvpIQ.timerOverlay.renderOverlay(isEditing);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

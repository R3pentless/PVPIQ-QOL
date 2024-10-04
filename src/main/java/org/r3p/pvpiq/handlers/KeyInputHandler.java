package org.r3p.pvpiq.handlers;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import org.r3p.pvpiq.PvpIQ;
import org.r3p.pvpiq.gui.EditGuiLocations;
import org.r3p.pvpiq.gui.FeaturesManagerGui;

public class KeyInputHandler {

    private boolean isEditing = false;

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (PvpIQ.modeSwitchKey.isPressed()) {
            PvpIQ.chatEventHandler.toggleMode();
        }
        if (PvpIQ.openGuiKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new FeaturesManagerGui());
        }
        if (PvpIQ.editGuiLocationsKey.isPressed()) {
            if (!isEditing) {
                isEditing = true;
                this.setEditing(true);
                Minecraft.getMinecraft().displayGuiScreen(new EditGuiLocations());
            } else {
                isEditing = false;
                this.setEditing(false);
                Minecraft.getMinecraft().displayGuiScreen(null);
            }
        }
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        this.isEditing = editing;
    }
}

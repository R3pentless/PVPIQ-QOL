package org.r3p.pvpiq.handlers;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import org.r3p.pvpiq.PvpIQ;
import org.r3p.pvpiq.gui.BossSelectionGui;
import org.r3p.pvpiq.gui.TimerEditGui;

public class KeyInputHandler {

    private boolean isEditing = false;

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (PvpIQ.modeSwitchKey.isPressed()) {
            PvpIQ.chatEventHandler.toggleMode();
        }
        if (PvpIQ.openGuiKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new BossSelectionGui());
        }
        if (PvpIQ.editTimersKey.isPressed()) {
            if (!isEditing) {
                isEditing = true;
                Minecraft.getMinecraft().displayGuiScreen(new TimerEditGui());
            } else {
                // Close edit mode if already editing
                isEditing = false;
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

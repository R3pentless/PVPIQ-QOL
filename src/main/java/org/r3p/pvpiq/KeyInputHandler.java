package org.r3p.pvpiq;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;

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
            Minecraft mc = Minecraft.getMinecraft();
            if (!isEditing) {
                isEditing = true;
                mc.displayGuiScreen(new TimerEditGui());
            } else {
                isEditing = false;
                mc.displayGuiScreen(null);
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

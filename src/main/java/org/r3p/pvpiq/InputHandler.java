package org.r3p.pvpiq;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;

public class InputHandler {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (PvpIQ.keyInputHandler.isEditing()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.inGameHasFocus) {
                mc.inGameHasFocus = false;
            }
            if (mc.thePlayer != null) {
                mc.thePlayer.movementInput.moveForward = 0;
                mc.thePlayer.movementInput.moveStrafe = 0;
                mc.thePlayer.movementInput.jump = false;
                mc.thePlayer.movementInput.sneak = false;
            }
        }
    }
}

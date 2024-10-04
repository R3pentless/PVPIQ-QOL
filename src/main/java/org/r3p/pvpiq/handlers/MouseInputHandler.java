package org.r3p.pvpiq.handlers;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.r3p.pvpiq.PvpIQ;

public class MouseInputHandler {

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        PvpIQ.timerOverlay.handleMouseInput();
    }
}

package org.r3p.pvpiq;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.r3p.pvpiq.config.ConfigHandler;
import org.r3p.pvpiq.crafting.CraftingRequirementsRenderer;
import org.r3p.pvpiq.handlers.*;
import org.r3p.pvpiq.overlay.TimerOverlay;

@Mod(modid = PvpIQ.MODID, name = PvpIQ.NAME, version = PvpIQ.VERSION, acceptedMinecraftVersions = "[1.8,1.8.9]", clientSideOnly = true)
public class PvpIQ {
    public static final String MODID = "pvpiq_qol";
    public static final String NAME = "PVPIQ - QOL";
    public static final String VERSION = "1.0";

    public static ChatEventHandler chatEventHandler = new ChatEventHandler();
    public static KeyInputHandler keyInputHandler = new KeyInputHandler();
    public static TimerOverlay timerOverlay = new TimerOverlay();
    public static GuiEventHandler guiEventHandler = new GuiEventHandler();
    public static CraftingRequirementsRenderer craftingRequirementsRenderer;

    public static KeyBinding modeSwitchKey;
    public static KeyBinding openGuiKey;
    public static KeyBinding editTimersKey;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(chatEventHandler);
        MinecraftForge.EVENT_BUS.register(keyInputHandler);
        MinecraftForge.EVENT_BUS.register(timerOverlay);
        MinecraftForge.EVENT_BUS.register(new InputHandler());
        MinecraftForge.EVENT_BUS.register(new MouseInputHandler());
        MinecraftForge.EVENT_BUS.register(guiEventHandler);

        craftingRequirementsRenderer = new CraftingRequirementsRenderer(guiEventHandler);
        MinecraftForge.EVENT_BUS.register(craftingRequirementsRenderer);

        modeSwitchKey = new KeyBinding("Toggle Mode", Keyboard.KEY_M, "PVPIQ - QOL");
        openGuiKey = new KeyBinding("Open Timer GUI", Keyboard.KEY_G, "PVPIQ - QOL");
        editTimersKey = new KeyBinding("Edit Timers", Keyboard.KEY_O, "PVPIQ - QOL");

        ClientRegistry.registerKeyBinding(modeSwitchKey);
        ClientRegistry.registerKeyBinding(openGuiKey);
        ClientRegistry.registerKeyBinding(editTimersKey);
    }
}

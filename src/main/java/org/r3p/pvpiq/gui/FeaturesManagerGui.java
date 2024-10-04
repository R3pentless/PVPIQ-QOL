package org.r3p.pvpiq.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.Minecraft;
import org.r3p.pvpiq.PvpIQ;
import org.r3p.pvpiq.boss.BossInfo;
import org.r3p.pvpiq.boss.BossManager;
import org.r3p.pvpiq.config.ConfigHandler;

import java.io.IOException;
import java.util.List;

public class FeaturesManagerGui extends GuiScreen {
    private GuiButton trackersButton;
    private GuiButton bossSelectorButton;
    private GuiButton alertsButton;
    private GuiButton doneButton;
    private int currentCategory = 0;
    private GuiButton trackerToggleButton;
    private GuiButton timerToggleButton;
    private GuiButton clearFishingTrackerButton;
    private GuiButton resetPositionScaleButton;
    private GuiButton alertsToggleButton;
    private GuiButton setAlertTimeButton;
    private GuiTextField alertTimeField;
    private List<BossInfo> bosses;

    @Override
    public void initGui() {
        int id = 0;
        this.buttonList.clear();
        trackersButton = new GuiButton(id++, this.width / 2 - 150, 20, 100, 20, "Trackery");
        bossSelectorButton = new GuiButton(id++, this.width / 2 - 50, 20, 100, 20, "Wybor Bossow");
        alertsButton = new GuiButton(id++, this.width / 2 + 50, 20, 100, 20, "Alerty");
        this.buttonList.add(trackersButton);
        this.buttonList.add(bossSelectorButton);
        this.buttonList.add(alertsButton);
        doneButton = new GuiButton(id++, this.width / 2 - 100, this.height - 40, 200, 20, "Gotowe");
        this.buttonList.add(doneButton);
        bosses = BossManager.getBosses();
        if (currentCategory == 0) {
            trackerToggleButton = new GuiButton(id++, this.width / 2 - 100, 60, 200, 20, "Wlacz Tracker: " + (ConfigHandler.trackerEnabled ? "ON" : "OFF"));
            timerToggleButton = new GuiButton(id++, this.width / 2 - 100, 90, 200, 20, "Wlacz Timer: " + (ConfigHandler.timerEnabled ? "ON" : "OFF"));
            clearFishingTrackerButton = new GuiButton(id++, this.width / 2 - 100, 120, 200, 20, "Wyczysc Tracker Polowow");
            resetPositionScaleButton = new GuiButton(id++, this.width / 2 - 100, 150, 200, 20, "Resetuj Pozycje i Skalowanie");
            this.buttonList.add(trackerToggleButton);
            this.buttonList.add(timerToggleButton);
            this.buttonList.add(clearFishingTrackerButton);
            this.buttonList.add(resetPositionScaleButton);
        } else if (currentCategory == 1) {
            int y = 60;
            for (BossInfo boss : bosses) {
                GuiButton bossButton = new GuiButton(id++, this.width / 2 - 100, y, 200, 20, getButtonLabel(boss));
                this.buttonList.add(bossButton);
                y += 25;
            }
        } else if (currentCategory == 2) {
            alertsToggleButton = new GuiButton(id++, this.width / 2 - 100, 60, 200, 20, "Wlacz Alerty: " + (ConfigHandler.showSpawnAlert ? "ON" : "OFF"));
            setAlertTimeButton = new GuiButton(id++, this.width / 2 - 100, 90, 200, 20, "Ustaw Czas Alertu (minuty)");
            this.buttonList.add(alertsToggleButton);
            this.buttonList.add(setAlertTimeButton);
            alertTimeField = new GuiTextField(id++, this.fontRendererObj, this.width / 2 - 50, 120, 100, 20);
            alertTimeField.setMaxStringLength(3);
            alertTimeField.setText(String.valueOf(ConfigHandler.alertBeforeMinutes));
        }
    }

    private String getButtonLabel(BossInfo boss) {
        boolean isVisible = PvpIQ.chatEventHandler.isBossVisible(boss.getBossName());
        return (isVisible ? "[X] " : "[ ] ") + boss.getBossName();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == doneButton) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        } else if (button == trackersButton) {
            currentCategory = 0;
            initGui();
        } else if (button == bossSelectorButton) {
            currentCategory = 1;
            initGui();
        } else if (button == alertsButton) {
            currentCategory = 2;
            initGui();
        } else if (button == trackerToggleButton) {
            ConfigHandler.trackerEnabled = !ConfigHandler.trackerEnabled;
            trackerToggleButton.displayString = "Wlacz Tracker: " + (ConfigHandler.trackerEnabled ? "ON" : "OFF");
            ConfigHandler.saveConfig();
        } else if (button == timerToggleButton) {
            ConfigHandler.timerEnabled = !ConfigHandler.timerEnabled;
            timerToggleButton.displayString = "Wlacz Timer: " + (ConfigHandler.timerEnabled ? "ON" : "OFF");
            ConfigHandler.saveConfig();
        } else if (button == clearFishingTrackerButton) {
            PvpIQ.profitTrackerOverlay.resetTracker();
        } else if (button == resetPositionScaleButton) {
            ConfigHandler.posX = 10;
            ConfigHandler.posY = 10;
            ConfigHandler.profitPosX = 300;
            ConfigHandler.profitPosY = 300;
            ConfigHandler.scale = 1.0f;
            ConfigHandler.profitScale = 1.0f;
            ConfigHandler.saveConfig();
            initGui();
        } else if (button == alertsToggleButton) {
            ConfigHandler.showSpawnAlert = !ConfigHandler.showSpawnAlert;
            alertsToggleButton.displayString = "Wlacz Alerty: " + (ConfigHandler.showSpawnAlert ? "ON" : "OFF");
            ConfigHandler.saveAlertSettings(ConfigHandler.showSpawnAlert, ConfigHandler.alertBeforeMinutes);
        } else if (button == setAlertTimeButton) {
            if (alertTimeField.getText().matches("\\d+")) {
                int time = Integer.parseInt(alertTimeField.getText());
                ConfigHandler.alertBeforeMinutes = time;
                ConfigHandler.saveAlertSettings(ConfigHandler.showSpawnAlert, ConfigHandler.alertBeforeMinutes);
                initGui();
            }
        } else {
            for (BossInfo boss : bosses) {
                if (button.displayString.contains(boss.getBossName())) {
                    PvpIQ.chatEventHandler.toggleBossVisibility(boss.getBossName());
                    button.displayString = getButtonLabel(boss);
                    break;
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (currentCategory == 2 && alertTimeField != null) {
            alertTimeField.textboxKeyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        if (currentCategory == 0) {
            this.drawCenteredString(this.fontRendererObj, "Trackery", this.width / 2, 10, 0xFFFFFF);
        } else if (currentCategory == 1) {
            this.drawCenteredString(this.fontRendererObj, "Wybor Bossow", this.width / 2, 10, 0xFFFFFF);
        } else if (currentCategory == 2) {
            this.drawCenteredString(this.fontRendererObj, "Alerty", this.width / 2, 10, 0xFFFFFF);
            if (alertTimeField != null) {
                alertTimeField.drawTextBox();
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        if (currentCategory == 2 && alertTimeField != null) {
            alertTimeField.updateCursorCounter();
        }
        super.updateScreen();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (currentCategory == 2 && alertTimeField != null) {
            alertTimeField.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}

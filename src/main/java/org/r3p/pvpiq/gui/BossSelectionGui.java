package org.r3p.pvpiq.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.Minecraft;
import org.r3p.pvpiq.PvpIQ;
import org.r3p.pvpiq.boss.BossInfo;
import org.r3p.pvpiq.boss.BossManager;
import org.r3p.pvpiq.config.ConfigHandler;

import java.io.IOException;
import java.util.List;

public class BossSelectionGui extends GuiScreen {
    private GuiButton doneButton;
    private GuiButton alertToggleButton;
    private List<BossInfo> bosses;

    @Override
    public void initGui() {
        int id = 0;
        int y = 20;
        bosses = BossManager.getBosses();

        for (BossInfo boss : bosses) {
            GuiButton button = new GuiButton(id++, this.width / 2 - 100, y, 200, 20, getButtonLabel(boss));
            this.buttonList.add(button);
            y += 25;
        }

        alertToggleButton = new GuiButton(id++, this.width / 2 - 100, y + 10, 200, 20, getAlertButtonLabel());
        this.buttonList.add(alertToggleButton);

        doneButton = new GuiButton(id++, this.width / 2 - 100, y + 40, 200, 20, "Gotowe");
        this.buttonList.add(doneButton);
    }

    private String getButtonLabel(BossInfo boss) {
        boolean isVisible = PvpIQ.chatEventHandler.isBossVisible(boss.getBossName());
        return (isVisible ? "[X] " : "[ ] ") + boss.getBossName();
    }

    private String getAlertButtonLabel() {
        return "Alerty o spawnie: " + (ConfigHandler.showSpawnAlert ? "ON" : "OFF");
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == doneButton) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        } else if (button == alertToggleButton) {
            ConfigHandler.showSpawnAlert = !ConfigHandler.showSpawnAlert;
            ConfigHandler.saveAlertSettings(ConfigHandler.showSpawnAlert, ConfigHandler.alertBeforeMinutes);
            alertToggleButton.displayString = getAlertButtonLabel();
        } else {
            int index = button.id;
            BossInfo boss = bosses.get(index);
            PvpIQ.chatEventHandler.toggleBossVisibility(boss.getBossName());
            button.displayString = getButtonLabel(boss);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Wybierz bossy do trackowania", this.width / 2, 10, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

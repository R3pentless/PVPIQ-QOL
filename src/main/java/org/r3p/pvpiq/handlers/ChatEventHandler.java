package org.r3p.pvpiq.handlers;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.r3p.pvpiq.boss.BossInfo;
import org.r3p.pvpiq.boss.BossManager;
import org.r3p.pvpiq.config.ConfigHandler;

import java.text.Normalizer;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class ChatEventHandler {
    private Map<String, Long> bossTimers = new HashMap<>();
    private boolean isAkademiaMode = false;
    private Map<String, Boolean> alertSent = new HashMap<>();

    public ChatEventHandler() {
        isAkademiaMode = ConfigHandler.isAkademiaMode;
    }

    public void toggleMode() {
        isAkademiaMode = !isAkademiaMode;
        String mode = isAkademiaMode ? "Akademia" : "Glowny Server";
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Przelaczono na tryb " + mode + "."));
        ConfigHandler.isAkademiaMode = isAkademiaMode;
        ConfigHandler.saveMode();
    }

    public boolean isAkademiaMode() {
        return isAkademiaMode;
    }

    public void toggleBossVisibility(String bossName) {
        if (ConfigHandler.visibleBosses.contains(bossName)) {
            ConfigHandler.visibleBosses.remove(bossName);
        } else {
            ConfigHandler.visibleBosses.add(bossName);
        }
        ConfigHandler.saveVisibleBosses();
    }

    public boolean isBossVisible(String bossName) {
        if (ConfigHandler.visibleBosses == null || ConfigHandler.visibleBosses.isEmpty()) {
            return true;
        }
        return ConfigHandler.visibleBosses.contains(bossName);
    }

    public Map<String, Long> getBossTimers() {
        return bossTimers;
    }

    public List<BossInfo> getVisibleBosses() {
        List<BossInfo> visibleBossesList = new ArrayList<>();
        for (BossInfo boss : BossManager.getBosses()) {
            if (isBossVisible(boss.getBossName())) {
                visibleBossesList.add(boss);
            }
        }
        return visibleBossesList;
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        if (message.contains("[Lvl ")) {
            return;
        }
        String messageNoDiacritics = removeDiacritics(message).toLowerCase();
        for (BossInfo boss : BossManager.getBosses()) {
            List<String> namesToCheck = new ArrayList<>();
            namesToCheck.add(boss.getBossName());
            namesToCheck.addAll(boss.getAliases());
            for (String name : namesToCheck) {
                String nameNoDiacritics = removeDiacritics(name).toLowerCase();
                if (messageNoDiacritics.contains("zabil " + nameNoDiacritics) ||
                        messageNoDiacritics.contains("pokonal " + nameNoDiacritics)) {
                    int waitingTime = boss.getWaitingTime();
                    if (isAkademiaMode) {
                        waitingTime /= 2;
                    }
                    bossTimers.put(boss.getBossName(), System.currentTimeMillis() + waitingTime * 60000);
                    alertSent.put(boss.getBossName(), false);
                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                            new ChatComponentText("§cTimer rozpoczal sie dla " + boss.getBossName() + ": " + waitingTime + " minut.")
                    );
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!ConfigHandler.showSpawnAlert) return;
        for (Map.Entry<String, Long> entry : bossTimers.entrySet()) {
            String bossName = entry.getKey();
            long timeLeft = entry.getValue() - System.currentTimeMillis();
            if (timeLeft <= 0) {
                // Timer expired
            } else {
                long minutesLeft = timeLeft / 60000;
                if (minutesLeft <= ConfigHandler.alertBeforeMinutes && !alertSent.getOrDefault(bossName, false)) {
                    sendSpawnAlert(bossName);
                    alertSent.put(bossName, true);
                }
            }
        }
    }

    private void sendSpawnAlert(String bossName) {
        Minecraft mc = Minecraft.getMinecraft();
        String alertMessage = "§c" + bossName + " pojawi sie za " + ConfigHandler.alertBeforeMinutes + " minut!";
        mc.thePlayer.addChatMessage(new ChatComponentText(alertMessage));
        mc.ingameGUI.displayTitle("§6" + bossName + " §cpojawi sie wkrotce!", null, 10, 70, 20);
    }

    private String removeDiacritics(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
}

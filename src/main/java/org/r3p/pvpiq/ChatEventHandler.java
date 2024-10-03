package org.r3p.pvpiq;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.text.Normalizer;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class ChatEventHandler {
    private Map<String, Long> bossTimers = new HashMap<>();
    private boolean isAkademiaMode = false;
    private Map<String, Boolean> visibleBosses = new HashMap<>();
    private Map<String, Boolean> alertSent = new HashMap<>();

    public ChatEventHandler() {
        for (BossInfo boss : BossManager.getBosses()) {
            boolean isVisible = true;
            if (ConfigHandler.visibleBosses != null && !ConfigHandler.visibleBosses.isEmpty()) {
                isVisible = ConfigHandler.visibleBosses.contains(boss.getBossName());
            }
            visibleBosses.put(boss.getBossName(), isVisible);
        }
        isAkademiaMode = ConfigHandler.isAkademiaMode;
    }

    public void toggleMode() {
        isAkademiaMode = !isAkademiaMode;
        String mode = isAkademiaMode ? "Akademia" : "Glowny Server";
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Przelaczono na tryb " + mode + "."));
        ConfigHandler.saveMode(isAkademiaMode);
    }

    public boolean isAkademiaMode() {
        return isAkademiaMode;
    }

    public void toggleBossVisibility(String bossName) {
        boolean currentVisibility = visibleBosses.getOrDefault(bossName, true);
        visibleBosses.put(bossName, !currentVisibility);
        saveVisibleBossesToConfig();
    }

    private void saveVisibleBossesToConfig() {
        List<String> bossesToSave = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : visibleBosses.entrySet()) {
            if (entry.getValue()) {
                bossesToSave.add(entry.getKey());
            }
        }
        ConfigHandler.saveVisibleBosses(bossesToSave);
    }

    public boolean isBossVisible(String bossName) {
        return visibleBosses.getOrDefault(bossName, true);
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

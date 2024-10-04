package org.r3p.pvpiq.handlers;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.r3p.pvpiq.boss.BossInfo;
import org.r3p.pvpiq.boss.BossManager;
import org.r3p.pvpiq.config.ConfigHandler;
import org.r3p.pvpiq.PvpIQ;

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

        if (message.startsWith("Polow zakonczony powodzeniem")) {
            calculatePriceFromMessage(message);
        }
    }

    private void calculatePriceFromMessage(String message) {
        String cleanedMessage = message.replaceAll("§[0-9a-fk-or]", "");

        String[] parts = cleanedMessage.split("wylowiles:");
        if (parts.length < 2) return;

        String fishInfo = parts[1].trim();

        if (fishInfo.contains("Rozdymka")) {
            PvpIQ.profitTrackerOverlay.addRozdymka();
            return;
        } else if (fishInfo.contains("Rybka Nemo")) {
            PvpIQ.profitTrackerOverlay.addNemo();
            return;
        } else if (fishInfo.contains("Skrzynia Rybaka")) {
            PvpIQ.profitTrackerOverlay.addChest();
            return;
        }

        String fishName = fishInfo.split("\\(Ciezar")[0].trim();

        int weight = 0;
        int length = 0;

        java.util.regex.Matcher weightMatcher = java.util.regex.Pattern.compile("\\(\\s*Ciezar\\s*:\\s*(\\d+)g\\s*\\)").matcher(fishInfo);
        if (weightMatcher.find()) {
            weight = Integer.parseInt(weightMatcher.group(1));
        }

        java.util.regex.Matcher lengthMatcher = java.util.regex.Pattern.compile("\\(\\s*Dlugosc\\s*:\\s*(\\d+)cm\\s*\\)").matcher(fishInfo);
        if (lengthMatcher.find()) {
            length = Integer.parseInt(lengthMatcher.group(1));
        }

        int price = calculateFishPrice(fishName, weight, length);

        PvpIQ.profitTrackerOverlay.addProfit(price);
        PvpIQ.profitTrackerOverlay.addFish();
    }

    private int calculateFishPrice(String fishName, int weight, int length) {
        int basePrice = 0;
        int lengthMultiplier = 0;
        int weightMultiplier = 0;

        switch (fishName) {
            case "Surowa Ryba":
                basePrice = 2000;
                break;
            case "Pieczona Ryba":
                basePrice = 4000;
                break;
            case "Surowy Losos":
                basePrice = 10000;
                break;
            case "Pieczony Losos":
                basePrice = 35000;
                break;
            case "Karas":
                basePrice = 1000;
                lengthMultiplier = 12;
                weightMultiplier = 5;
                break;
            case "Losos":
                basePrice = 2200;
                lengthMultiplier = 16;
                weightMultiplier = 6;
                break;
            case "Karp":
                basePrice = 8000;
                lengthMultiplier = 20;
                weightMultiplier = 8;
                break;
            case "Okon":
                basePrice = 25000;
                lengthMultiplier = 40;
                weightMultiplier = 12;
                break;
            default:
                basePrice = 1000;
                break;
        }

        int finalPrice = basePrice + (length * lengthMultiplier) + (weight * weightMultiplier);
        return finalPrice;
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

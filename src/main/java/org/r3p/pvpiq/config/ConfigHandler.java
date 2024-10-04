package org.r3p.pvpiq.config;

import net.minecraftforge.common.config.Configuration;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class ConfigHandler {

    private static Configuration config;

    public static int posX = 10;
    public static int posY = 10;
    public static float scale = 1.0f;

    public static int profitPosX = 300;
    public static int profitPosY = 300;
    public static float profitScale = 1.0f;
    public static int totalProfit = 0;

    public static boolean isAkademiaMode = false;
    public static List<String> visibleBosses = new ArrayList<>();
    public static boolean showSpawnAlert = true;
    public static int alertBeforeMinutes = 2;

    public static boolean trackerEnabled = true;
    public static boolean timerEnabled = true;

    public static void init(File configFile) {
        config = new Configuration(configFile);
        loadConfig();
    }

    public static void loadConfig() {
        posX = config.getInt("posX", Configuration.CATEGORY_GENERAL, 10, 0, Integer.MAX_VALUE, "Timer X Position");
        posY = config.getInt("posY", Configuration.CATEGORY_GENERAL, 10, 0, Integer.MAX_VALUE, "Timer Y Position");
        scale = (float) config.get(Configuration.CATEGORY_GENERAL, "scale", 1.0, "Timer Scale").getDouble(1.0);

        profitPosX = config.getInt("profitPosX", Configuration.CATEGORY_GENERAL, 300, 0, Integer.MAX_VALUE, "Profit Tracker X Position");
        profitPosY = config.getInt("profitPosY", Configuration.CATEGORY_GENERAL, 300, 0, Integer.MAX_VALUE, "Profit Tracker Y Position");
        profitScale = (float) config.get(Configuration.CATEGORY_GENERAL, "profitScale", 1.0, "Profit Tracker Scale").getDouble(1.0);
        totalProfit = config.getInt("totalProfit", Configuration.CATEGORY_GENERAL, 0, 0, Integer.MAX_VALUE, "Total Profit");

        isAkademiaMode = config.getBoolean("isAkademiaMode", Configuration.CATEGORY_GENERAL, false, "Is Akademia Mode");
        String[] savedBosses = config.getStringList("visibleBosses", Configuration.CATEGORY_GENERAL, new String[]{}, "Visible Bosses");
        visibleBosses = new ArrayList<>(Arrays.asList(savedBosses));
        showSpawnAlert = config.getBoolean("showSpawnAlert", Configuration.CATEGORY_GENERAL, true, "Show Boss Spawn Alert");
        alertBeforeMinutes = config.getInt("alertBeforeMinutes", Configuration.CATEGORY_GENERAL, 2, 1, Integer.MAX_VALUE, "Minutes before spawn to alert");

        trackerEnabled = config.getBoolean("trackerEnabled", Configuration.CATEGORY_GENERAL, true, "Enable Tracker");
        timerEnabled = config.getBoolean("timerEnabled", Configuration.CATEGORY_GENERAL, true, "Enable Timer");

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void saveConfig() {
        config.get(Configuration.CATEGORY_GENERAL, "posX", posX).set(posX);
        config.get(Configuration.CATEGORY_GENERAL, "posY", posY).set(posY);
        config.get(Configuration.CATEGORY_GENERAL, "scale", scale).set(scale);

        config.get(Configuration.CATEGORY_GENERAL, "profitPosX", profitPosX).set(profitPosX);
        config.get(Configuration.CATEGORY_GENERAL, "profitPosY", profitPosY).set(profitPosY);
        config.get(Configuration.CATEGORY_GENERAL, "profitScale", profitScale).set(profitScale);
        config.get(Configuration.CATEGORY_GENERAL, "totalProfit", totalProfit).set(totalProfit);

        config.get(Configuration.CATEGORY_GENERAL, "isAkademiaMode", isAkademiaMode).set(isAkademiaMode);
        config.get(Configuration.CATEGORY_GENERAL, "visibleBosses", new String[]{}).set(visibleBosses.toArray(new String[0]));
        config.get(Configuration.CATEGORY_GENERAL, "showSpawnAlert", showSpawnAlert).set(showSpawnAlert);
        config.get(Configuration.CATEGORY_GENERAL, "alertBeforeMinutes", alertBeforeMinutes).set(alertBeforeMinutes);

        config.get(Configuration.CATEGORY_GENERAL, "trackerEnabled", trackerEnabled).set(trackerEnabled);
        config.get(Configuration.CATEGORY_GENERAL, "timerEnabled", timerEnabled).set(timerEnabled);

        config.save();
    }

    public static void saveMode() {
        config.get(Configuration.CATEGORY_GENERAL, "isAkademiaMode", isAkademiaMode).set(isAkademiaMode);
        config.save();
    }

    public static void saveVisibleBosses() {
        config.get(Configuration.CATEGORY_GENERAL, "visibleBosses", new String[]{}).set(visibleBosses.toArray(new String[0]));
        config.save();
    }

    public static void saveProfit(int profit) {
        if (!trackerEnabled) return;
        totalProfit += profit;
        config.get(Configuration.CATEGORY_GENERAL, "totalProfit", totalProfit).set(totalProfit);
        config.save();
    }

    public static void saveProfitScale(float scale) {
        profitScale = scale;
        config.get(Configuration.CATEGORY_GENERAL, "profitScale", profitScale).set(profitScale);
        config.save();
    }

    public static void saveAlertSettings(boolean showAlert, int minutesBefore) {
        showSpawnAlert = showAlert;
        alertBeforeMinutes = minutesBefore;
        saveConfig();
    }

    public static void clearTracker() {
        if (!trackerEnabled) return;
        totalProfit = 0;
        saveConfig();
    }
}

package org.r3p.pvpiq;

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
    public static boolean isAkademiaMode = false;
    public static List<String> visibleBosses = new ArrayList<>();
    public static boolean showSpawnAlert = true;
    public static int alertBeforeMinutes = 2;

    public static void init(File configFile) {
        config = new Configuration(configFile);
        loadConfig();
    }

    public static void loadConfig() {
        posX = config.getInt("posX", Configuration.CATEGORY_GENERAL, 10, 0, Integer.MAX_VALUE, "Timer X Position");
        posY = config.getInt("posY", Configuration.CATEGORY_GENERAL, 10, 0, Integer.MAX_VALUE, "Timer Y Position");
        scale = (float) config.get(Configuration.CATEGORY_GENERAL, "scale", 1.0, "Timer Scale").getDouble(1.0);

        isAkademiaMode = config.getBoolean("isAkademiaMode", Configuration.CATEGORY_GENERAL, false, "Is Akademia Mode");

        String[] defaultBosses = {};
        String[] savedBosses = config.getStringList("visibleBosses", Configuration.CATEGORY_GENERAL, defaultBosses, "Visible Bosses");

        visibleBosses = new ArrayList<>(Arrays.asList(savedBosses));

        showSpawnAlert = config.getBoolean("showSpawnAlert", Configuration.CATEGORY_GENERAL, true, "Show Boss Spawn Alert");
        alertBeforeMinutes = config.getInt("alertBeforeMinutes", Configuration.CATEGORY_GENERAL, 2, 1, Integer.MAX_VALUE, "Minutes before spawn to alert");

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void saveConfig(int x, int y, float s) {
        config.get(Configuration.CATEGORY_GENERAL, "posX", x).set(x);
        config.get(Configuration.CATEGORY_GENERAL, "posY", y).set(y);
        config.get(Configuration.CATEGORY_GENERAL, "scale", s).set(s);
        config.save();
    }

    public static void saveMode(boolean isAkademiaMode) {
        config.get(Configuration.CATEGORY_GENERAL, "isAkademiaMode", isAkademiaMode).set(isAkademiaMode);
        config.save();
    }

    public static void saveVisibleBosses(List<String> bosses) {
        config.get(Configuration.CATEGORY_GENERAL, "visibleBosses", new String[]{}).set(bosses.toArray(new String[0]));
        config.save();
    }

    public static void saveAlertSettings(boolean showAlert, int minutesBefore) {
        config.get(Configuration.CATEGORY_GENERAL, "showSpawnAlert", showAlert).set(showAlert);
        config.get(Configuration.CATEGORY_GENERAL, "alertBeforeMinutes", minutesBefore).set(minutesBefore);
        config.save();
    }
}

package org.r3p.pvpiq.boss;

import java.util.List;

public class BossInfo {
    private String bossName;
    private List<String> aliases;
    private int waitingTime;

    public BossInfo(String bossName, List<String> aliases, int waitingTime) {
        this.bossName = bossName;
        this.aliases = aliases;
        this.waitingTime = waitingTime;
    }

    public String getBossName() {
        return bossName;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public int getWaitingTime() {
        return waitingTime;
    }
}

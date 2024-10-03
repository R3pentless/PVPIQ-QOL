package org.r3p.pvpiq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BossManager {
    private static List<BossInfo> bosses = new ArrayList<>();

    static {
        bosses.add(new BossInfo("Sniezny Lord", Arrays.asList("Snieznego Lorda"), 5));
        bosses.add(new BossInfo("Lesny Wladca", Arrays.asList("Lesnego Wladce"), 20));
        bosses.add(new BossInfo("Zmutowana Jaszczura", Arrays.asList("Zmutowana Jaszczurke"), 30));
        bosses.add(new BossInfo("Wladca Ptasznikow", Arrays.asList("Wladce Ptasznikow"), 30));
        bosses.add(new BossInfo("Ognisty Krol", Arrays.asList("Ognistego Krola"), 42));
        bosses.add(new BossInfo("Przywodca Klanu Zlodziei", Arrays.asList("Przywodce Klanu Zlodziei"), 60));
        bosses.add(new BossInfo("Krolowa Pajakow", Arrays.asList("Krolowa Pajakow"), 30));
        bosses.add(new BossInfo("Dzikolud", Arrays.asList("Dzikoluda"), 40));
        bosses.add(new BossInfo("Przywodca Orkow", Arrays.asList("Przywodce Orkow"), 90));
    }

    public static List<BossInfo> getBosses() {
        return bosses;
    }
}

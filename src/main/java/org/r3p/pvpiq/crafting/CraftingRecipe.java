package org.r3p.pvpiq.crafting;

import java.util.Map;

public class CraftingRecipe {
    private String guiName;
    private Map<String, Integer> requiredItems;

    public CraftingRecipe(String guiName, Map<String, Integer> requiredItems) {
        this.guiName = guiName;
        this.requiredItems = requiredItems;
    }

    public String getGuiName() {
        return guiName;
    }

    public Map<String, Integer> getRequiredItems() {
        return requiredItems;
    }
}

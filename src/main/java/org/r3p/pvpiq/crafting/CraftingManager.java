package org.r3p.pvpiq.crafting;

import net.minecraft.util.EnumChatFormatting;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class CraftingManager {
    private static List<CraftingRecipe> recipes = new ArrayList<>();

    static {
        Map<String, Integer> miedzianyPierscienItems = new HashMap<>();
        miedzianyPierscienItems.put(EnumChatFormatting.BLUE + "Esencja Ducha", 1);
        miedzianyPierscienItems.put(EnumChatFormatting.DARK_PURPLE + "Oko Wladcy Ptasznikow", 10);
        miedzianyPierscienItems.put(EnumChatFormatting.DARK_GRAY + "Czarny Piach", 5);
        miedzianyPierscienItems.put(EnumChatFormatting.BLUE + "Krysztal", 10);
        miedzianyPierscienItems.put(EnumChatFormatting.DARK_GRAY + "" + EnumChatFormatting.BOLD + "Magiczne Ziolo Regeneracji", 1);
        addRecipe(new CraftingRecipe("§6Crafting: §8§lMiedziany Pierscien", miedzianyPierscienItems));

        // Add more recipes here
    }

    public static void addRecipe(CraftingRecipe recipe) {
        recipes.add(recipe);
    }

    public static CraftingRecipe getRecipeByGuiName(String guiName) {
        for (CraftingRecipe recipe : recipes) {
            if (recipe.getGuiName().equals(guiName)) {
                return recipe;
            }
        }
        return null;
    }
}

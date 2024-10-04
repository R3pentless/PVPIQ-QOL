package org.r3p.pvpiq.handlers;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import org.r3p.pvpiq.crafting.CraftingManager;
import org.r3p.pvpiq.crafting.CraftingRecipe;

public class GuiEventHandler {

    private boolean isCraftingGuiOpen = false;
    private CraftingRecipe currentRecipe = null;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiChest) {
            GuiChest chestGui = (GuiChest) event.gui;
            if (chestGui.inventorySlots instanceof ContainerChest) {
                ContainerChest containerChest = (ContainerChest) chestGui.inventorySlots;
                IInventory chestInventory = containerChest.getLowerChestInventory();
                String chestTitle = chestInventory.getDisplayName().getUnformattedText();

                currentRecipe = CraftingManager.getRecipeByGuiName(chestTitle);
                isCraftingGuiOpen = currentRecipe != null;
            }
        } else {
            isCraftingGuiOpen = false;
            currentRecipe = null;
        }
    }

    public boolean isCraftingGuiOpen() {
        return isCraftingGuiOpen;
    }

    public CraftingRecipe getCurrentRecipe() {
        return currentRecipe;
    }
}

package com.airzy.cosmeticslots;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

/** Client-only part: draws the slot panel and hides the slots where they would not fit. */
@Mod(value = CosmeticSlots.MOD_ID, dist = Dist.CLIENT)
public final class CosmeticSlotsClient {
    public CosmeticSlotsClient(IEventBus modBus) {
        CosmeticSlot.setClientVisibility(CosmeticSlotsClient::slotsVisible);
        NeoForge.EVENT_BUS.addListener(CosmeticSlotsClient::drawPanel);
    }

    private static boolean slotsVisible() {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof InventoryScreen inventory) {
            // The recipe book opens over the area where the panel sits.
            return !inventory.getRecipeBookComponent().isVisible();
        }
        // The creative screen re-lays-out the player's menu slots and has no room for ours.
        return !(screen instanceof CreativeModeInventoryScreen);
    }

    private static void drawPanel(ContainerScreenEvent.Render.Background event) {
        if (!(event.getContainerScreen() instanceof InventoryScreen screen) || !slotsVisible()) return;

        GuiGraphics g = event.getGuiGraphics();
        int left = screen.getGuiLeft();
        int top = screen.getGuiTop();

        // Panel behind the slots (vanilla-style grey with a dark outline).
        int panelLeft = left + CosmeticSlot.X - 5;
        int panelTop = top + CosmeticSlot.FIRST_Y - 5;
        int panelBottom = top + CosmeticSlot.FIRST_Y + CosmeticSlot.STEP * 4 - 1 + 4;
        g.fill(panelLeft, panelTop, left, panelBottom, 0xFF000000);
        g.fill(panelLeft + 1, panelTop + 1, left, panelBottom - 1, 0xFFC6C6C6);

        // One vanilla-looking 18x18 slot frame per cosmetic slot.
        for (int i = 0; i < CosmeticArmor.SLOTS.length; i++) {
            int fx = left + CosmeticSlot.X - 1;
            int fy = top + CosmeticSlot.FIRST_Y + i * CosmeticSlot.STEP - 1;
            g.fill(fx, fy, fx + 18, fy + 18, 0xFF373737);
            g.fill(fx + 1, fy + 1, fx + 18, fy + 18, 0xFFFFFFFF);
            g.fill(fx + 1, fy + 1, fx + 17, fy + 17, 0xFF8B8B8B);
        }
    }
}

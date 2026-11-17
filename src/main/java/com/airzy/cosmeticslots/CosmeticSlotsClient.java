package com.airzy.cosmeticslots;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

/** Client-only part: reveals the cosmetic column when the armor-slot group is hovered. */
@Mod(value = CosmeticSlots.MOD_ID, dist = Dist.CLIENT)
public final class CosmeticSlotsClient {
    private static boolean panelVisible;

    public CosmeticSlotsClient(IEventBus modBus) {
        CosmeticSlot.setClientVisibility(CosmeticSlotsClient::slotsVisible);
        NeoForge.EVENT_BUS.addListener(CosmeticSlotsClient::drawPanel);
    }

    private static boolean slotsVisible() {
        Screen screen = Minecraft.getInstance().screen;
        return panelVisible
                && screen instanceof InventoryScreen inventory
                && !inventory.getRecipeBookComponent().isVisible();
    }

    private static void drawPanel(ContainerScreenEvent.Render.Background event) {
        if (!(event.getContainerScreen() instanceof InventoryScreen screen)) {
            panelVisible = false;
            return;
        }

        // The recipe book covers this area, so collapse the cosmetic column while it is open.
        if (screen.getRecipeBookComponent().isVisible()) {
            panelVisible = false;
            return;
        }

        int left = screen.getGuiLeft();
        int top = screen.getGuiTop();
        int mouseX = event.getMouseX();
        int mouseY = event.getMouseY();

        int slotsTop = top + CosmeticSlot.FIRST_Y;
        int slotsBottom = slotsTop + CosmeticSlot.STEP * CosmeticArmor.SLOTS.length;

        // Hovering the real armor column opens the cosmetic column. Once open, it stays
        // open while the pointer moves across to or rests over the cosmetic slots.
        boolean hoveringArmorColumn = mouseX >= left + 8 && mouseX < left + 26
                && mouseY >= slotsTop && mouseY < slotsBottom;
        boolean hoveringCosmeticColumn = mouseX >= left + CosmeticSlot.X
                && mouseX < left + CosmeticSlot.X + 18
                && mouseY >= slotsTop && mouseY < slotsBottom;
        panelVisible = hoveringArmorColumn || (panelVisible && hoveringCosmeticColumn);

        if (!panelVisible) return;

        GuiGraphics g = event.getGuiGraphics();

        // One shared vanilla-style panel frames both the real and cosmetic armor columns.
        int panelLeft = left + 6;
        int panelTop = top + 6;
        int panelRight = left + CosmeticSlot.X + 18 + 1;
        int panelBottom = slotsBottom + 1;
        g.fill(panelLeft, panelTop, panelRight, panelBottom, 0xFF000000);
        g.fill(panelLeft + 1, panelTop + 1, panelRight - 1, panelBottom - 1, 0xFFC6C6C6);
    }
}

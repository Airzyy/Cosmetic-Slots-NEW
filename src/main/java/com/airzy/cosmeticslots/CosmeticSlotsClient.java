package com.airzy.cosmeticslots;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

/** Client-only part: reveals only the cosmetic slot paired with the hovered armor slot. */
@Mod(value = CosmeticSlots.MOD_ID, dist = Dist.CLIENT)
public final class CosmeticSlotsClient {
    private static EquipmentSlot visibleSlot;

    public CosmeticSlotsClient(IEventBus modBus) {
        CosmeticSlot.setClientVisibility(CosmeticSlotsClient::slotsVisible);
        NeoForge.EVENT_BUS.addListener(CosmeticSlotsClient::drawPanel);
    }

    private static boolean slotsVisible(EquipmentSlot slot) {
        Screen screen = Minecraft.getInstance().screen;
        return visibleSlot == slot
                && screen instanceof InventoryScreen inventory
                && !inventory.getRecipeBookComponent().isVisible();
    }

    private static void drawPanel(ContainerScreenEvent.Render.Background event) {
        if (!(event.getContainerScreen() instanceof InventoryScreen screen)) {
            visibleSlot = null;
            return;
        }

        // The recipe book covers this area, so collapse the cosmetic slot while it is open.
        if (screen.getRecipeBookComponent().isVisible()) {
            visibleSlot = null;
            return;
        }

        int left = screen.getGuiLeft();
        int top = screen.getGuiTop();
        int mouseX = event.getMouseX();
        int mouseY = event.getMouseY();

        int slotsTop = top + CosmeticSlot.FIRST_Y;
        int slotsBottom = slotsTop + CosmeticSlot.STEP * CosmeticArmor.SLOTS.length;

        // Treat the two adjacent columns as one hover target for each row. This keeps
        // the matching cosmetic slot open while the pointer moves from the real slot to it.
        boolean overSlotPair = mouseX >= left + 8
                && mouseX < left + CosmeticSlot.X + 18
                && mouseY >= slotsTop
                && mouseY < slotsBottom;

        if (!overSlotPair) {
            visibleSlot = null;
            return;
        }

        int row = (mouseY - slotsTop) / CosmeticSlot.STEP;
        if (row < 0 || row >= CosmeticArmor.SLOTS.length) {
            visibleSlot = null;
            return;
        }

        visibleSlot = CosmeticArmor.SLOTS[row];

        GuiGraphics g = event.getGuiGraphics();

        // Draw a single shared vanilla-style border around this armor/cosmetic pair.
        int rowTop = slotsTop + row * CosmeticSlot.STEP;
        int panelLeft = left + 6;
        int panelTop = rowTop - 2;
        int panelRight = left + CosmeticSlot.X + 18 + 1;
        int panelBottom = rowTop + CosmeticSlot.STEP + 1;
        g.fill(panelLeft, panelTop, panelRight, panelBottom, 0xFF000000);
        g.fill(panelLeft + 1, panelTop + 1, panelRight - 1, panelBottom - 1, 0xFFC6C6C6);
    }
}

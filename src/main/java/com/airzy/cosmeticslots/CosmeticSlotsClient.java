package com.airzy.cosmeticslots;

import com.airzy.cosmeticslots.mixin.client.AbstractContainerScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

/** Client-only part: reveals the matching cosmetic slot and layers it over the player model. */
@Mod(value = CosmeticSlots.MOD_ID, dist = Dist.CLIENT)
public final class CosmeticSlotsClient {
    private static EquipmentSlot visibleSlot;

    public CosmeticSlotsClient(IEventBus modBus) {
        CosmeticSlot.setClientVisibility(CosmeticSlotsClient::slotsVisible);
        // Update the active row before the container screen renders its slots.
        NeoForge.EVENT_BUS.addListener(CosmeticSlotsClient::updateHoveredSlot);
        // Draw the hovered pair after the screen contents, so the player model cannot cover it.
        NeoForge.EVENT_BUS.addListener(CosmeticSlotsClient::drawHoveredPair);
    }

    private static boolean slotsVisible(EquipmentSlot slot) {
        Screen screen = Minecraft.getInstance().screen;
        return visibleSlot == slot
                && screen instanceof InventoryScreen inventory
                && !inventory.getRecipeBookComponent().isVisible();
    }

    private static void updateHoveredSlot(ContainerScreenEvent.Render.Background event) {
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
        // the matching cosmetic slot open while the pointer moves between the pair.
        boolean overSlotPair = mouseX >= left + 8
                && mouseX < left + CosmeticSlot.X + 18
                && mouseY >= slotsTop
                && mouseY < slotsBottom;

        if (!overSlotPair) {
            visibleSlot = null;
            return;
        }

        int row = (mouseY - slotsTop) / CosmeticSlot.STEP;
        visibleSlot = row >= 0 && row < CosmeticArmor.SLOTS.length
                ? CosmeticArmor.SLOTS[row]
                : null;
    }

    private static void drawHoveredPair(ContainerScreenEvent.Render.Foreground event) {
        if (!(event.getContainerScreen() instanceof InventoryScreen screen)
                || visibleSlot == null
                || !slotsVisible(visibleSlot)) {
            return;
        }

        int row = slotIndex(visibleSlot);
        if (row < 0) return;

        // Foreground is posted while AbstractContainerScreen's pose is already translated
        // to leftPos/topPos, so everything drawn here must use screen-local coordinates.
        int rowSlotY = CosmeticSlot.FIRST_Y + row * CosmeticSlot.STEP;
        GuiGraphics g = event.getGuiGraphics();

        // Repaint the backing panel on top of the player model.
        int panelLeft = 6;
        int panelTop = rowSlotY - 2;
        int panelRight = CosmeticSlot.X + 18 + 1;
        int panelBottom = rowSlotY + CosmeticSlot.STEP + 1;
        g.fill(panelLeft, panelTop, panelRight, panelBottom, 0xFF000000);
        g.fill(panelLeft + 1, panelTop + 1, panelRight - 1, panelBottom - 1, 0xFFC6C6C6);

        // Re-render the matching vanilla and cosmetic slots with Minecraft's own slot renderer.
        Slot armorSlot = null;
        Slot cosmeticSlot = null;
        for (Slot slot : screen.getMenu().slots) {
            if (slot.y != rowSlotY) continue;

            if (slot instanceof CosmeticSlot) {
                cosmeticSlot = slot;
            } else if (slot.x == 8) {
                armorSlot = slot;
            }
        }

        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) (Object) screen;
        if (armorSlot != null) accessor.cosmeticslots$renderSlot(g, armorSlot);
        if (cosmeticSlot != null && cosmeticSlot.isActive()) {
            accessor.cosmeticslots$renderSlot(g, cosmeticSlot);
        }
    }

    private static int slotIndex(EquipmentSlot slot) {
        for (int i = 0; i < CosmeticArmor.SLOTS.length; i++) {
            if (CosmeticArmor.SLOTS[i] == slot) return i;
        }
        return -1;
    }
}

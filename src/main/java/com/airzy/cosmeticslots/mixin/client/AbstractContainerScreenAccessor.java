package com.airzy.cosmeticslots.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Exposes vanilla's slot renderer so the hovered pair can be redrawn above the player model. */
@Mixin(value = AbstractContainerScreen.class, remap = false)
public interface AbstractContainerScreenAccessor {
    @Invoker("renderSlot")
    void cosmeticslots$renderSlot(GuiGraphics graphics, Slot slot);
}

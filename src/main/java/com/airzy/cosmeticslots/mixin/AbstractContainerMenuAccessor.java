package com.airzy.cosmeticslots.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** NeoForge 1.21.1 runs on Mojang names, so these are the plain method names (not SRG "m_xxxx_"). */
@Mixin(value = AbstractContainerMenu.class, remap = false)
public interface AbstractContainerMenuAccessor {
    @Invoker("addSlot")
    Slot cosmeticslots$addSlot(Slot slot);

    @Invoker("moveItemStackTo")
    boolean cosmeticslots$moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection);
}

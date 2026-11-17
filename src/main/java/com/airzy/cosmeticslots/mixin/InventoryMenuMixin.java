package com.airzy.cosmeticslots.mixin;

import com.airzy.cosmeticslots.CosmeticArmor;
import com.airzy.cosmeticslots.CosmeticSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InventoryMenu.class, remap = false)
public abstract class InventoryMenuMixin {
    /** Appends the cosmetic slots after all vanilla slots, so vanilla slot indices never change. */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void cosmeticslots$addSlots(Inventory inventory, boolean active, Player owner, CallbackInfo ci) {
        AbstractContainerMenuAccessor menu = (AbstractContainerMenuAccessor) (Object) this;
        for (int i = 0; i < CosmeticArmor.SLOTS.length; i++) {
            menu.cosmeticslots$addSlot(new CosmeticSlot(
                    owner, CosmeticArmor.SLOTS[i], CosmeticSlot.X, CosmeticSlot.FIRST_Y + i * CosmeticSlot.STEP));
        }
    }

    /**
     * Shift-click on a cosmetic slot moves the item to the main inventory.
     * Without this, vanilla would try to push the item into the real armor slot.
     */
    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void cosmeticslots$quickMove(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
        if (index < 0 || index >= menu.slots.size()) return;
        if (!(menu.slots.get(index) instanceof CosmeticSlot slot)) return;

        ItemStack original = slot.getItem().copy();
        if (original.isEmpty()) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        ItemStack working = original.copy();
        AbstractContainerMenuAccessor accessor = (AbstractContainerMenuAccessor) (Object) this;
        // 9..45 = main inventory + hotbar
        if (!accessor.cosmeticslots$moveItemStackTo(working, 9, 45, false)) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }
        slot.set(working.isEmpty() ? ItemStack.EMPTY : working);
        cir.setReturnValue(original);
    }
}

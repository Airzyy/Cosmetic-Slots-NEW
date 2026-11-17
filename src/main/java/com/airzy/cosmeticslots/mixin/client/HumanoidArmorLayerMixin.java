package com.airzy.cosmeticslots.mixin.client;

import com.airzy.cosmeticslots.CosmeticArmor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = HumanoidArmorLayer.class, remap = false)
public abstract class HumanoidArmorLayerMixin {
    /**
     * When a player has a cosmetic piece in a slot, render that instead of the real armor.
     * Wrapped (not redirected) so other mods touching the same call still work.
     * NeoForge adds a second renderArmorPiece overload that just delegates; the one that
     * actually calls getItemBySlot is the one that gets patched.
     */
    @WrapOperation(
            method = "renderArmorPiece",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack cosmeticslots$useCosmetic(LivingEntity entity, EquipmentSlot slot, Operation<ItemStack> original) {
        if (entity instanceof Player player) {
            ItemStack cosmetic = CosmeticArmor.get(player).get(slot);
            if (!cosmetic.isEmpty()) return cosmetic;
        }
        return original.call(entity, slot);
    }
}

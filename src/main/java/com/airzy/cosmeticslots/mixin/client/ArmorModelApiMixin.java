package com.airzy.cosmeticslots.mixin.client;

import com.airzy.cosmeticslots.CosmeticArmor;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Optional integration for Armor Model API. Its dispatcher receives the equipped
 * stack as an argument, independently of the vanilla armor-layer lookup.
 */
@Pseudo
@Mixin(targets = "net.rpg_foundation.armor_api.client.ArmorRenderDispatcher", remap = false)
public abstract class ArmorModelApiMixin {
    @ModifyVariable(
            method = "render",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private static ItemStack cosmeticslots$useCosmeticArmorModelApi(
            ItemStack original,
            @Local(argsOnly = true) LivingEntity entity,
            @Local(argsOnly = true) EquipmentSlot slot
    ) {
        return CosmeticArmor.getRenderStack(entity, slot, original);
    }
}

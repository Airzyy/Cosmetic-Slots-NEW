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
 * Optional GeckoLib integration. GeckoLib resolves its armor renderer from the
 * stack passed into InternalUtil, so replace that stack before renderer lookup.
 */
@Pseudo
@Mixin(targets = "software.bernie.geckolib.util.InternalUtil", remap = false)
public abstract class GeckoLibArmorMixin {
    @ModifyVariable(
            method = "tryRenderGeoArmorPiece",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private static ItemStack cosmeticslots$useCosmeticGeckoArmor(
            ItemStack original,
            @Local(argsOnly = true) LivingEntity entity,
            @Local(argsOnly = true) EquipmentSlot slot
    ) {
        return CosmeticArmor.getRenderStack(entity, slot, original);
    }
}

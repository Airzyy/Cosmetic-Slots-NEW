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
     * NeoForge 1.21.1 keeps the original six-argument method as a deprecated
     * overload and routes armor rendering through its twelve-argument method.
     * Target that exact overload so cosmetic stacks replace the rendered armor.
     */
    @WrapOperation(
            method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V",
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

package com.airzy.cosmeticslots;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class CosmeticEvents {
    /** Where version 1.x stored cosmetic items (inside the player's NeoForgeData tag). */
    private static final String LEGACY_KEY = "cosmeticslots";

    private CosmeticEvents() {
    }

    /** Cosmetic items are real items: they drop on death like worn armor, unless keepInventory is on. */
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.serverLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

        CosmeticArmor armor = CosmeticArmor.get(player);
        if (armor.isEmpty()) return;

        for (EquipmentSlot slot : CosmeticArmor.SLOTS) {
            ItemStack stack = armor.get(slot);
            if (stack.isEmpty()) continue;
            ItemEntity drop = new ItemEntity(player.level(), player.getX(), player.getY() + 0.5, player.getZ(), stack.copy());
            drop.setDefaultPickUpDelay();
            event.getDrops().add(drop);
        }
        CosmeticArmor.clear(player);
    }

    /** One-time import of items saved by version 1.x so existing worlds keep their cosmetics. */
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        CompoundTag persistent = player.getPersistentData();
        if (!persistent.contains(LEGACY_KEY, Tag.TAG_COMPOUND)) return;
        CompoundTag legacy = persistent.getCompound(LEGACY_KEY);
        persistent.remove(LEGACY_KEY);

        if (!CosmeticArmor.get(player).isEmpty()) return;
        CosmeticArmor imported = CosmeticArmor.EMPTY;
        for (int i = 0; i < CosmeticArmor.SLOTS.length; i++) {
            String key = CosmeticArmor.KEYS[i];
            if (!legacy.contains(key, Tag.TAG_COMPOUND)) continue;
            ItemStack stack = ItemStack.parse(player.registryAccess(), legacy.getCompound(key)).orElse(ItemStack.EMPTY);
            imported = imported.with(CosmeticArmor.SLOTS[i], stack);
        }
        if (!imported.isEmpty()) player.setData(CosmeticAttachments.ARMOR, imported);
    }
}

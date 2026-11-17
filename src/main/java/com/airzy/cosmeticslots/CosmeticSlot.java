package com.airzy.cosmeticslots;

import com.mojang.datafixers.util.Pair;
import java.util.function.BooleanSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** A single cosmetic armor slot, backed by the player's {@link CosmeticArmor} attachment. */
public final class CosmeticSlot extends Slot {
    /** Slot position relative to the inventory screen: a column just left of the vanilla armor column. */
    public static final int X = -21;
    public static final int FIRST_Y = 8;
    public static final int STEP = 18;

    /** Set by the client mod class; lets the client hide the slots (recipe book open, creative screen). */
    private static volatile BooleanSupplier clientVisibility = () -> true;

    public static void setClientVisibility(BooleanSupplier visibility) {
        clientVisibility = visibility;
    }

    private final Player owner;
    private final EquipmentSlot equipmentSlot;

    public CosmeticSlot(Player owner, EquipmentSlot equipmentSlot, int x, int y) {
        super(new CosmeticContainer(owner, equipmentSlot), 0, x, y);
        this.owner = owner;
        this.equipmentSlot = equipmentSlot;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        // Accept vanilla and modded wearable armor, including GeckoLib-backed pieces.
        return stack.getItem() instanceof Equipable equipable
                && equipable.getEquipmentSlot() == equipmentSlot;
    }

    @Override
    public boolean isActive() {
        return !owner.level().isClientSide || clientVisibility.getAsBoolean();
    }

    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        ResourceLocation icon = switch (equipmentSlot) {
            case HEAD -> InventoryMenu.EMPTY_ARMOR_SLOT_HELMET;
            case CHEST -> InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE;
            case LEGS -> InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS;
            case FEET -> InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS;
            default -> null;
        };
        return icon == null ? null : Pair.of(InventoryMenu.BLOCK_ATLAS, icon);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    private record CosmeticContainer(Player owner, EquipmentSlot slot) implements Container {
        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return getItem(0).isEmpty();
        }

        @Override
        public ItemStack getItem(int index) {
            return index == 0 ? CosmeticArmor.get(owner).get(slot) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int index, int amount) {
            return amount > 0 ? removeItemNoUpdate(index) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int index) {
            if (index != 0) return ItemStack.EMPTY;
            ItemStack removed = getItem(0);
            if (!removed.isEmpty()) CosmeticArmor.set(owner, slot, ItemStack.EMPTY);
            return removed;
        }

        @Override
        public void setItem(int index, ItemStack stack) {
            if (index == 0) CosmeticArmor.set(owner, slot, stack);
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {
            CosmeticArmor.set(owner, slot, ItemStack.EMPTY);
        }
    }
}

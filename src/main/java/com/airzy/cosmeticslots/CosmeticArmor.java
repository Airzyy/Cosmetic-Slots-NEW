package com.airzy.cosmeticslots;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

/**
 * Immutable snapshot of the four cosmetic armor stacks of a player.
 * Always replace the whole value via {@link #set} so NeoForge syncs the change.
 */
public record CosmeticArmor(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
    public static final CosmeticArmor EMPTY =
            new CosmeticArmor(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);

    /** Order used for layout in the inventory screen. */
    public static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    static final String[] KEYS = {"head", "chest", "legs", "feet"};

    public static final StreamCodec<RegistryFriendlyByteBuf, CosmeticArmor> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, CosmeticArmor::head,
            ItemStack.OPTIONAL_STREAM_CODEC, CosmeticArmor::chest,
            ItemStack.OPTIONAL_STREAM_CODEC, CosmeticArmor::legs,
            ItemStack.OPTIONAL_STREAM_CODEC, CosmeticArmor::feet,
            CosmeticArmor::new
    );

    /**
     * Hand-written (instead of a Codec) so that one unknown item, e.g. from a removed mod,
     * only empties that slot instead of failing the whole player's data.
     */
    public static final IAttachmentSerializer<CompoundTag, CosmeticArmor> SERIALIZER = new IAttachmentSerializer<>() {
        @Override
        public CosmeticArmor read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
            ItemStack[] stacks = new ItemStack[4];
            for (int i = 0; i < 4; i++) {
                stacks[i] = tag.contains(KEYS[i], Tag.TAG_COMPOUND)
                        ? ItemStack.parse(provider, tag.getCompound(KEYS[i])).orElse(ItemStack.EMPTY)
                        : ItemStack.EMPTY;
            }
            return new CosmeticArmor(stacks[0], stacks[1], stacks[2], stacks[3]);
        }

        @Nullable
        @Override
        public CompoundTag write(CosmeticArmor armor, HolderLookup.Provider provider) {
            if (armor.isEmpty()) return null;
            CompoundTag tag = new CompoundTag();
            for (int i = 0; i < 4; i++) {
                ItemStack stack = armor.get(SLOTS[i]);
                if (!stack.isEmpty()) tag.put(KEYS[i], stack.save(provider));
            }
            return tag;
        }
    };

    public static CosmeticArmor get(Player player) {
        CosmeticArmor armor = player.getExistingDataOrNull(CosmeticAttachments.ARMOR);
        return armor == null ? EMPTY : armor;
    }

    public static void set(Player player, EquipmentSlot slot, ItemStack stack) {
        player.setData(CosmeticAttachments.ARMOR, get(player).with(slot, stack));
    }

    public static void clear(Player player) {
        if (!get(player).isEmpty()) player.setData(CosmeticAttachments.ARMOR, EMPTY);
    }

    public ItemStack get(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> head;
            case CHEST -> chest;
            case LEGS -> legs;
            case FEET -> feet;
            default -> ItemStack.EMPTY;
        };
    }

    public CosmeticArmor with(EquipmentSlot slot, ItemStack stack) {
        ItemStack value = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        return switch (slot) {
            case HEAD -> new CosmeticArmor(value, chest, legs, feet);
            case CHEST -> new CosmeticArmor(head, value, legs, feet);
            case LEGS -> new CosmeticArmor(head, chest, value, feet);
            case FEET -> new CosmeticArmor(head, chest, legs, value);
            default -> this;
        };
    }

    public boolean isEmpty() {
        return head.isEmpty() && chest.isEmpty() && legs.isEmpty() && feet.isEmpty();
    }
}

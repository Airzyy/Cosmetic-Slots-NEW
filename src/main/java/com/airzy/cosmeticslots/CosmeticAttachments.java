package com.airzy.cosmeticslots;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Registers the data attachment that stores a player's cosmetic armor.
 * <ul>
 *   <li>saved with the player (only when something is stored),</li>
 *   <li>synced automatically by NeoForge to the owner and everyone tracking them,</li>
 *   <li>copied on respawn; items are dropped on death in {@link CosmeticEvents} unless keepInventory is on.</li>
 * </ul>
 */
public final class CosmeticAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CosmeticSlots.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CosmeticArmor>> ARMOR =
            ATTACHMENTS.register("armor", () -> AttachmentType.builder(() -> CosmeticArmor.EMPTY)
                    .serialize(CosmeticArmor.SERIALIZER)
                    .copyOnDeath()
                    .sync(CosmeticArmor.STREAM_CODEC)
                    .build());

    private CosmeticAttachments() {
    }
}

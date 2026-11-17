package com.airzy.cosmeticslots;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(CosmeticSlots.MOD_ID)
public final class CosmeticSlots {
    public static final String MOD_ID = "cosmeticslots";

    public CosmeticSlots(IEventBus modBus) {
        CosmeticAttachments.ATTACHMENTS.register(modBus);
        NeoForge.EVENT_BUS.addListener(CosmeticEvents::onLivingDrops);
        NeoForge.EVENT_BUS.addListener(CosmeticEvents::onLogin);
    }
}

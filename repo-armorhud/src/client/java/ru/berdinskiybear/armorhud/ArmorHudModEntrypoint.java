package ru.berdinskiybear.armorhud;

import net.fabricmc.api.ClientModInitializer;

public final class ArmorHudModEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ArmorHudMod.readCurrentConfig();
    }
}

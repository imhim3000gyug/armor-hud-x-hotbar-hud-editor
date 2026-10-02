package ru.berdinskiybear.armorhud.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import ru.berdinskiybear.armorhud.ArmorHudMod;

public final class ArmorHudModmenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            return ArmorHudConfigScreenBuilder::create;
        }
        return (Screen parent) -> null;
    }
}
package ru.berdinskiybear.armorhud.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.berdinskiybear.armorhud.ArmorHudClient;
import ru.berdinskiybear.armorhud.ArmorHudMod;

@Mixin(Minecraft.class)
public class ArmorHudMinecraftMixin {
    @Inject(method = "setScreenAndShow", at = @At("HEAD"))
    private void armorhud$updatePreview(Screen screen, CallbackInfo ci) {
        ArmorHudClient.setPreviewActive(screen != null && screen.getTitle().equals(ArmorHudMod.CONFIG_SCREEN_NAME));
    }
}
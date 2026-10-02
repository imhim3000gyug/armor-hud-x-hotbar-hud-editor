package ru.berdinskiybear.armorhud.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.berdinskiybear.armorhud.ArmorHudClient;
import ru.berdinskiybear.armorhud.ArmorHudMod;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig;

import java.util.Optional;

@Mixin(BossHealthOverlay.class)
public class ArmorHudBossBarMixin {
    @Unique
    private int armorhud$bossBarOffset;

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void armorhud$calculateBossBarOffset(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        armorhud$bossBarOffset = 0;
        ArmorHudConfig config = ArmorHudClient.getConfig();
        if (!config.isEnabled() || !config.getPushBossbars() || config.getAnchor() != ArmorHudConfig.Anchor.TOP_CENTER) return;

        Player player = ArmorHudClient.getCameraPlayer();
        if (player == null) return;
        Optional<Rect2i> rect = ArmorHudClient.getEffectiveWidgetRect(graphics, player);
        rect.ifPresent(value -> armorhud$bossBarOffset = Math.max(value.getY() + value.getHeight(), 0));
    }

    @ModifyVariable(method = "extractRenderState", at = @At("STORE"), name = "yOffset")
    private int armorhud$pushBossBars(int yOffset) {
        return yOffset + armorhud$bossBarOffset;
    }
}
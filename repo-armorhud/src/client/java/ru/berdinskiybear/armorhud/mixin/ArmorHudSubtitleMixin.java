package ru.berdinskiybear.armorhud.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.berdinskiybear.armorhud.ArmorHudClient;
import ru.berdinskiybear.armorhud.ArmorHudMod;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig;

import java.util.Optional;

@Mixin(SubtitleOverlay.class)
public class ArmorHudSubtitleMixin {
    @Unique
    private int armorhud$subtitleOffset;

    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;width(Ljava/lang/String;)I", ordinal = 3))
    private void armorhud$calculateOffset(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        armorhud$subtitleOffset = 0;
        ArmorHudConfig config = ArmorHudClient.getConfig();
        if (!config.isEnabled() || !config.getPushSubtitles() || config.getAnchor() != ArmorHudConfig.Anchor.BOTTOM
                || config.getSide() != ArmorHudConfig.Side.RIGHT) return;
        Player player = ArmorHudClient.getCameraPlayer();
        if (player == null) return;
        Optional<Rect2i> rect = ArmorHudClient.getEffectiveWidgetRect(graphics, player);
        rect.ifPresent(value -> armorhud$subtitleOffset = Math.max(graphics.guiHeight() - value.getY() - 25, 0));
    }

    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;translate(FF)Lorg/joml/Matrix3x2f;", shift = At.Shift.AFTER, remap = false))
    private void armorhud$offsetSubtitles(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        graphics.pose().translate(0.0F, -armorhud$subtitleOffset);
    }
}
package ru.berdinskiybear.armorhud.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.berdinskiybear.armorhud.ArmorHudClient;
import ru.berdinskiybear.armorhud.ArmorHudMod;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mixin(Hud.class)
public abstract class ArmorHudHudMixin {
    @Shadow
    @Final
    private RandomSource random;

    @Shadow
    @Final
    private static Identifier HOTBAR_SPRITE;

    @Shadow
    @Final
    private static Identifier HOTBAR_OFFHAND_LEFT_SPRITE;

    @Shadow
    protected abstract void extractSlot(GuiGraphicsExtractor graphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack itemStack, int seed);

    @Shadow
    public abstract Font getFont();

    @Unique
    private int armorhud$statusEffectOffset;

    @Inject(method = "extractItemHotbar", at = @At("TAIL"))
    private void armorhud$render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Profiler.get().push(ArmorHudMod.MOD_ID);
        try {
            armorhud$drawWidget(graphics, deltaTracker);
        } finally {
            Profiler.get().pop();
        }
    }

    @Unique
    private void armorhud$drawWidget(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        ArmorHudConfig config = ArmorHudClient.getConfig();
        if (!config.isEnabled()) return;
        Player player = ArmorHudClient.getCameraPlayer();
        if (player == null) return;

        Optional<Rect2i> widget = ArmorHudClient.getWidgetRect(graphics, player);
        if (widget.isEmpty()) return;
        List<ItemStack> armor = new ArrayList<>(ArmorHudMod.getArmorItems(player));
        if (config.isReversed()) Collections.reverse(armor);
        Rect2i bounds = widget.get();
        int width = ArmorHudMod.SIZE + (armor.size() - 1) * ArmorHudMod.STEP;

        graphics.pose().pushMatrix();
        if (config.getStyle() != ArmorHudConfig.Style.STYLE_3) {
            int color = ARGB.white(1.0F);
            int cap = switch (config.getStyle()) {
                case STYLE_1_E, STYLE_2_E -> 3;
                case STYLE_1_H, STYLE_2_H -> ArmorHudMod.SIZE / 2;
                case STYLE_1_S, STYLE_2_S -> (ArmorHudMod.SIZE + ArmorHudMod.STEP) / 2;
                case STYLE_3 -> 0;
            };
            if (config.getStyle().name().startsWith("STYLE_1")) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 0, 0, bounds.getX(), bounds.getY(), width - cap, ArmorHudMod.SIZE, color);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 182 - cap, 0, bounds.getX() + width - cap, bounds.getY(), cap, ArmorHudMod.SIZE, color);
            } else {
                int innerWidth = Math.max(width - cap * 2, 0);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, 0, 1, bounds.getX(), bounds.getY(), cap, ArmorHudMod.SIZE, color);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, cap, 0, bounds.getX() + cap, bounds.getY(), innerWidth, ArmorHudMod.SIZE, color);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, 29 - cap, 1, bounds.getX() + width - cap, bounds.getY(), cap, ArmorHudMod.SIZE, color);
            }
        }
        graphics.pose().popMatrix();

        EquipmentSlot[] slots = InventoryMenuAccessor.armorhud$getSlotIds();
        for (int index = 0; index < armor.size(); index++) {
            ItemStack stack = armor.get(index);
            int x = bounds.getX() + index * ArmorHudMod.STEP;
            int y = bounds.getY();
            if (stack.isEmpty() && config.getIconsShown() && config.getWidgetShown() != ArmorHudConfig.WidgetShown.NOT_EMPTY) {
                int originalIndex = config.isReversed() ? slots.length - index - 1 : index;
                Identifier icon = InventoryMenuAccessor.armorhud$getEmptySlotTextures().get(slots[originalIndex]);
                if (icon != null) graphics.blitSprite(RenderPipelines.GUI_TEXTURED, icon, x + 3, y + 3, 16, 16);
            }

            if (!stack.isEmpty()) this.extractSlot(graphics, x + 3, y + 3, deltaTracker, player, stack, index + 1);
            if (config.isWarningShown() && ArmorHudMod.shouldShowWarning(stack, config)) {
                int bobOffset = armorhud$getWarningBobOffset(config);
                int warningY = config.getAnchor() == ArmorHudConfig.Anchor.TOP || config.getAnchor() == ArmorHudConfig.Anchor.TOP_CENTER
                        ? y + ArmorHudMod.SIZE + bobOffset
                        : y - ArmorHudMod.WARNING_SIZE - 2 + bobOffset;
                graphics.centeredText(this.getFont(), "!", x + ArmorHudMod.SIZE / 2, warningY, 0xFFFFAA00);
            }
        }
    }

    @Unique
    private int armorhud$getWarningBobOffset(ArmorHudConfig config) {
        float interval = config.getWarningIconBobbingIntervalMs();
        if (interval == 0.0F) return 0;
        return Math.round((float) Math.sin(System.currentTimeMillis() * (Math.PI * 2.0 / interval)) * 3.0F);
    }

    @Inject(method = "extractEffects", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void armorhud$calculateStatusEffectOffset(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        armorhud$statusEffectOffset = 0;
        ArmorHudConfig config = ArmorHudClient.getConfig();
        if (!config.isEnabled() || !config.getPushStatusEffectIcons() || config.getAnchor() != ArmorHudConfig.Anchor.TOP
                || config.getSide() != ArmorHudConfig.Side.RIGHT) return;
        Player player = ArmorHudClient.getCameraPlayer();
        if (player == null) return;
        ArmorHudClient.getEffectiveWidgetRect(graphics, player)
                .ifPresent(rect -> armorhud$statusEffectOffset = Math.max(rect.getY() + rect.getHeight(), 0));
    }

    @ModifyVariable(method = "extractEffects", at = @At("STORE"), name = "y")
    private int armorhud$pushStatusEffects(int y) {
        return y + armorhud$statusEffectOffset;
    }
}
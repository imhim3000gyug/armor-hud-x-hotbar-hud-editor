package ru.berdinskiybear.armorhud;

import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig;

import java.util.List;
import java.util.Optional;

public final class ArmorHudClient {
    private static boolean previewActive;

    private ArmorHudClient() {
    }

    public static ArmorHudConfig getConfig() {
        return previewActive ? ArmorHudMod.previewConfig : ArmorHudMod.getCurrentConfig();
    }

    public static void setPreviewActive(boolean active) {
        previewActive = active;
    }

    public static Player getCameraPlayer() {
        return Minecraft.getInstance().getCameraEntity() instanceof Player player ? player : null;
    }

    public static Optional<Rect2i> getWidgetRect(GuiGraphicsExtractor graphics, Player player) {
        ArmorHudConfig config = getConfig();
        List<ItemStack> armor = ArmorHudMod.getArmorItems(player, config);
        if (armor.isEmpty()) return Optional.empty();

        boolean fromLeft = config.getAnchor() == ArmorHudConfig.Anchor.HOTBAR
                ? config.getSide() == ArmorHudConfig.Side.LEFT
                : config.getSide() == ArmorHudConfig.Side.RIGHT;
        int sideMultiplier = fromLeft ? -1 : 1;
        int sideOffsetMultiplier = fromLeft ? -1 : 0;
        HumanoidArm widgetSide = config.getSide() == ArmorHudConfig.Side.LEFT ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        int hotbarExtra = switch (config.getOffhandSlotBehavior()) {
            case ALWAYS_IGNORE -> 0;
            case ALWAYS_LEAVE_SPACE -> player.getMainArm() == widgetSide ? ArmorHudMod.ATTACK_INDICATOR_OFFSET : ArmorHudMod.OFFHAND_OFFSET;
            case ADHERE -> {
                boolean sameSide = player.getMainArm() == widgetSide;
                if (sameSide && Minecraft.getInstance().options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR
                        && player.getAttackStrengthScale(0) < 1.0F) yield ArmorHudMod.ATTACK_INDICATOR_OFFSET;
                if (!sameSide && !player.getOffhandItem().isEmpty()) yield ArmorHudMod.OFFHAND_OFFSET;
                yield 0;
            }
        };

        int width = ArmorHudMod.SIZE + (armor.size() - 1) * ArmorHudMod.STEP;
        int x = switch (config.getAnchor()) {
            case TOP_CENTER -> (graphics.guiWidth() - width) / 2;
            case TOP, BOTTOM -> (width - graphics.guiWidth()) * sideOffsetMultiplier;
            case HOTBAR -> graphics.guiWidth() / 2 + (ArmorHudMod.HOTBAR_OFFSET + hotbarExtra) * sideMultiplier
                    + width * sideOffsetMultiplier;
        };
        x += config.getOffsetX() * sideMultiplier;
        int y = switch (config.getAnchor()) {
            case TOP, TOP_CENTER -> config.getOffsetY();
            case BOTTOM, HOTBAR -> graphics.guiHeight() - ArmorHudMod.SIZE - config.getOffsetY();
        };
        return Optional.of(new Rect2i(x, y, width, ArmorHudMod.SIZE));
    }

    public static Optional<Rect2i> getEffectiveWidgetRect(GuiGraphicsExtractor graphics, Player player) {
        return getWidgetRect(graphics, player).map(rect -> {
                ArmorHudConfig config = getConfig();
            boolean hasWarning = config.isWarningShown()
                    && ArmorHudMod.getArmorItems(player, config).stream().anyMatch(stack -> ArmorHudMod.shouldShowWarning(stack, config));
            int extra = hasWarning ? ArmorHudMod.WARNING_SIZE + 2
                    + (config.getWarningIconBobbingIntervalMs() == 0.0F ? 0 : 7) : 0;
            if (config.getAnchor() == ArmorHudConfig.Anchor.TOP_CENTER
                    || config.getAnchor() == ArmorHudConfig.Anchor.TOP) {
                rect.setHeight(rect.getHeight() + extra);
            } else {
                rect.setY(rect.getY() - extra);
                rect.setHeight(rect.getHeight() + extra);
            }
            return rect;
        });
    }
}

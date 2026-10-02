package ru.berdinskiybear.armorhud.config;

public class ArmorHudConfig {
    protected boolean enabled = true;
    protected Anchor anchor = Anchor.HOTBAR;
    protected Side side = Side.LEFT;
    protected int offsetX;
    protected int offsetY;
    protected Style style = Style.STYLE_1_E;
    protected WidgetShown widgetShown = WidgetShown.NOT_EMPTY;
    protected OffhandSlotBehavior offhandSlotBehavior = OffhandSlotBehavior.ADHERE;
    protected boolean pushBossbars = true;
    protected boolean pushStatusEffectIcons = true;
    protected boolean pushSubtitles = true;
    protected boolean reversed = true;
    protected boolean iconsShown = true;
    protected boolean warningShown = true;
    protected int minDurabilityValue = 5;
    protected double minDurabilityPercentage = 0.05D;
    protected float warningIconBobbingIntervalMs = 2000.0F;

    public ArmorHudConfig() {
    }

    public ArmorHudConfig(ArmorHudConfig original) {
        enabled = original.enabled;
        anchor = original.anchor;
        side = original.side;
        offsetX = original.offsetX;
        offsetY = original.offsetY;
        style = original.style;
        widgetShown = original.widgetShown;
        offhandSlotBehavior = original.offhandSlotBehavior;
        pushBossbars = original.pushBossbars;
        pushStatusEffectIcons = original.pushStatusEffectIcons;
        pushSubtitles = original.pushSubtitles;
        reversed = original.reversed;
        iconsShown = original.iconsShown;
        warningShown = original.warningShown;
        minDurabilityValue = original.minDurabilityValue;
        minDurabilityPercentage = original.minDurabilityPercentage;
        warningIconBobbingIntervalMs = original.warningIconBobbingIntervalMs;
    }

    public boolean isPreview() { return false; }
    public boolean isEnabled() { return enabled; }
    public Anchor getAnchor() { return anchor; }
    public Side getSide() { return side; }
    public int getOffsetX() { return offsetX; }
    public int getOffsetY() { return offsetY; }
    public Style getStyle() { return style; }
    public WidgetShown getWidgetShown() { return widgetShown; }
    public OffhandSlotBehavior getOffhandSlotBehavior() { return offhandSlotBehavior; }
    public boolean getPushBossbars() { return pushBossbars; }
    public boolean getPushStatusEffectIcons() { return pushStatusEffectIcons; }
    public boolean getPushSubtitles() { return pushSubtitles; }
    public boolean isReversed() { return reversed; }
    public boolean getIconsShown() { return iconsShown; }
    public boolean isWarningShown() { return warningShown; }
    public int getMinDurabilityValue() { return minDurabilityValue; }
    public double getMinDurabilityPercentage() { return minDurabilityPercentage; }
    public float getWarningIconBobbingIntervalMs() { return warningIconBobbingIntervalMs; }

    public enum Anchor { TOP_CENTER, TOP, BOTTOM, HOTBAR }
    public enum Side { RIGHT, LEFT }
    public enum OffhandSlotBehavior { ALWAYS_IGNORE, ADHERE, ALWAYS_LEAVE_SPACE }
    public enum WidgetShown { ALWAYS, IF_ANY_PRESENT, NOT_EMPTY }
    public enum Style { STYLE_1_E, STYLE_1_H, STYLE_1_S, STYLE_2_E, STYLE_2_H, STYLE_2_S, STYLE_3 }

    public static class MutableConfig extends ArmorHudConfig {
        public MutableConfig() { super(); }
        public MutableConfig(ArmorHudConfig original) { super(original); }

        public void setEnabled(boolean value) { enabled = value; }
        public void setAnchor(Anchor value) { anchor = value; }
        public void setSide(Side value) { side = value; }
        public void setOffsetX(int value) { offsetX = value; }
        public void setOffsetY(int value) { offsetY = value; }
        public void setStyle(Style value) { style = value; }
        public void setWidgetShown(WidgetShown value) { widgetShown = value; }
        public void setOffhandSlotBehavior(OffhandSlotBehavior value) { offhandSlotBehavior = value; }
        public void setPushBossbars(boolean value) { pushBossbars = value; }
        public void setPushStatusEffectIcons(boolean value) { pushStatusEffectIcons = value; }
        public void setPushSubtitles(boolean value) { pushSubtitles = value; }
        public void setReversed(boolean value) { reversed = value; }
        public void setIconsShown(boolean value) { iconsShown = value; }
        public void setWarningShown(boolean value) { warningShown = value; }
        public void setMinDurabilityValue(int value) { minDurabilityValue = value; }
        public void setMinDurabilityPercentage(double value) { minDurabilityPercentage = value; }
        public void setWarningIconBobbingIntervalMs(float value) { warningIconBobbingIntervalMs = value; }
    }
}

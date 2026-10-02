package ru.berdinskiybear.armorhud.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.berdinskiybear.armorhud.ArmorHudMod;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig.Anchor;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig.OffhandSlotBehavior;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig.Side;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig.Style;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig.WidgetShown;

import java.util.Optional;
import java.util.function.Consumer;

public final class ArmorHudConfigScreenBuilder {
    private ArmorHudConfigScreenBuilder() {
    }

    public static Screen create(Screen parent) {
        ArmorHudConfig current = ArmorHudMod.getCurrentConfig();
        ArmorHudConfig defaults = new ArmorHudConfig();
                ArmorHudMod.temporaryConfig = new ArmorHudConfig.MutableConfig(current);
                ArmorHudMod.previewConfig = new ArmorHudConfig.MutableConfig(current) {
                        @Override
                        public boolean isPreview() { return true; }
                };
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(ArmorHudMod.CONFIG_SCREEN_NAME)
                .setSavingRunnable(() -> {
                    ArmorHudMod.setCurrentConfig(new ArmorHudConfig(ArmorHudMod.temporaryConfig));
                    ArmorHudMod.writeCurrentConfig();
                });

        ConfigCategory category = builder.getOrCreateCategory(Component.translatable("armorHud.configScreen.category"));
        ConfigEntryBuilder entries = builder.entryBuilder();
        addBoolean(category, entries, "enable", current.isEnabled(), defaults.isEnabled(),
                ArmorHudMod.temporaryConfig::setEnabled, ArmorHudMod.previewConfig::setEnabled);
        addEnum(category, entries, "anchor", Anchor.class, current.getAnchor(), defaults.getAnchor(),
                ArmorHudMod.temporaryConfig::setAnchor, ArmorHudMod.previewConfig::setAnchor);
        addEnum(category, entries, "side", Side.class, current.getSide(), defaults.getSide(),
                ArmorHudMod.temporaryConfig::setSide, ArmorHudMod.previewConfig::setSide);
        addEnum(category, entries, "offhandSlot", OffhandSlotBehavior.class, current.getOffhandSlotBehavior(), defaults.getOffhandSlotBehavior(),
                ArmorHudMod.temporaryConfig::setOffhandSlotBehavior, ArmorHudMod.previewConfig::setOffhandSlotBehavior);
        addBoolean(category, entries, "pushBossbars", current.getPushBossbars(), defaults.getPushBossbars(),
                ArmorHudMod.temporaryConfig::setPushBossbars, ArmorHudMod.previewConfig::setPushBossbars);
        addBoolean(category, entries, "pushStatusEffectIcons", current.getPushStatusEffectIcons(), defaults.getPushStatusEffectIcons(),
                ArmorHudMod.temporaryConfig::setPushStatusEffectIcons, ArmorHudMod.previewConfig::setPushStatusEffectIcons);
        addBoolean(category, entries, "pushSubtitles", current.getPushSubtitles(), defaults.getPushSubtitles(),
                ArmorHudMod.temporaryConfig::setPushSubtitles, ArmorHudMod.previewConfig::setPushSubtitles);
        addInteger(category, entries, "offsetX", current.getOffsetX(), defaults.getOffsetX(),
                ArmorHudMod.temporaryConfig::setOffsetX, ArmorHudMod.previewConfig::setOffsetX);
        addInteger(category, entries, "offsetY", current.getOffsetY(), defaults.getOffsetY(),
                ArmorHudMod.temporaryConfig::setOffsetY, ArmorHudMod.previewConfig::setOffsetY);
        addEnum(category, entries, "style", Style.class, current.getStyle(), defaults.getStyle(),
                ArmorHudMod.temporaryConfig::setStyle, ArmorHudMod.previewConfig::setStyle);
        addEnum(category, entries, "widgetShown", WidgetShown.class, current.getWidgetShown(), defaults.getWidgetShown(),
                ArmorHudMod.temporaryConfig::setWidgetShown, ArmorHudMod.previewConfig::setWidgetShown);
        addBoolean(category, entries, "reversed", current.isReversed(), defaults.isReversed(),
                ArmorHudMod.temporaryConfig::setReversed, ArmorHudMod.previewConfig::setReversed);
        addBoolean(category, entries, "iconsShown", current.getIconsShown(), defaults.getIconsShown(),
                ArmorHudMod.temporaryConfig::setIconsShown, ArmorHudMod.previewConfig::setIconsShown);
        addBoolean(category, entries, "warningShown", current.isWarningShown(), defaults.isWarningShown(),
                ArmorHudMod.temporaryConfig::setWarningShown, ArmorHudMod.previewConfig::setWarningShown);
        addInteger(category, entries, "minDurabilityValue", current.getMinDurabilityValue(), defaults.getMinDurabilityValue(),
                ArmorHudMod.temporaryConfig::setMinDurabilityValue, ArmorHudMod.previewConfig::setMinDurabilityValue);
        addDouble(category, entries, "minDurabilityPercentage", current.getMinDurabilityPercentage(), defaults.getMinDurabilityPercentage(),
                ArmorHudMod.temporaryConfig::setMinDurabilityPercentage, ArmorHudMod.previewConfig::setMinDurabilityPercentage);
        addFloat(category, entries, "warningIconBobbingIntervalEntry", current.getWarningIconBobbingIntervalMs() / 1000.0F,
                defaults.getWarningIconBobbingIntervalMs() / 1000.0F,
                value -> ArmorHudMod.temporaryConfig.setWarningIconBobbingIntervalMs(value * 1000.0F),
                value -> ArmorHudMod.previewConfig.setWarningIconBobbingIntervalMs(value * 1000.0F));
        return builder.build();
    }

    private static void addBoolean(ConfigCategory category, ConfigEntryBuilder entries, String key,
                                   boolean current, boolean defaultValue, Consumer<Boolean> save, Consumer<Boolean> preview) {
        category.addEntry(entries.startBooleanToggle(label(key), current).setDefaultValue(defaultValue)
                .setSaveConsumer(save).setErrorSupplier(value -> updatePreview(value, preview)).build());
    }

    private static void addInteger(ConfigCategory category, ConfigEntryBuilder entries, String key,
                                   int current, int defaultValue, Consumer<Integer> save, Consumer<Integer> preview) {
        category.addEntry(entries.startIntField(label(key), current).setDefaultValue(defaultValue)
                .setSaveConsumer(save).setErrorSupplier(value -> updatePreview(value, preview)).build());
    }

    private static void addDouble(ConfigCategory category, ConfigEntryBuilder entries, String key,
                                  double current, double defaultValue, Consumer<Double> save, Consumer<Double> preview) {
        category.addEntry(entries.startDoubleField(label(key), current).setDefaultValue(defaultValue)
                .setMin(0.0D).setMax(1.0D).setSaveConsumer(save)
                .setErrorSupplier(value -> updatePreview(value, preview)).build());
    }

    private static void addFloat(ConfigCategory category, ConfigEntryBuilder entries, String key,
                                 float current, float defaultValue, Consumer<Float> save, Consumer<Float> preview) {
        category.addEntry(entries.startFloatField(label(key), current).setDefaultValue(defaultValue)
                .setMin(0.0F).setMax(5.0F).setSaveConsumer(save)
                .setErrorSupplier(value -> updatePreview(value, preview)).build());
    }

    private static <T extends Enum<T>> void addEnum(ConfigCategory category, ConfigEntryBuilder entries, String key,
                                                     Class<T> type, T current, T defaultValue,
                                                     Consumer<T> save, Consumer<T> preview) {
        category.addEntry(entries.startEnumSelector(label(key), type, current).setDefaultValue(defaultValue)
                .setSaveConsumer(save).setErrorSupplier(value -> updatePreview(value, preview)).build());
    }

    private static Component label(String key) {
        return Component.translatable("armorHud.configScreen.setting." + key + ".name");
    }

    private static <T> Optional<Component> updatePreview(T value, Consumer<T> preview) {
        preview.accept(value);
        return Optional.empty();
    }
}
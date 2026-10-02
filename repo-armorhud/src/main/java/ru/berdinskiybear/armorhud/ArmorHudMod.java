package ru.berdinskiybear.armorhud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public final class ArmorHudMod {
    public static final String MOD_ID = "armor_hud";
    public static final String MOD_NAME = "BerdinskiyBear's ArmorHUD";
    public static final Component CONFIG_SCREEN_NAME = Component.translatable("armorHud.configScreen.title");
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final int STEP = 20;
    public static final int SIZE = 22;
    public static final int HOTBAR_OFFSET = 98;
    public static final int OFFHAND_OFFSET = 29;
    public static final int ATTACK_INDICATOR_OFFSET = 23;
    public static final int WARNING_SIZE = 8;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json");
    private static ArmorHudConfig currentConfig = new ArmorHudConfig();
    public static ArmorHudConfig.MutableConfig temporaryConfig = new ArmorHudConfig.MutableConfig();
    public static ArmorHudConfig.MutableConfig previewConfig = new ArmorHudConfig.MutableConfig();

    private ArmorHudMod() {
    }

    public static ArmorHudConfig getCurrentConfig() {
        return currentConfig;
    }

    public static void setCurrentConfig(ArmorHudConfig config) {
        currentConfig = config;
    }

    public static void readCurrentConfig() {
        if (Files.exists(CONFIG_PATH)) {
            try (var reader = Files.newBufferedReader(CONFIG_PATH)) {
                ArmorHudConfig loaded = GSON.fromJson(reader, ArmorHudConfig.class);
                currentConfig = loaded == null ? new ArmorHudConfig() : loaded;
                return;
            } catch (IOException | RuntimeException exception) {
                LOGGER.error("Could not read ArmorHUD config; using defaults", exception);
            }
        }

        currentConfig = new ArmorHudConfig();
        writeCurrentConfig();
    }

    public static void writeCurrentConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (var writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(currentConfig, writer);
            }
        } catch (IOException exception) {
            LOGGER.error("Could not write ArmorHUD config", exception);
        }
    }

    public static List<ItemStack> getArmorItems(Player player) {
        return getArmorItems(player, currentConfig);
    }

    public static List<ItemStack> getArmorItems(Player player, ArmorHudConfig config) {
        Stream<ItemStack> items = Stream.of(
                player.getItemBySlot(EquipmentSlot.FEET),
                player.getItemBySlot(EquipmentSlot.LEGS),
                player.getItemBySlot(EquipmentSlot.CHEST),
                player.getItemBySlot(EquipmentSlot.HEAD)
        );

        return switch (config.getWidgetShown()) {
            case ALWAYS -> items.toList();
            case IF_ANY_PRESENT -> {
                List<ItemStack> armor = items.toList();
                yield armor.stream().allMatch(ItemStack::isEmpty) ? List.of() : armor;
            }
            case NOT_EMPTY -> items.filter(stack -> !stack.isEmpty()).toList();
        };
    }

    public static boolean shouldShowWarning(ItemStack stack) {
        return shouldShowWarning(stack, currentConfig);
    }

    public static boolean shouldShowWarning(ItemStack stack, ArmorHudConfig config) {
        if (stack.isEmpty() || !stack.isDamageableItem()) return false;
        int remaining = stack.getMaxDamage() - stack.getDamageValue();
        double percentage = (double) remaining / stack.getMaxDamage();
        return percentage <= config.getMinDurabilityPercentage()
                || remaining <= config.getMinDurabilityValue();
    }

    public static boolean shouldReserveWarningSpace(List<ItemStack> items) {
        return currentConfig.isWarningShown() && items.stream().anyMatch(ArmorHudMod::shouldShowWarning);
    }

}

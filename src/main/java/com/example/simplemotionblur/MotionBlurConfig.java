package com.example.simplemotionblur;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Tiny settings file: .minecraft/config/simplemotionblur.properties
 */
public final class MotionBlurConfig {
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 10;

    public static boolean enabled = true;
    public static int level = 5;

    private static final Path FILE =
            FabricLoader.getInstance().getConfigDir().resolve("simplemotionblur.properties");

    private MotionBlurConfig() {}

    public static void load() {
        if (!Files.exists(FILE)) {
            save();
            return;
        }
        try (InputStream in = Files.newInputStream(FILE)) {
            Properties props = new Properties();
            props.load(in);
            enabled = Boolean.parseBoolean(props.getProperty("enabled", "true"));
            level = clamp(Integer.parseInt(props.getProperty("level", "5").trim()));
        } catch (IOException | NumberFormatException e) {
            System.err.println("[SimpleMotionBlur] Could not read config, using defaults: " + e);
        }
    }

    public static void save() {
        Properties props = new Properties();
        props.setProperty("enabled", Boolean.toString(enabled));
        props.setProperty("level", Integer.toString(level));
        try (OutputStream out = Files.newOutputStream(FILE)) {
            props.store(out, "Simple Motion Blur - level goes from 1 (light) to 10 (very strong)");
        } catch (IOException e) {
            System.err.println("[SimpleMotionBlur] Could not save config: " + e);
        }
    }

    public static int clamp(int value) {
        return Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, value));
    }
}

package com.example.simplemotionblur;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Runs the blur. All the heavy lifting is done by the post effect JSON + shaders in
 * assets/simplemotionblur/. Each frame we only run 3 cheap fullscreen passes:
 *   1) blend (current frame + previous result) -> "blended"
 *   2) copy "blended" -> "history"  (remembered for next frame)
 *   3) copy "blended" -> the screen
 * Both "history" and "blended" are persistent, so nothing is created per frame.
 */
public final class MotionBlurRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger("SimpleMotionBlur");
    private static final Set<Identifier> EXTERNAL_TARGETS = Set.of(PostEffectProcessor.MAIN);

    private static PostEffectProcessor processor;
    private static int loadedLevel = -1;
    private static boolean failed = false;

    private MotionBlurRenderer() {}

    /** Called by the mixin right after the 3D world has been drawn (before the HUD). */
    public static void apply() {
        if (!MotionBlurConfig.enabled || failed) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) {
            return;
        }

        int level = MotionBlurConfig.level;
        if (processor == null || loadedLevel != level) {
            reset();
            load(client, level);
            if (processor == null) {
                return;
            }
        }

        try {
            processor.render(client.getFramebuffer(), ObjectAllocator.TRIVIAL);
        } catch (Exception e) {
            LOGGER.error("Motion blur crashed while rendering, turning it off until you rejoin.", e);
            failed = true;
        }
    }

    private static void load(MinecraftClient client, int level) {
        Identifier id = Identifier.of(SimpleMotionBlurMod.MOD_ID, "level_" + level);
        try {
            processor = client.getShaderLoader().loadPostEffect(id, EXTERNAL_TARGETS);
            loadedLevel = level;
            if (processor == null) {
                LOGGER.error("Could not load post effect {} (is the file there? did the shader compile?)", id);
                failed = true;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load post effect {}", id, e);
            processor = null;
            failed = true;
        }
    }

    /** Throws away the history so the next frame starts clean (join/leave world, level change). */
    public static void reset() {
        if (processor instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
        processor = null;
        loadedLevel = -1;
        failed = false;
    }
}

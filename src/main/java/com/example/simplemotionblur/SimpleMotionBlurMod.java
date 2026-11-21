package com.example.simplemotionblur;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.text.Text;

public class SimpleMotionBlurMod implements ClientModInitializer {
    public static final String MOD_ID = "simplemotionblur";

    @Override
    public void onInitializeClient() {
        MotionBlurConfig.load();

        // Start with a clean history whenever you join or leave a world.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> MotionBlurRenderer.reset());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> MotionBlurRenderer.reset());

        // /motionblur            -> shows status
        // /motionblur on|off     -> enable / disable
        // /motionblur level 1-10 -> blur strength
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("motionblur")
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(Text.literal(status()));
                            return 1;
                        })
                        .then(ClientCommandManager.literal("on").executes(ctx -> {
                            MotionBlurConfig.enabled = true;
                            MotionBlurConfig.save();
                            MotionBlurRenderer.reset();
                            ctx.getSource().sendFeedback(Text.literal(status()));
                            return 1;
                        }))
                        .then(ClientCommandManager.literal("off").executes(ctx -> {
                            MotionBlurConfig.enabled = false;
                            MotionBlurConfig.save();
                            MotionBlurRenderer.reset();
                            ctx.getSource().sendFeedback(Text.literal(status()));
                            return 1;
                        }))
                        .then(ClientCommandManager.literal("level")
                                .then(ClientCommandManager.argument("value",
                                                IntegerArgumentType.integer(MotionBlurConfig.MIN_LEVEL, MotionBlurConfig.MAX_LEVEL))
                                        .executes(ctx -> {
                                            MotionBlurConfig.level = IntegerArgumentType.getInteger(ctx, "value");
                                            MotionBlurConfig.enabled = true;
                                            MotionBlurConfig.save();
                                            MotionBlurRenderer.reset();
                                            ctx.getSource().sendFeedback(Text.literal(status()));
                                            return 1;
                                        })))));
    }

    private static String status() {
        return "Motion blur: " + (MotionBlurConfig.enabled ? "ON" : "OFF")
                + " (level " + MotionBlurConfig.level + "/" + MotionBlurConfig.MAX_LEVEL + ")";
    }
}
